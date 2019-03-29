package com.cloudsherpas.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.cloudsherpas.domain.Heading;
import com.cloudsherpas.domain.Instance;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.domain.Type;
import com.cloudsherpas.enums.ReportTypeEnum;
import com.cloudsherpas.google.api.GoogleCloudStorageApi;
import com.cloudsherpas.responses.SelectItem;
import com.cloudsherpas.utils.DateUtils;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;

public class ReportDao extends BaseDao<Report> {
	private final GroupDao groupDao;
	private final UserDao userDao;
	private final HeadingDao headingDao;
	private final InstanceDao instanceDao;
	private final GoogleCloudStorageApi cloudStorageApi;
	public final MemcacheService cache;
	private Logger logger = Logger
			.getLogger(this.getClass().getCanonicalName());

	@Inject
	public ReportDao(UserDao userDao, GroupDao groupDao, HeadingDao headingDao,
			InstanceDao instanceDao, GoogleCloudStorageApi cloudStorageApi,
			MemcacheService cache) {
		super(Report.class);
		this.userDao = userDao;
		this.groupDao = groupDao;
		this.headingDao = headingDao;
		this.instanceDao = instanceDao;
		this.cloudStorageApi = cloudStorageApi;
		this.cache = cache;
	}

	public void persist(Report entity) {
		PersistenceManager pm = getPersistenceManager();
		try {
			if (entity.isNew()) {
				entity.setCreatedDate(new Date());
			}
			entity.setLastUpdatedDate(new Date());
			pm.makePersistent(entity);
		} finally {
			pm.close();
		}
	}

	public List<Report> getDivisionalReport(String headingKey) {
		List<Report> reportList = new ArrayList<Report>();
		PersistenceManager pm = getPersistenceManager();
		List<Key> userGroupsKey = groupDao.getUserGroupsKey(userDao.getCurrentUser());
		if (userGroupsKey == null) {
			logger.log(Level.INFO, "getDivisionalReport User group is NULL");
			return null;
		}
		logger.log(Level.INFO,"user groups: "+userGroupsKey.toString());

		Heading heading = headingDao.get(headingKey);
		logger.log(Level.INFO,"heading: "+heading.toString());
		Query q = pm.newQuery(Report.class);
		q.setFilter("heading == :headingParam && allowedGroupsKey == :allowGroupParam && reportType == :reportTypeParam");
		q.setOrdering("title ASC, lastUpdatedDate DESC");
		try {

			List<Report> list = new ArrayList<Report>();

			// optimization
			int a = userGroupsKey.size() / 10;
			int r = userGroupsKey.size() % 10;
			for (int i = 0; i < a; i++) {
				List<Key> subGroups = userGroupsKey.subList(i * 10 ,(i + 1) * 10 );
				List<Report> subList = (List<Report>) q.execute(heading,subGroups, ReportTypeEnum.DIVISIONAL);
				logger.log(Level.INFO, "queried Report : "+subList.toString());
				if (subList != null && !subList.isEmpty()){
					list.addAll(subList);
				 }
			}
			if (r > 0) {
				List<Key> subGroups = userGroupsKey.subList(a * 10, a * 10 + r);
				List<Report> subList = (List<Report>) q.execute(heading,subGroups, ReportTypeEnum.DIVISIONAL);
				logger.log(Level.INFO, "queried Report 2: "+subList.toString());
				if (subList != null && !subList.isEmpty()){
					list.addAll(subList);
				}
			}

			// end of optimization

			if (list == null || list.isEmpty()) {
				logger.log(Level.INFO, "Divisional report list is empty");
				return null;
			}
			logger.log(Level.INFO, "Divisional report list start :  Time: "+ new Date());
			for (Report report : list) {
				Instance instance = instanceDao.getLastInstanceByCode(report.getCode(), "000");
				if (instance != null) {
					report.setLastReportDateAsString(DateUtils.getDateFormat(instance.getDate()));
					reportList.add(report);
				}
			}
			logger.log(Level.INFO, "Divisional report list end :  Time: "+ new Date());
			return reportList;
		} finally {
			q.closeAll();
			pm.close();
		}

	}

