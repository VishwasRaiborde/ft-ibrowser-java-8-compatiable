package com.cloudsherpas.google.api;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.InstanceDao;
import com.cloudsherpas.dao.NotificationDao;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.dao.TradingYearDao;
import com.cloudsherpas.domain.Instance;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.domain.TradingYear;
import com.cloudsherpas.enums.FrequencyEnum;
import com.cloudsherpas.enums.ReportPeriodEnum;
import com.cloudsherpas.utils.DateUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.appengine.api.datastore.Text;

public class GoogleCloudStorageApi implements GlobalConstants {

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String FILE_NAME_PATTERN = "^[A-Z]{3}[A-Z0-9]{4}[A-Z0-9][DW]_[0-9]{3,}_[0-9]{0,8}[.]{1}[a-zA-Z]{0,}";
	public static final String KEY_PASSWORD = "notasecret";
	public static Storage storage;
	private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
	public List<Instance> instances;
	private InstanceDao instanceDao;
	private TradingYearDao tradingYearDao;
	private ReportDao reportDao;
	private NotificationDao notificationDao;
	private DateUtils dateUtils;
	public  DecimalFormat defaultScaleFormat = new DecimalFormat(",##0.00");
	

	public GoogleCloudStorageApi() {

	}

	public GoogleCloudStorageApi(InstanceDao instanceDao, TradingYearDao tradingYearDao, ReportDao reportDao,
			NotificationDao notificationDao, DateUtils dateUtils) {
		this.instanceDao = instanceDao;
		this.tradingYearDao = tradingYearDao;
		this.reportDao = reportDao;
		this.notificationDao = notificationDao;
		this.dateUtils = dateUtils;
	}
	
