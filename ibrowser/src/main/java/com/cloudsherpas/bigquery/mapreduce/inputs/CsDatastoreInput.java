// Copyright 2011 Google Inc. All Rights Reserved.
package com.cloudsherpas.bigquery.mapreduce.inputs;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.cloudsherpas.enums.EntityTransferStatus;
import com.cloudsherpas.utils.DateUtils;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.mapreduce.Input;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

/**
 * An input to read entities of a specified kind from the datastore.
 *
 */
public final class CsDatastoreInput extends Input<Entity> {

  private static final Logger logger = Logger.getLogger(CsDatastoreInput.class.getName());
  private static final String SCATTER_RESERVED_PROPERTY = Entity.SCATTER_RESERVED_PROPERTY;
  private static final int SCATTER_ENTITIES_PER_SHARD = 32;
  private static final long serialVersionUID = -3939543473076385308L;
  private static final Comparator<Entity> ENTITY_COMPARATOR = new Comparator<Entity>() {
        @Override
        public int compare(Entity o1, Entity o2) {
          return o1.getKey().compareTo(o2.getKey());
        }
      };

  private final String entityKind;
  private final int shardCount;
  private final String namespace;

  /**
   * @param entityKind entity kind to read from the datastore.
   * @param shardCount number of parallel shards for the input.
   */
  public CsDatastoreInput(String entityKind, int shardCount) {
    this(entityKind, shardCount, null);
  }

  /**
   * @param entityKind entity kind to read from the datastore.
   * @param shardCount number of parallel shards for the input.
   */
  public CsDatastoreInput(String entityKind, int shardCount, String namespace) {
    Preconditions.checkArgument(shardCount > 0, "shardCount must be greater than zero.");
    this.entityKind = checkNotNull(entityKind);
    this.shardCount = shardCount;
    this.namespace = namespace;
  }

  @Override
  public List<CsDatastoreInputReader> createReaders() {
    logger.info("Getting input splits for: " + entityKind);

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Key startKey = getStartKey(datastoreService);
    if (startKey == null) {
      logger.info("No data");
      return Collections.emptyList();
    }

    List<Entity> scatterEntities = retrieveScatterKeys(datastoreService);
    List<CsDatastoreInputReader> result = new ArrayList<>();
    for (Key currentKey : chooseSplitPoints(scatterEntities, shardCount)) {
      addInputReader(result, startKey, currentKey);
      startKey = currentKey;
    }

    // Add in the final split. null is special cased so this split contains [startKey, Infinity).
    addInputReader(result, startKey, null);
    return result;
  }

  private void addInputReader(List<CsDatastoreInputReader> result, Key start, Key end) {
    CsDatastoreInputReader source = new CsDatastoreInputReader(entityKind, start, end, namespace);
    result.add(source);
    logger.info(String.format("Added DatastoreInputSplit %s %s %s", source, start, end));
  }

  public String getEntityKind() {
    return entityKind;
  }

  public int getShardCount() {
    return shardCount;
  }

  public String getNamespace() {
    return namespace;
  }

  private List<Entity> retrieveScatterKeys(DatastoreService datastoreService) {
    // A scatter property is added to 1 out of every X entities (X is currently 512), see:
    // http://code.google.com/p/appengine-mapreduce/wiki/ScatterPropertyImplementation
    //
    // We need to determine #shards - 1 split points to divide entity space into equal shards. We
    // oversample the entities with scatter properties to get a better approximation.
    // Note: there is a region of entities before and after each scatter entity:
    //    |---*------*------*------*------*------*------*---|  * = scatter entity,   - = entity
    // so if each scatter entity represents the region following it, there is an extra region before
    // the first scatter entity. Thus we query for one less than the desired number of regions to
    // account for the this extra region before the first scatter entity
    int desiredNumScatterEntities = (shardCount * SCATTER_ENTITIES_PER_SHARD) - 1;
    Query scatter = createQuery(entityKind, namespace)
        //.addSort(SCATTER_RESERVED_PROPERTY) //TODO 
        //.setKeysOnly() //TODO
        ;
    List<Entity> scatterKeys = datastoreService.prepare(scatter).asList(
        withLimit(desiredNumScatterEntities));
    Collections.sort(scatterKeys, ENTITY_COMPARATOR);
    logger.info("Requested " + desiredNumScatterEntities + " scatter entities, retrieved "
        + scatterKeys.size());
    return scatterKeys;
  }