	public List<Report> getReports(String headingKey,ReportTypeEnum reportType, String typeGroupCode) {
		List<Report> reportList = new ArrayList<Report>();
		List<Key> userGroupsKey = groupDao.getUserGroupsKey(userDao.getCurrentUser());
        if(userGroupsKey==null){
        	logger.log(Level.INFO, "getReports User group is NULL");
			return reportList;
        }
		Heading heading = headingDao.get(headingKey);

		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(Report.class);
		q.setFilter("heading == :headingParam && reportType == :typeParam && allowedGroupsKey == :allowedGroupsParam");
		q.setOrdering("title ASC");
		try {
			List<Report> list = new ArrayList<Report>();
			// optimization
			int a = userGroupsKey.size() / 10;
			int r = userGroupsKey.size() % 10;
			for (int i = 0; i < a; i++) {
			  List<Key> subGroups = userGroupsKey.subList(i * 10 ,(i + 1) * 10 );
			  List<Report> subList = (List<Report>) q.execute(heading,reportType,subGroups);
			    if (subList != null && !subList.isEmpty()){
			    	list.addAll(subList);
			    }
			 }
			 if (r > 0) {
			  List<Key> subGroups = userGroupsKey.subList(a * 10, a * 10 + r);
			  List<Report> subList = (List<Report>) q.execute(heading, reportType,subGroups);
			   if (subList != null && !subList.isEmpty()){
				   list.addAll(subList);
			   }
			 }
			  // end of optimization
			
			 if (list == null || list.isEmpty()) {
			    logger.log(Level.INFO, "Branch/Buying report list is empty");
				return reportList;
			}
			logger.log(Level.INFO, "Branch/Buying report list start :  Time: "+ new Date());
			for (Report report : list) {
				report.setKeyAsString();
				Instance instance = instanceDao.getLastInstanceByCode(report.getCode(), typeGroupCode);
				if (instance != null) {
					report.setLastReportDateAsString(DateUtils.getDateFormat(instance.getDate()));
					reportList.add(report);
				}
			}
			logger.log(Level.INFO, "Branch/Buying report list end :  Time: "+ new Date());
			return reportList;
		} finally {
			q.closeAll();
			pm.close();
		}
	}

	public List<Report> getSearchReports(ReportTypeEnum reportType,String typeGroupCode, String codeSearchVal, String titleSearchVal) {
		List<Report> reportList = new ArrayList<Report>();
		List<Key> userGroupsKey = groupDao.getUserGroupsKey(userDao.getCurrentUser());
        
		if(userGroupsKey==null || userGroupsKey.isEmpty()){
        	return reportList;
        }
        
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(Report.class);
		q.setFilter("reportType == :typeParam && allowedGroupsKey == :allowedGroupsParam");
		q.setOrdering("title ASC");
		try {
			List<Report> list = new ArrayList<Report>();
			// optimization
			int a = userGroupsKey.size() / 10;
			int r = userGroupsKey.size() % 10;
			for (int i = 0; i < a; i++) {
				List<Key> subGroups = userGroupsKey.subList(i * 10 ,(i + 1) * 10);
				List<Report> subList = (List<Report>) q.execute(reportType,subGroups);
				if (subList != null && !subList.isEmpty()){
					list.addAll(subList);
				 }
			}
			if (r > 0) {
				List<Key> subGroups = userGroupsKey.subList(a * 10, a * 10 + r);
				List<Report> subList = (List<Report>) q.execute(reportType,subGroups);
				if (subList != null && !subList.isEmpty()){
					list.addAll(subList);
				}
			}
			// end of optimization
			if(list==null || list.isEmpty()){
				logger.log(Level.INFO, "Searching Branch/Buying report is empty");
				return reportList;
			}
			
			logger.log(Level.INFO, "Branch/Buying report list start :  Time: "+ new Date());
			for (Report report : list) {
				if (report.getCode().contains(codeSearchVal) || report.getTitle().contains(titleSearchVal)) {
					Instance instance = instanceDao.getLastInstanceByCode(report.getCode(), typeGroupCode);
					if (instance != null) {
						report.setKeyAsString();
						report.setLastReportDateAsString(DateUtils.getDateFormat(instance.getDate()));
						report.setTypeAsString(report.getReportType().getName());
						report.setGroupCode(instance.getGroupCode());
						reportList.add(report);
					}
				}
			}
			logger.log(Level.INFO, "Branch/Buying report list end :  Time: "+ new Date());
			return reportList;
		} finally {
			q.closeAll();
			pm.close();
		}
	}

