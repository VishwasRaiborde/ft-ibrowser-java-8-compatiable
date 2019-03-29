package com.cloudsherpas.bigquery.mapreduce;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.KindConstants;
import com.cloudsherpas.bigquery.mapreduce.inputs.CsDatastoreInput;
import com.cloudsherpas.enums.EntityTransferStatus;
import com.cloudsherpas.google.api.BigQueryApi;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.Bigquery.Jobs.Insert;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobConfiguration;
import com.google.api.services.bigquery.model.JobConfigurationLoad;
import com.google.api.services.bigquery.model.JobReference;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
//import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.mapreduce.GoogleCloudStorageFileSet;
import com.google.appengine.tools.mapreduce.Input;
import com.google.appengine.tools.mapreduce.MapReduceJob;
import com.google.appengine.tools.mapreduce.MapReduceResult;
import com.google.appengine.tools.mapreduce.MapReduceSettings;
import com.google.appengine.tools.mapreduce.MapReduceSpecification;
import com.google.appengine.tools.mapreduce.Mapper;
import com.google.appengine.tools.mapreduce.Marshaller;
import com.google.appengine.tools.mapreduce.Marshallers;
import com.google.appengine.tools.mapreduce.Output;
import com.google.appengine.tools.mapreduce.Reducer;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.outputs.GoogleCloudStorageFileOutput;
import com.google.appengine.tools.mapreduce.outputs.MarshallingOutput;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.appengine.tools.pipeline.Value;
@SuppressWarnings("serial")
public abstract class BigQueryMapReduceServlet extends HttpServlet implements GlobalConstants{
	protected static final String PROJECT_ID = PROJECT_NAME;
	protected static final String DATASET_ID = GCS_BUCKET_NAME;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//If we haven't transferring entities, the entities transferring functional doesn't work
		for(int i=0; i<5; i++){
            try {
                if (getTransferringEntityCount() == 0) {
                    return;
                }else{
                    break;
                }
            }catch(Exception e){ 
                e.printStackTrace();
     
                try {
                    Thread.sleep(30000);    
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
		
		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new AppPipelineJob());
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.put("pipelineId", pipelineId);
		//IF YOU NEED TEST Mapreduce-Pipeline STATUS, YOU CAN UNCOMMENT BELOW LINE
		redirectToPipelineStatus(req, resp, pipelineId);
		try {
			savePipelineHistory(service, pipelineId, getJobName());
		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	protected MapReduceSettings getSettings() {
		return new MapReduceSettings().setWorkerQueueName("mapreduce-workers")
				.setBucketName(GCS_BUCKET_NAME);
	}
	
	protected abstract String getJobName();
	
	protected abstract Input<Entity> getInput();
	
	protected abstract Reducer<String, Entity, String> getReducer();
	
	protected abstract String getFilenamePattern();
	
	protected abstract String getTableId();
	
	protected abstract List<TableFieldSchema> getFieldsSchema();

	protected Boolean beforeRemoveTable() {
		return false;
	}
	
	protected String getFieldDelimiter() {
		return DEFAULT_DELIMITER;
	}
	
	protected int getCloudStorageFileSetShard() {
		return SHARD;
	}
	
	protected MapReduceSpecification<Entity, String, Entity, String, GoogleCloudStorageFileSet> createMapReduceSpec() {
		Input<Entity> input = getInput();
		Mapper<Entity, String, Entity> mapper = new EntityMapper();
		Marshaller<String> intermediateKeyMarshaller = Marshallers.getStringMarshaller();
		Marshaller<Entity> intermediateValueMarshaller = Marshallers.getSerializationMarshaller();
		Reducer<String, Entity, String> reducer = getReducer();
		Marshaller<String> outputMarshaller = Marshallers.getStringMarshaller();

		Output<String, GoogleCloudStorageFileSet> output = new MarshallingOutput<>(
				new GoogleCloudStorageFileOutput(GCS_BUCKET_NAME, getFilenamePattern(), "text/csv", getCloudStorageFileSetShard()), outputMarshaller); 
		
		return MapReduceSpecification.of(getJobName(), input, mapper,
				intermediateKeyMarshaller, intermediateValueMarshaller,
				reducer, output);
	}

	protected void savePipelineHistory(PipelineService service, String pipelineId, String jobName) throws NoSuchObjectException {
		Entity entity = new Entity(KindConstants.PIPELINE_HISTORY);
		entity.setProperty("pipelineId", pipelineId);
		entity.setProperty("url", "/_ah/pipeline/status.html?root=" + pipelineId);
		entity.setProperty("jobName", jobName);
		entity.setProperty("entityKind", getEntityKind());
		entity.setProperty("status", service.getJobInfo(pipelineId).getJobState().name());
		entity.setProperty("date", new Date());
		
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		datastoreService.put(entity);
	}

	private String getEntityKind() {
		String entityKind = null;
		
		if (getInput() instanceof DatastoreInput) {
			entityKind = ((DatastoreInput)getInput()).getEntityKind();
		} else if (getInput() instanceof CsDatastoreInput) {
			entityKind = ((CsDatastoreInput)getInput()).getEntityKind();
		}
		
		return entityKind;
	}
	
	private int getTransferringEntityCount() {
		if (getEntityKind() == null || getEntityKind().isEmpty()) {
			return 0;
		}
		
		Query query = new Query(getEntityKind());
		Filter filter = new FilterPredicate("status", FilterOperator.EQUAL, EntityTransferStatus.READY_TO_TRANSFER.getCode());
		
		if (getInput() instanceof CsDatastoreInput) {
			query.setFilter(filter);
		}
		
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
//		return datastoreService.prepare(query).countEntities(FetchOptions.Builder.withDefaults());
		return datastoreService.prepare(query).countEntities(FetchOptions.Builder.withLimit(20));
	}
	
	private class AppPipelineJob extends Job0<Void> {
		private static final long serialVersionUID = 1954542676168374323L;
		
		@Override
		public Value<Void> run() throws Exception {
			MapReduceJob<Entity, String, Entity, String, GoogleCloudStorageFileSet> mapReduceJob = new MapReduceJob<>();
			MapReduceSpecification<Entity, String, Entity, String, GoogleCloudStorageFileSet> spec = createMapReduceSpec();
			MapReduceSettings settings = getSettings();

			FutureValue<MapReduceResult<GoogleCloudStorageFileSet>> mapReduceResult = futureCall(mapReduceJob, immediate(spec), immediate(settings));
			
			return futureCall(new GCSFilesToBigQueryJob(), mapReduceResult, immediate(beforeRemoveTable()));
		}

	}
	
	private class GCSFilesToBigQueryJob extends Job2<Void, MapReduceResult<GoogleCloudStorageFileSet>, Boolean> {
		private static final long serialVersionUID = 6277239748168293296L;
		
		@Override
		public Value<Void> run(MapReduceResult<GoogleCloudStorageFileSet> files, Boolean beforeRemoveTable) throws Exception {
			/*List<String> sources = new ArrayList<>();
			for (GcsFilename name : files.getOutputResult().getAllFiles()) {
				sources.add("gs://" + name.getBucketName() + "/" + name.getObjectName());
			}
			
			BigQueryApi api = new BigQueryApi();
			Bigquery service = api.authenticateToBigQuery();
			
			//remove a table with older data before creating the table with new data
			if (beforeRemoveTable != null && beforeRemoveTable.booleanValue()) {
				try {
					service.tables().delete(PROJECT_ID, DATASET_ID, getTableId()).execute();
				} catch(Exception e) {
				}
			}
			
		    Job job = new Job();
		    JobConfiguration config = new JobConfiguration();
		    JobConfigurationLoad loadConfig = new JobConfigurationLoad();
		    config.setLoad(loadConfig);

		    job.setConfiguration(config);

		    // Set where you are importing from (i.e. the Google Cloud Storage paths).
		    loadConfig.setSourceUris(sources);

		    // Describe the resulting table you are importing to:
		    TableReference tableRef = new TableReference();
		    tableRef.setDatasetId(DATASET_ID);
		    tableRef.setTableId(getTableId());
		    tableRef.setProjectId(PROJECT_ID);
		    loadConfig.setDestinationTable(tableRef);
		    
		    TableSchema schema = new TableSchema();
		    schema.setFields(getFieldsSchema());
		    
		    loadConfig.setSchema(schema);
		    loadConfig.setFieldDelimiter(getFieldDelimiter());
		    

		    Insert insert = service.jobs().insert(PROJECT_ID, job);
		    insert.setProjectId(PROJECT_ID);
			JobReference jobRef =  insert.execute().getJobReference();

		    long startTime = System.currentTimeMillis();
		    long elapsedTime;
		    
		    while (true) {
		        Job pollJob = service.jobs().get(PROJECT_ID, jobRef.getJobId()).execute();
		        elapsedTime = System.currentTimeMillis() - startTime;
		        
		        System.out.format("Job status (%dms) %s: %s\n", elapsedTime, jobRef.getJobId(), pollJob.getStatus().getState());
		        
		        if (pollJob.getStatus().getState().equals("DONE")) {
		          break;
		        }
		        // Pause execution for one second before polling job status again, to
		        // reduce unnecessary calls to the BigQUery API and lower overall
		        // application bandwidth.
		        Thread.sleep(1000);
		      }
		    */
			return null;
		}
		
	}
	
	private String getUrlBase(HttpServletRequest req)
			throws MalformedURLException {
		URL requestUrl = new URL(req.getRequestURL().toString());
		String portString = requestUrl.getPort() == -1 ? "" : ":"
				+ requestUrl.getPort();
		return requestUrl.getProtocol() + "://" + requestUrl.getHost()
				+ portString + "/";
	}

	private String getPipelineStatusUrl(String urlBase, String pipelineId) {
		return urlBase + "_ah/pipeline/status.html?root=" + pipelineId;
	}

	private void redirectToPipelineStatus(HttpServletRequest req,
			HttpServletResponse resp, String pipelineId) throws IOException {
		String destinationUrl = getPipelineStatusUrl(getUrlBase(req),
				pipelineId);

		resp.sendRedirect(destinationUrl);
	}
}
