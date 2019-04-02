package com.cloudsherpas.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.InstanceDao;
import com.cloudsherpas.dao.NotificationDao;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.domain.Instance;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.enums.DeletionPeriodEnum;
import com.cloudsherpas.enums.ReportPeriodEnum;
import com.cloudsherpas.google.api.GoogleCloudStorageApi;
import com.cloudsherpas.utils.DateUtils;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
public class CleanupAgentServlet extends HttpServlet implements GlobalConstants {

  private final ReportDao reportDao;
  private final InstanceDao instanceDao;
  public static GregorianCalendar currentYearStartDate;
  private final NotificationDao notificationDao;
  private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

  @Inject
  public CleanupAgentServlet(ReportDao reportDao, InstanceDao instanceDao,
      NotificationDao notificationDao) {
    this.reportDao = reportDao;
    this.instanceDao = instanceDao;
    this.notificationDao = notificationDao;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    ArrayList<String> errorFiles = new ArrayList<String>();
    Date currentDate = new Date();

    GregorianCalendar month1 = new GregorianCalendar();
    month1.setTime(currentDate);
    month1.add(Calendar.MONTH, -1);
    GregorianCalendar month2 = new GregorianCalendar();
    month2.setTime(currentDate);
    month2.add(Calendar.MONTH, -2);
    GregorianCalendar month3 = new GregorianCalendar();
    month3.setTime(currentDate);
    month3.add(Calendar.MONTH, -3);
    GregorianCalendar week1 = new GregorianCalendar();
    week1.setTime(currentDate);
    week1.add(Calendar.DATE, -7);
    GregorianCalendar week2 = new GregorianCalendar();
    week2.setTime(currentDate);
    week2.add(Calendar.DATE, -7 * 2);
    GregorianCalendar week3 = new GregorianCalendar();
    week3.setTime(currentDate);
    week3.add(Calendar.DATE, -7 * 3);
    GregorianCalendar day1 = new GregorianCalendar();
    day1.setTime(currentDate);
    day1.add(Calendar.DATE, -1);

    List<Instance> instances = new ArrayList<Instance>();
    List<Report> reports = new ArrayList<Report>();
    int minLimit = 0, maxLimit = 100;
    Date date = null;

    reports = reportDao.getReportsByLimit(minLimit, maxLimit);
    while (reports != null) {

      for (Report report : reports) {

        int start = 0, end = 1000;

        Date dateBefore = null;

        if (DeletionPeriodEnum.MONTH_1.equals(report.getDeletionPeriod())) {
          dateBefore = month1.getTime();
        } else if (DeletionPeriodEnum.MONTH_2.equals(report.getDeletionPeriod())) {
          dateBefore = month2.getTime();
        } else if (DeletionPeriodEnum.MONTH_3.equals(report.getDeletionPeriod())) {
          dateBefore = month3.getTime();
        } else if (DeletionPeriodEnum.WEEK_1.equals(report.getDeletionPeriod())) {
          dateBefore = week1.getTime();
        } else if (DeletionPeriodEnum.WEEK_2.equals(report.getDeletionPeriod())) {
          dateBefore = week2.getTime();
        } else if (DeletionPeriodEnum.WEEK_3.equals(report.getDeletionPeriod())) {
          dateBefore = week3.getTime();
        } else if (DeletionPeriodEnum.DAY_1.equals(report.getDeletionPeriod())) {
          dateBefore = day1.getTime();
        } else {
          dateBefore = month3.getTime();
        }

        instances = instanceDao.getInstancesByRangeForDelete(report.getCode(), dateBefore, start,
            end);
        while (instances != null) {

          for (Instance instance : instances) {

            if (ReportPeriodEnum.DAILY.equals(instance.getPeriod())) {

              if (removeInstanceFromBucket(instance)) {
                instanceDao.delete(instance.getKey());
                logger.log(Level.INFO, "Daily Instance successfull daleted from Datastore");
              } else {
                errorFiles.add(instance.getFileName());
              }
            } else if (ReportPeriodEnum.WEEKLY.equals(instance.getPeriod())) {

              // check if there are EndOfYear/EndOfPeriod reports
              // for this instance
              if (instanceDao.hasEndOfPeriodInstances(instance.getFileName())) {
                // If there are do not move the file to delete
                // bucket...,
                // ...just delete the instance record from db
                instanceDao.delete(instance.getKey());
                logger.log(Level.INFO, "Weekly Instance successfull daleted from Datastore");
              } else {

                if (removeInstanceFromBucket(instance)) {
                  instanceDao.delete(instance.getKey());
                  logger.log(Level.INFO, "Daily Instance successfull daleted from Datastore");
                } else {
                  errorFiles.add(instance.getFileName());
                }

              }
            }

          }
          instances = instanceDao.getInstancesByRangeForDelete(report.getCode(), dateBefore, start,
              end);
        }

      }
      reports = reportDao.getReportsByLimit(minLimit = maxLimit, maxLimit = maxLimit + 100);
    }

    if (!errorFiles.isEmpty()) {
      StringBuilder body = new StringBuilder(
          "Hi iBrowser administrator,\nThere are one or more report instances could not delete :\n");
      for (String file : errorFiles) {
        body.append(file).append("\n");
        logger.log(Level.INFO, "Error file " + file);
      }
      notificationDao.saveNotification(FROM_EMAIL, TO_EMAIL, "Clean agent run failed",
          body.toString());
    }
  }

  private boolean removeInstanceFromBucket(Instance instance) {
    GoogleCloudStorageApi cloudStorageApi = new GoogleCloudStorageApi();
    if (GoogleCloudStorageApi.storage == null) {
      cloudStorageApi.init();
    }
    String deletedDate = DateUtils.dateFormat3.format(new Date());
    String filePath = instance.getGroupCode() + "/" + instance.getReportCode() + "/"
        + instance.getFileName();
    try {
      Storage.Objects.Get getObject = GoogleCloudStorageApi.storage.objects().get(REPORTS_BUCKET,
          filePath);
      StorageObject storageObject = getObject.execute();
      GoogleCloudStorageApi.storage.objects().copy(REPORTS_BUCKET, storageObject.getName(),
          DELETED_BUCKET,
          (instance.getReportCode() + "- deleted " + deletedDate + "/" + instance.getFileName()),
          null).execute();
      logger.log(Level.INFO, "File \"" + instance.getFileName() + "\" successfully cop	ied to "
          + DELETED_BUCKET + " bucket.");

      GoogleCloudStorageApi.storage.objects().delete(REPORTS_BUCKET, filePath).execute();
      logger.log(Level.INFO, "File \"" + instance.getFileName() + "\" successfully deleted from "
          + REPORTS_BUCKET + " bucket.");
    } catch (IOException e) {
      e.getMessage();
      return false;
    }
    return true;
  }

}