  @VisibleForTesting
  static Iterable<Key> chooseSplitPoints(List<Entity> scatterKeys, int numShards) {
    // Determine the number of regions per shard based on the actual number of scatter entities
    // found. The number of regions is one more than the number of keys retrieved to account for
    // the region before the first scatter entity. We ensure a minimum of 1 region per shard, since
    // this is the smallest granularity of entity space we can partition on at this stage.
    double scatterRegionsPerShard = Math.max(1.0, (double) (scatterKeys.size() + 1) / numShards);
    logger.info("Using " + scatterRegionsPerShard + " regions per shard");

    // Assuming each region contains the same number of entities (which is not true, but does as
    // the number of regions approaches infinity) assign each shard an equal number of regions
    // (rounded to the nearest scatter key).
    Collection<Key> splitKeys = new ArrayList<>(numShards - 1);
    for (int i = 1; i < numShards; i++) {
      // Since scatterRegionsPerShard is at least one, no two values of i can produce the same
      // splitPoint. We subtract one since the array is 0-indexed, but our calculation starts w/ 1.
      int splitPoint = (int) Math.round(i * scatterRegionsPerShard) - 1;
      // Check to see if we have exhausted the scatter keys.
      if (splitPoint >= scatterKeys.size()) {
        // There were not enough regions to create the requested number of shards, fewer shards
        // will be used. This should occur iff there were too few scatter entities to start with.
        break;
      }
      splitKeys.add(scatterKeys.get(splitPoint).getKey());
    }
    return splitKeys;
  }

  private Key getStartKey(DatastoreService datastoreService) {
    Query ascending = createQuery(entityKind, namespace)
        //.addSort(Entity.KEY_RESERVED_PROPERTY) //TODO
        //.setKeysOnly()
    	;
    Iterator<Entity> ascendingIt = datastoreService.prepare(ascending).asIterator(withLimit(1));
    if (!ascendingIt.hasNext()) {
      return null;
    }
    
    return ascendingIt.next().getKey();
  }

  static Query createQuery(String kind, String namespace) {
	Query q = null;
    if (namespace == null) {
      q = new Query(kind);
      
      GregorianCalendar gFrom = new GregorianCalendar();
      gFrom.setTime(new Date()); 
      gFrom.add(GregorianCalendar.DATE, -1);
      DateUtils.setStartOfDay(gFrom);
      
      GregorianCalendar gTo = new GregorianCalendar();
      gTo.setTime((Date)gFrom.getTime().clone()); 
      DateUtils.setEndOfDay(gTo);
      
      Filter filter = new FilterPredicate("viewDate", 
    		  FilterOperator.GREATER_THAN_OR_EQUAL,gFrom.getTime());
      Filter filter2 = new FilterPredicate("viewDate", 
    		  FilterOperator.LESS_THAN_OR_EQUAL, gTo.getTime());
      filter = CompositeFilterOperator.and(filter, filter2);
      q.setFilter(filter);
      
      return q;
    }
    String ns = NamespaceManager.get();
    try {
      NamespaceManager.set(namespace);
      q = new Query(kind);
      
      Filter filter = new FilterPredicate("status", FilterOperator.EQUAL, EntityTransferStatus.READY_TO_TRANSFER.getCode());
      q.setFilter(filter);
      
      return q;
    } finally {
      NamespaceManager.set(ns);
    }
  }
}
