package com.cloudsherpas.http;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
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
public class LeapYearPatch2 extends HttpServlet{
	
	private final InstanceDao instanceDao;
	private final PatchInstanceDao patchInstanceDao;
	private final TradingYearDao tradingYearDao;
	private final ReportDao reportDao;
	private final NotificationDao notificationDao;
	private final DateUtils dateUtils;
	private final PatchDao patchDao;
	private Logger logger;
	
	@Inject
	public LeapYearPatch2(InstanceDao instanceDao, PatchInstanceDao patchInstanceDao, TradingYearDao tradingYearDao, ReportDao reportDao,
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
		
				long start=patch.getLastStart(), end=start+500;
				
				
				List<PatchInstance> patchInstances = patchInstanceDao.getPatchInstancesByAfterTheDate(ReportPeriodEnum.WEEKLY, /*afterDates.get(k),beforeDates.get(k),*/ start,end);

				int eoY=0, eoH=0, eoT = 0;
				boolean doStop = false;
				while(!patchInstances.isEmpty() && patchInstances.size()>0 && !doStop /*&& (eoY<=5 && eoH<=5 && eoT<=5)*/) {

					logger.log(java.util.logging.Level.WARNING, "Patch: instancesWeekly Page start: " + start + " to end: " + end );

					
					List<Instance> instancesToSave = new ArrayList<Instance>();
					
					
					for(PatchInstance i : patchInstances){
						
						//instancesToSave.add(getNewInstance(i));
						
						PersistenceManager pm = instanceDao.getPersistenceManager();
						Query q = pm.newQuery(Instance.class);
						q.setFilter("fileName == :fileNameParam && period == :periodParam");
						q.setOrdering("date DESC");
						try {
							List<Instance> list = (List<Instance>) q.execute(i.getFileName(), i.getPeriod());
							if (list !=null && !list.isEmpty() && list.size()>1) {
								Instance duplicate = list.get(0);
								logger.log(java.util.logging.Level.WARNING, "Patch: Deleting duplicate record : " + i.getFileName());

								instanceDao.delete(duplicate.getKey());
							}
							
						} finally {
							q.closeAll();
							pm.close();
						}
						

						
					}
					
					if(!instancesToSave.isEmpty()){
						logger.log(java.util.logging.Level.WARNING, "Patch: Total Instances To Save : " + instancesToSave.size());
						logger.log(java.util.logging.Level.WARNING, "Patch: Saving...");
						instanceDao.persistAll(instancesToSave);
						logger.log(java.util.logging.Level.WARNING, "Patch: Completed: ");
						
					}
					
					if(eoY>=5 || eoH>=5 || eoT >=5) {
						//logger.log(java.util.logging.Level.WARNING, "Patch: Hit 5 times, skipping the rest " + DateUtils.dateFormat5.format(afterDates.get(k)));
						//break;
					}	
					
					//patch.setLastAfterDate(afterDates.get(k));
					patch.setLastStart(start);
					patchDao.persist(patch);
					
					start = end; end=end+500;
					patchInstances = patchInstanceDao.getPatchInstancesByAfterTheDate(ReportPeriodEnum.WEEKLY, /*afterDates.get(k),beforeDates.get(k),*/ start,end);
					
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
	
	private Instance getNewInstance(PatchInstance existingInstance){
		Instance newInstance = new Instance();
		newInstance.setReportCode(existingInstance.getReportCode());
		newInstance.setGroupCode(existingInstance.getGroupCode());
		newInstance.setDate(existingInstance.getDate());
		newInstance.setPeriod(existingInstance.getPeriod());
		newInstance.setFileName(existingInstance.getFileName());
		newInstance.setFileSize(existingInstance.getFileSize());
		newInstance.setPeriod(existingInstance.getPeriod());
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