	public List<SelectItem> getUserBranches() {
		return getBranchesOrBuyingOffice(ReportTypeEnum.BRANCH);
	}

	public List<SelectItem> getUserBuyingOffices() {
		return getBranchesOrBuyingOffice(ReportTypeEnum.BUYING);
	}

	public List<SelectItem> getBranchesOrBuyingOffice(ReportTypeEnum typeEnum) {

		PersistenceManager pm = getPersistenceManager();
		List<SelectItem> result = new ArrayList<SelectItem>();

		Query q = pm.newQuery(Type.class);
		q.setFilter("type == :reportTypeParam");
		q.setOrdering("groupName ASC");

		try {
			logger.log(Level.INFO, "Drop down list start :  Time: "+ new Date());

			List<Type> types = (List<Type>) q.execute(typeEnum);
			for (Type type : types) {
				SelectItem item = new SelectItem(type.getGroupCode(),type.getGroupName());
				result.add(item);
			}
			logger.log(Level.INFO, "Drop down list end :  Time: " + new Date());
			return result;

		} finally {
			q.closeAll();
			pm.close();
		}
	}

	/*public List<Report> getUserFavouriteReports() {
		PersistenceManager pm = getPersistenceManager();
		List<Key> userFavouriteReports = userDao.getCurrentUser().getFavouriteReportsKey();

		if (userFavouriteReports == null || userFavouriteReports.isEmpty()) {
			return new ArrayList<Report>();
		}

		Query q = pm.newQuery(Report.class);
		q.setFilter("key == :keyParam");
		q.setOrdering("title ASC,lastUpdatedDate DESC");
		try {
			List<Report> list = (List<Report>) q.execute(userFavouriteReports);
			if (list.isEmpty()) {
				return new ArrayList<Report>();
			}
			for (Report report : list) {
				 Instance instance = instanceDao.getLastInstanceByCode(report.getCode(), null);
				 report.setKeyAsString();
				 report.setGroupCode(instance != null ? instance.getGroupCode() : null);
			}
			return list;
		} finally {
			q.closeAll();
			pm.close();
		}
	}*/

	public List<SelectItem> getReportTypeGroups(Report report) {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(Type.class);
		q.setFilter("groupCode == :groupCodeParam  && type == :reportTypeParam");
		q.setOrdering("groupName ASC");

		List<SelectItem> result = new ArrayList<SelectItem>();
		try {
			Set<String> instanceGroupCodes = new HashSet<String>();
			instanceGroupCodes.addAll(instanceDao.getInstanceGroupCodes(report
					.getCode()));
			if (instanceGroupCodes != null && !instanceGroupCodes.isEmpty()) {
				List<Type> types = (List<Type>) q.execute(new ArrayList<String>(instanceGroupCodes),report.getReportType());
				for (Type type : types) {
					result.add(new SelectItem(type.getGroupCode(), type.getGroupName()));
				}
			}
			return result;

		} finally {
			q.closeAll();
			pm.close();
		}

	}

