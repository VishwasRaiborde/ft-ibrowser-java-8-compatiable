package com.cloudsherpas.bigquery.mapreduce;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.mapreduce.Mapper;

@SuppressWarnings("serial")
public class EntityMapper extends Mapper<Entity, String, Entity> {

  @Override
  public void map(Entity value) {
    if (value != null) {
      emit(KeyFactory.keyToString(value.getKey()), value);
    }
  }

}
