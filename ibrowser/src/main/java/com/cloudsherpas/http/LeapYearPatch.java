package com.cloudsherpas.http;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.InstanceDao;
import com.cloudsherpas.dao.NotificationDao;
import com.cloudsherpas.dao.PatchDao;
import com.cloudsherpas.dao.PatchInstanceDao;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.dao.TradingYearDao;
import com.cloudsherpas.domain.Instance;
import com.cloudsherpas.domain.Patch;
import com.cloudsherpas.domain.PatchInstance;
import com.cloudsherpas.enums.ReportPeriodEnum;
import com.cloudsherpas.utils.DateUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
public class LeapYearPatch extends HttpServlet{
	
	private final InstanceDao instanceDao;
	private final PatchInstanceDao patchInstanceDao;
	private final TradingYearDao tradingYearDao;
	private final ReportDao reportDao;
	private final NotificationDao notificationDao;
	private final DateUtils dateUtils;
	private final PatchDao patchDao;
	private Logger logger;
	
	@Inject
	public LeapYearPatch(InstanceDao instanceDao, PatchInstanceDao patchInstanceDao, TradingYearDao tradingYearDao, ReportDao reportDao,
			NotificationDao notificationDao,DateUtils dateUtils, PatchDao patchDao, Logger logger){
		this.instanceDao = instanceDao;
		this.patchInstanceDao = patchInstanceDao;
		this.tradingYearDao = tradingYearDao;
		this.reportDao = reportDao;
		this.notificationDao = notificationDao;
		this.dateUtils = dateUtils;
		this.patchDao = patchDao;
		this.logger = logger;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		Date date;
		try {
			
			Patch patch = patchDao.getLastPatch();
				
			date = DateUtils.dateFormat2.parse("01102014");
			if(patch==null){
				patch = new Patch();
				patch.setLastAfterDate(date);
				patch.setLastStart(0);
				patchDao.persist(patch);
			}
			List<Date> afterDates = new ArrayList<Date>();
			List<Date> beforeDates = new ArrayList<Date>();
			//dateUtils.getDateRangesForPatch(afterDates, beforeDates);
			
//			List<Instance> instancesEndOfYear = instanceDao.getInstancesByAfterTheDate(ReportPeriodEnum.END_OF_YEAR, date);
//			List<Instance> instancesEndOfHalf = instanceDao.getInstancesByAfterTheDate(ReportPeriodEnum.END_OF_HALF, date);
//			List<Instance> instancesEndOfPeriod = instanceDao.getInstancesByAfterTheDate(ReportPeriodEnum.END_OF_TRADING_PERIOD, date);
//			
//			for(Instance i : instancesEndOfYear){
//				if(!dateUtils.isEndOfYear(new Date(i.getDate().getTime()))) {
//					logger.log(java.util.logging.Level.WARNING, "Patch: Not an EndOfYear report: " + i.getFileName());
//				}
//			}
//			
//			for(Instance i : instancesEndOfHalf){
//				if(!dateUtils.isEndOfHalf(new Date(i.getDate().getTime()))) {
//					logger.log(java.util.logging.Level.WARNING, "Patch: Not an EndOfHalf report: " + i.getFileName());
//				}
//			}
//			
//			for(Instance i : instancesEndOfPeriod){
//				if(!dateUtils.isEndOfTradingPeriod(new Date(i.getDate().getTime()))) {
//					logger.log(java.util.logging.Level.WARNING, "Patch: Not an EndOfTradingPeriod report: " + i.getFileName());
//				}
//			}
			
			

			//for(int k=0; k < afterDates.size(); k++) {
				
				//if(afterDates.get(k).before(patch.getLastAfterDate())) continue;
				

				//logger.log(java.util.logging.Level.WARNING, "Patch: AfterDate : " + DateUtils.dateFormat5.format(afterDates.get(k)) + " to BeforeDate: " + DateUtils.dateFormat5.format(beforeDates.get(k)) );
				
				
				long start=patch.getLastStart(), end=start+500;
				
																						//TODO change in 2 places!!!
				List<Instance> instancesWeekly = instanceDao.getInstancesByAfterTheDate(ReportPeriodEnum.END_OF_HALF, /*afterDates.get(k),beforeDates.get(k),*/ start,end);

				int eoY=0, eoH=0, eoT = 0;
				boolean doStop = false;
				while(!instancesWeekly.isEmpty() && instancesWeekly.size()>0 && !doStop /*&& (eoY<=5 && eoH<=5 && eoT<=5)*/) {

					logger.log(java.util.logging.Level.WARNING, "Patch: instancesWeekly Page start: " + start + " to end: " + end );

					
					List<PatchInstance> instancesToSave = new ArrayList<PatchInstance>();
					List<Instance> instancesToDelete = new ArrayList<Instance>();
					
					
					for(Instance i : instancesWeekly){
						
						if(i.getDate().getTime()<=date.getTime()) {doStop=true; break;}
						
//						if(!dateUtils.isEndOfYear(new Date(i.getDate().getTime()))) {
							
							//logger.log(java.util.logging.Level.WARNING, "Patch: Identified as EndOfYear report: " + i.getFileName());
							
//							Instance existingInstance = instanceDao.getInstanceByFileName(i.getFileName(), ReportPeriodEnum.END_OF_YEAR);
							
//							if(existingInstance == null){
								
//								logger.log(java.util.logging.Level.WARNING, "Patch: Deleting EndOfYear report: " + i.getFileName());
//								instancesToSave.add(getNewPatchInstance(i, ReportPeriodEnum.END_OF_YEAR));
//								instanceDao.delete(i.getKey());
//								instancesToDelete.add(i);
								
//							} else {
								//logger.log(java.util.logging.Level.WARNING, "Patch: Already Exists as EndOfYear report: " + i.getFileName());
//								eoY ++;
//							}
							
//						} 

						
						
						if (!dateUtils.isEndOfHalf(new Date(i.getDate().getTime()))){
								
							//logger.log(java.util.logging.Level.WARNING, "Patch: Identified as EndOfHalf report: " + i.getFileName());

//							Instance existingInstance = instanceDao.getInstanceByFileName(i.getFileName(), ReportPeriodEnum.END_OF_HALF);
//							if(existingInstance == null) {
								logger.log(java.util.logging.Level.WARNING, "Patch: Deleting as EndOfHalf report: " + i.getFileName());
								instancesToSave.add(getNewPatchInstance(i, ReportPeriodEnum.END_OF_HALF));
								instanceDao.delete(i.getKey());

//							} else{
								//logger.log(java.util.logging.Level.WARNING, "Patch: Already Exists as EndOfHalf report: " + i.getFileName());
//								eoH++;
								
//							}
							

						} 
						
//						if(!dateUtils.isEndOfTradingPeriod(new Date(i.getDate().getTime()))){
							
							//logger.log(java.util.logging.Level.WARNING, "Patch: Identified as EndOfTradingPeriod report: " + i.getFileName());

//							Instance existingInstance = instanceDao.getInstanceByFileName(i.getFileName(), ReportPeriodEnum.END_OF_TRADING_PERIOD);
//							if(existingInstance == null) {
//								logger.log(java.util.logging.Level.WARNING, "Patch: Deleting as EndOfTradingPeriod report: " + i.getFileName());
//								instancesToSave.add(getNewPatchInstance(i, ReportPeriodEnum.END_OF_TRADING_PERIOD));
//								instanceDao.delete(i.getKey());

//							} else{
								//logger.log(java.util.logging.Level.WARNING, "Patch: Already Exists as EndOfTradingPeriod report: " + i.getFileName());
//								eoT++;
								
//							}
												
//						}
						
					
						
						
						if(eoY>=5 || eoH>=5 || eoT >=5) {
							//logger.log(java.util.logging.Level.WARNING, "Patch: Hit 5 times, skipping the rest " + DateUtils.dateFormat5.format(afterDates.get(k)));
							//break;
						}

						
					}
					
					if(!instancesToSave.isEmpty()){
						logger.log(java.util.logging.Level.WARNING, "Patch: Total Instances To Save : " + instancesToSave.size());
						logger.log(java.util.logging.Level.WARNING, "Patch: Saving...");
						patchInstanceDao.persistAll(instancesToSave);
						logger.log(java.util.logging.Level.WARNING, "Patch: Completed: ");
						
					}
					
//					if(!instancesToDelete.isEmpty()){
//						logger.log(java.util.logging.Level.WARNING, "Patch: Total Instances To Delete : " + instancesToDelete.size());
//						logger.log(java.util.logging.Level.WARNING, "Patch: Deleting...");
//						instanceDao.deleteAll(instancesToDelete);
//						logger.log(java.util.logging.Level.WARNING, "Patch: Completed: ");
//						
//					}					
					
					if(eoY>=5 || eoH>=5 || eoT >=5) {
						//logger.log(java.util.logging.Level.WARNING, "Patch: Hit 5 times, skipping the rest " + DateUtils.dateFormat5.format(afterDates.get(k)));
						//break;
					}	
					
					//patch.setLastAfterDate(afterDates.get(k));
					patch.setLastStart(start);
					patchDao.persist(patch);
					
					start = end; end=end+500;
					instancesWeekly = instanceDao.getInstancesByAfterTheDate(ReportPeriodEnum.END_OF_HALF,/* afterDates.get(k),beforeDates.get(k),*/ start,end);
					
				}
				
			
				
			//}
			



			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		
//		GoogleCloudStorageApi gcsApi = new GoogleCloudStorageApi(instanceDao, tradingYearDao, reportDao,notificationDao, dateUtils);
//		gcsApi.init();
//		gcsApi.run();
	
	}

	private Instance getNewInstance(Instance existingInstance, ReportPeriodEnum period){
		Instance newInstance = new Instance();
		newInstance.setReportCode(existingInstance.getReportCode());
		newInstance.setGroupCode(existingInstance.getGroupCode());
		newInstance.setDate(existingInstance.getDate());
		newInstance.setPeriod(existingInstance.getPeriod());
		newInstance.setFileName(existingInstance.getFileName());
		newInstance.setFileSize(existingInstance.getFileSize());
		newInstance.setPeriod(period);
		return newInstance;
	}

	private PatchInstance getNewPatchInstance(Instance existingInstance, ReportPeriodEnum period){
		PatchInstance newInstance = new PatchInstance();
		newInstance.setReportCode(existingInstance.getReportCode());
		newInstance.setGroupCode(existingInstance.getGroupCode());
		newInstance.setDate(existingInstance.getDate());
		newInstance.setPeriod(existingInstance.getPeriod());
		newInstance.setFileName(existingInstance.getFileName());
		newInstance.setFileSize(existingInstance.getFileSize());
		newInstance.setPeriod(period);
		return newInstance;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
}