	public List<Report> getDivisionalReports(String code, String title) {
		List<Report> resultList = new ArrayList<Report>();
		PersistenceManager pm = getPersistenceManager();
		List<Key> userGroupsKey = groupDao.getUserGroupsKey(userDao.getCurrentUser());
		if(userGroupsKey==null || userGroupsKey.isEmpty()){
			return null;
		}
		Query q = pm.newQuery(Report.class);
		q.setFilter("allowedGroupsKey == :allowGroupParam && reportType == :reportTypeParam");
		q.setOrdering("title ASC, lastUpdatedDate DESC");
		try {
			List<Report> list = new ArrayList<Report>();
			// optimization
			int a = userGroupsKey.size() / 10;
			int r = userGroupsKey.size() % 10;
			for (int i = 0; i < a; i++) {
				List<Key> subGroups = userGroupsKey.subList(i * 10,(i + 1) * 10);
				List<Report> subList = (List<Report>) q.execute(subGroups,ReportTypeEnum.DIVISIONAL);
				if (subList != null && !subList.isEmpty()){
					list.addAll(subList);
				 }
			}
			if (r > 0) {
				List<Key> subGroups = userGroupsKey.subList(a * 10, a * 10 + r);
				List<Report> subList = (List<Report>) q.execute(subGroups,ReportTypeEnum.DIVISIONAL);
				if (subList != null && !subList.isEmpty()){
					list.addAll(subList);
				}
			}
			// end of optimization
			if(list==null || list.isEmpty()){
				logger.log(Level.INFO, "Searching Divisional report is empty");
				return null;
			}
			
			logger.log(Level.INFO, "Divisional report list start :  Time: "+ new Date());
			for (Report report : list) {
				if (report.getCode().toUpperCase().contains(code) || report.getTitle().toUpperCase().contains(title)) {
					Instance instance = instanceDao.getLastInstanceByCode(report.getCode(), "000");
					if (instance != null) {
						report.setKeyAsString();
						report.setTypeAsString(report.getReportType().getName());
						report.setLastReportDateAsString(DateUtils.getDateFormat(instance.getDate()));
						report.setGroupCode(instance.getGroupCode());
						resultList.add(report);
					}
				}
			}
			logger.log(Level.INFO, "Divisional report list end :  Time: "+ new Date());
			return resultList;
		} finally {
			q.closeAll();
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public void delete(Object key) {
		PersistenceManager pm = getPersistenceManager();
		Transaction txn = pm.currentTransaction();
		try {
			txn.begin();
			Report report = pm.getObjectById(Report.class, key);
			String reportCode = new String(report.getCode());
			pm.deletePersistent(report);
			txn.commit();

			Query q = pm.newQuery(Instance.class);
			q.setFilter("reportCode == :codeParam");
			List<Instance> reportWeeklyInstances = (List<Instance>) q
					.execute(reportCode);
			ArrayList<String> fileNames = new ArrayList<String>();
			if (reportWeeklyInstances != null
					&& !reportWeeklyInstances.isEmpty()) {
				for (Instance instance : reportWeeklyInstances) {
					fileNames.add(instance.getFileName());
					txn.begin();
					pm.deletePersistent(instance);
					txn.commit();
				}
				cloudStorageApi.init();
				cloudStorageApi.removeInstances(fileNames);
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
			pm.close();
		}

	}

	public Report getReportByCode(String code) {
		PersistenceManager pm = getPersistenceManager();
		List<Key> userGroupsKey = groupDao.getUserGroupsKey(userDao.getCurrentUser());
		Query q = pm.newQuery(Report.class);
		q.setFilter("code == :codeParam && allowedGroupsKey == :allowedGroupsParam");
		try {
			List<Report> list = (List<Report>) q.execute(code, userGroupsKey);
			if (list.isEmpty()) {
				return null;
			}
			return list.get(0);
		} finally {
			q.closeAll();
			pm.close();
		}
	}

	public Report getReportsByCode(String code) {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(Report.class);
		q.setFilter("code == :codeParam");
		try {
			List<Report> list = (List<Report>) q.execute(code);
			if (list.isEmpty()) {
				return null;
			}
			return list.get(0);
		} finally {
			q.closeAll();
			pm.close();
		}
	}


	public List<Report> getReportsByLimit(int minLimit, int maxLimit) {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(Report.class);
		q.setRange(minLimit, maxLimit);
		try {
			List<Report> list = (List<Report>) q.execute();
			if (list.isEmpty()) {
				return null;
			}
			return list;
		} finally {
			q.closeAll();
			pm.close();
		}
	}
}
