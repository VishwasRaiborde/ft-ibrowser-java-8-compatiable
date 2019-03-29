package com.cloudsherpas.guice;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.cloudsherpas.bigquery.mapreduce.auditlog.AuditLogMapReduceServlet;
import com.cloudsherpas.google.api.GoogleCloudStorageApi;
import com.cloudsherpas.http.CleanupAgentServlet;
import com.cloudsherpas.http.CronServlet;
import com.cloudsherpas.http.GoogleGroupQueueServlet;
import com.cloudsherpas.http.LeapYearPatch;
import com.cloudsherpas.http.LeapYearPatch2;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CsServicesModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CronServlet.class).asEagerSingleton();
		bind(GoogleGroupQueueServlet.class).asEagerSingleton();
		bind(GoogleCloudStorageApi.class).asEagerSingleton();
		bind(CleanupAgentServlet.class).asEagerSingleton();
		bind(LeapYearPatch.class).asEagerSingleton();
		bind(LeapYearPatch2.class).asEagerSingleton();
		/*bind(EntityDeleteServlet.class).asEagerSingleton();
		bind(TradingYearServlet.class).asEagerSingleton();*/
		bind(AuditLogMapReduceServlet.class).asEagerSingleton();
	}

	@Provides
	@Singleton
	public PersistenceManagerFactory provideEntityManagerFactory() {
		return JDOHelper.getPersistenceManagerFactory("transactions-optional");
	}
	@Provides
	@Singleton
	public DatastoreService provideDatastoreServiceFactory() {
		return DatastoreServiceFactory.getDatastoreService();
	}

	@Provides
	public UserService provideUserService() {
		return UserServiceFactory.getUserService();
	}
	
	@Provides
	public MemcacheService provideMemcacheService() {
		return MemcacheServiceFactory.getMemcacheService();
	}

	@Provides
	public BlobstoreService provideBlobstoreService() {
		return BlobstoreServiceFactory.getBlobstoreService();
	}

}