	public void init() {
		try {
			logger.log(Level.INFO, "GoogleCloudStorageApi init method started");
			instances = new ArrayList<Instance>();
			List<String> SCOPE = Arrays
					.asList("https://www.googleapis.com/auth/admin.directory.group",
							"https://www.googleapis.com/auth/admin.directory.orgunit.readonly",
							"https://www.googleapis.com/auth/admin.directory.user.readonly",
							"https://www.googleapis.com/auth/devstorage.full_control");

			HttpTransport transport = new NetHttpTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			GoogleCredential CREDENTIAL = new GoogleCredential.Builder()
					.setTransport(transport)
					.setJsonFactory(jsonFactory)
					.setServiceAccountId(SERVICE_ACCOUNT_ID)
					.setServiceAccountScopes(SCOPE)
					.setServiceAccountPrivateKeyFromP12File(
							new File(SERVICE_ACCOUNT_PKCS12_FILE_PATH)).build();
			storage = new Storage.Builder(HTTP_TRANSPORT, JSON_FACTORY,
					CREDENTIAL).setApplicationName(PROJECT_NAME).build();

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.log(Level.INFO, "GoogleCloudStorageApi init method ended");
	}

	public void run() throws IOException {
		logger.log(Level.INFO, "GoogleCloudStorageApi run method started");
		Storage.Objects.List listObject = storage.objects().list(INCOMING_BUCKET);
		Date afterFifteenMin = DateUtils.addDaysToTime(new Date(), 14, 40);
		ArrayList<String> noInsranceFiles = new ArrayList<String>();
		ArrayList<String> duplicateFiles = new ArrayList<String>();
		ArrayList<String> errorFiles = new ArrayList<String>();
		Objects objects;
		do {
			objects = listObject.execute();
			if (objects != null && objects.getItems() != null && !objects.getItems().isEmpty()) {
				for (StorageObject object : objects.getItems()) {
					try {
						String name = object.getName();
						logger.log(Level.INFO, "GoogleCloudStorageApi run method. FileName: " + name);
						String[] strings = name.split("_");
						//DDD2222PW_007_09042014.pdf
						if (name.matches(FILE_NAME_PATTERN)) {
							Report reportByCode = reportDao.getReportsByCode(strings[0]);
							ReportPeriodEnum reportPeriod = ReportPeriodEnum.WEEKLY;
							
							if (reportByCode==null){
								noInsranceFiles.add(object.getName());
							} else if (FrequencyEnum.DAILY.equals(reportByCode.getFrequency())) {
								reportPeriod = ReportPeriodEnum.DAILY;
							}
							//09042014.pdf
							//String date = strings[2].split(".pdf")[0];
							String date = strings[2].split("\\.")[0];
							Date parsedDate = DateUtils.dateFormat2.parse(date);
							TradingYear tradingYear = tradingYearDao.getTradingYearByDate(parsedDate);
							
//							DateUtils dateUtils = new DateUtils(tradingYear.getStartDate());
							Instance instance = createInstance(strings,	reportPeriod, object);
							
							if (instance.isNew()) {
								instances.add(instance);
							} else {
								duplicateFiles.add(object.getName());
							}
							
							//This is for only daily report functional 
							/*if (dateUtils.isEndOfWeek(instance.getDate()) && ReportPeriodEnum.DAILY.equals(reportPeriod)) {
								reportPeriod = ReportPeriodEnum.WEEKLY;
							}*/
							
							if (dateUtils.isEndOfYear(new Date(instance.getDate().getTime())) && ReportPeriodEnum.WEEKLY.equals(reportPeriod)) {
//								instances.add(createInstance(strings, ReportPeriodEnum.END_OF_TRADING_PERIOD, object));
//								instances.add(createInstance(strings, ReportPeriodEnum.END_OF_HALF, object));
								instances.add(createInstance(strings, ReportPeriodEnum.END_OF_YEAR,	object));
							}
							
							if (dateUtils.isEndOfHalf(new Date(instance.getDate().getTime())) && ReportPeriodEnum.WEEKLY.equals(reportPeriod)) {
//								instances.add(createInstance(strings, ReportPeriodEnum.END_OF_TRADING_PERIOD, object));
								instances.add(createInstance(strings, ReportPeriodEnum.END_OF_HALF, object));
							} 
							if (dateUtils.isEndOfTradingPeriod(new Date(instance.getDate().getTime())) && ReportPeriodEnum.WEEKLY.equals(reportPeriod)) {
								instances.add(createInstance(strings, ReportPeriodEnum.END_OF_TRADING_PERIOD, object));
							}
							// move file to report bucket,remove from incoming
							moveFileToReportBucket(object);
							
							logger.log(Level.INFO, "GoogleCloudStorageApi run method. moveFileToReportBucket(" + name + ")");
						} else {
							try {
								errorFiles.add(object.getName());
								moveFileToErrorBucket(object);
								logger.log(Level.INFO, "GoogleCloudStorageApi run method. moveFileToErrorBucket(" + name + "). No report for this code.");
							} catch (Exception e) {
								continue;
							}
						}

					} catch (Exception e) {
						logger.log(Level.SEVERE, "GoogleCloudStorageApi run method. " + e.getMessage());
						errorFiles.add(object.getName());
						e.printStackTrace();
						try {
							moveFileToErrorBucket(object);
							logger.log(Level.SEVERE, "GoogleCloudStorageApi run method. moveFileToErrorBucket("+object.getName()+")" + e.getMessage());
						} catch (Exception e1) {
							continue;
						}
					}
					
					//persist instance
					instanceDao.persistAll(instances);
					instances.clear();
					Date now = new Date();
					if(now.after(afterFifteenMin)){
						break;
					}
				}
			}
			listObject.setPageToken(objects.getNextPageToken());
		} while (null != objects.getNextPageToken());
		if (!noInsranceFiles.isEmpty()) {
			StringBuilder body = new StringBuilder("Hi iBrowser administrator,\nThere are one or more report instances imported, for which matching report needs to be created in the system:\n");
			for (String file : noInsranceFiles) {
				body.append(file).append("\n");
				logger.log(Level.INFO, "No insrance file "+ file);
			}
			notificationDao.saveNotification(FROM_EMAIL, TO_EMAIL,"Report fetching notification", body.toString());
			 //SendMail.sendEmail(TO_EMAIL, "Report fetching notification", body.toString());
			
		}
		if (!errorFiles.isEmpty()) {
			StringBuilder body = new StringBuilder("Hi iBrowser administrator,\nThere are one or more error files imported:\n");
			for (String file : errorFiles) {
				body.append(file).append("\n");
				logger.log(Level.INFO, "Error file "+ file);
			}
			notificationDao.saveNotification(FROM_EMAIL, TO_EMAIL,"Report fetching notification", body.toString());
			 //SendMail.sendEmail(TO_EMAIL, "Report fetching notification", body.toString());
		}
		if (!duplicateFiles.isEmpty()) {
			StringBuilder body = new StringBuilder("Hi iBrowser administrator,\nThere are one or more duplicate files imported:\n");
			for (String file : duplicateFiles) {
				body.append(file).append("\n");
				logger.log(Level.INFO, "Dublicate file "+ file);
			}
			notificationDao.saveNotification(FROM_EMAIL, TO_EMAIL,"Report fetching notification", body.toString());
			//SendMail.sendEmail(TO_EMAIL, "Report fetching notification", body.toString());
		}
		logger.log(Level.INFO, "GoogleCloudStorageApi run method successfull ended");
	}

	private Instance createInstance(String[] strings, ReportPeriodEnum period,StorageObject object) throws ParseException {
		String date = strings[2].split("\\.")[0];
		Instance etpInstance = instanceDao.getInstanceByFileName(object.getName(),period);
		if (etpInstance == null) {
			boolean isMB = false;
			Integer objectSize = object.getSize().intValue();
			Double size =  objectSize.doubleValue()/1024;
			if(size>1024){
				size =  size/1024;
				isMB = true;
			}
			defaultScaleFormat.setRoundingMode(RoundingMode.HALF_UP);
			String sizeStr = defaultScaleFormat.format(size);
			etpInstance = new Instance();
			etpInstance.setReportCode(strings[0]);
			etpInstance.setGroupCode(strings[1]);
			etpInstance.setDate(DateUtils.dateFormat2.parse(date));
			etpInstance.setPeriod(period);
			etpInstance.setFileName(object.getName());
			etpInstance.setFileSize(isMB?sizeStr+"MB":sizeStr+"KB");
		}
		return etpInstance;
	}

   private void moveFileToReportBucket(StorageObject object) throws IOException {
		String[] strings = object.getName().split("_");
		for(int i = 0; i<5; i++) {
			try {
				storage.objects().copy(INCOMING_BUCKET, object.getName(), REPORTS_BUCKET, (strings[1] + "/" + strings[0] + "/" + object.getName()), object).execute();
				logger.log(Level.INFO, "File \"" + object.getName()	+ "\" successfully copied to " + REPORTS_BUCKET	+ " bucket.");
				storage.objects().delete(INCOMING_BUCKET, object.getName()).execute();
				logger.log(Level.INFO, "File \"" + object.getName()	+ "\" successfully deleted from " + INCOMING_BUCKET	+ " bucket.");
				break;
			} catch (Exception e) {
				logger.log(Level.INFO, "Error encountered in moving files to Report Bucket. "+object.getName()+". Retrying ("+(i+1)+")");
				if (i>4) { break; }
			}
		}
	}

	private void moveFileToErrorBucket(StorageObject object) throws IOException {
		
		storage.objects().copy(INCOMING_BUCKET, object.getName(), ERRORS_BUCKET,object.getName(), object).execute();
		logger.log(Level.INFO, "File \"" + object.getName()	+ "\" successfully copied to " + ERRORS_BUCKET	+ " bucket.");	
	   	
		storage.objects().delete(INCOMING_BUCKET, object.getName()).execute();
		logger.log(Level.INFO, "File \"" + object.getName()+ "\" successfully copied from " + INCOMING_BUCKET + " bucket.");	
	}

	public void removeInstances(ArrayList<String> fileNames) {
		if (fileNames != null && !fileNames.isEmpty()) {
			try {
				String deletedDate = DateUtils.dateFormat3.format(new Date());
				for (String fileName : fileNames) {
					String[] strings = fileName.split("_");
					String filePath = strings[1] + "/" + strings[0] + "/"+ fileName;
					
					Storage.Objects.Get getObject = storage.objects().get(REPORTS_BUCKET,filePath);
					StorageObject storageObject = getObject.execute();
					storage.objects().copy(REPORTS_BUCKET, storageObject.getName(), DELETED_BUCKET,(strings[0]+"- deleted "+ deletedDate+"/"+fileName), null).execute();
					logger.log(Level.INFO, "File \"" + fileName+ "\" successfully copied to " + DELETED_BUCKET+ " bucket.");
							
					storage.objects().delete(REPORTS_BUCKET, filePath).execute();
					logger.log(Level.INFO, "File \"" + fileName+ "\" successfully deleted from "+ REPORTS_BUCKET + " bucket.");
						  	
			 }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public Storage getStorage() {
		return storage;
	}
	 
	public Boolean hasFileInBucket(String bucketName,String filePath) throws IOException {
		Storage.Objects.Get getObject = storage.objects().get(bucketName,filePath);
		logger.log(Level.INFO,"Start checking : has file exist in "+bucketName+filePath);
		try{
			StorageObject storageObject = getObject.execute();
			if (storageObject != null && !storageObject.isEmpty()) {
				logger.log(Level.INFO,"File exist");
				return Boolean.TRUE;			
			}
		}catch(Exception e){
			logger.log(Level.INFO,"There are no files in "+bucketName);
			return Boolean.FALSE;
		}
		return Boolean.FALSE;			
	}
	
}
