package com.cloudsherpas.guice;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.bigquery.mapreduce.auditlog.AuditLogMapReduceServlet;
import com.cloudsherpas.http.AuditLogCronServlet;
import com.cloudsherpas.http.CleanUpInstanceCronServlet;
import com.cloudsherpas.http.CleanupAgentServlet;
import com.cloudsherpas.http.CronServlet;
import com.cloudsherpas.http.CustomServlet;
import com.cloudsherpas.http.FetchingDeletedInstanceCronServlet;
import com.cloudsherpas.http.FetchingGoogleGroupCronServlet;
import com.cloudsherpas.http.FetchingReportInstanceCronServlet;
import com.cloudsherpas.http.GoogleCloudStorageServlet;
import com.cloudsherpas.http.GoogleGroupQueueServlet;
import com.cloudsherpas.http.LogoutServlet;
import com.cloudsherpas.http.SendNotificationCronServlet;
import com.cloudsherpas.http.SendNotificationServlet;
import com.cloudsherpas.http.SyncUsersGroups;
import com.cloudsherpas.http.admin.AdminAuthFilter;
import com.cloudsherpas.http.admin.AdminMvcFilter;
import com.cloudsherpas.http.app.AppAuthFilter;
import com.cloudsherpas.http.app.AppMvcFilter;
import com.google.inject.servlet.ServletModule;

public class CsServletModule extends ServletModule {

  @Override
  protected void configureServlets() {
    filter(GlobalConstants.ADMIN_ROOT + "*").through(AdminAuthFilter.class);
    filter(GlobalConstants.ADMIN_ROOT + "*").through(AdminMvcFilter.class);

    filter(GlobalConstants.APP_ROOT + "*", GlobalConstants.TASK_QUEUE + "*")
        .through(AppAuthFilter.class);
    filter(GlobalConstants.APP_ROOT + "*").through(AppMvcFilter.class);

    serve("/logout").with(LogoutServlet.class);

    // Creating system group to google
    serve(GlobalConstants.TASK_QUEUE + "/group").with(GoogleGroupQueueServlet.class);
    // Fetching google group to app

    serve("/fetching-google-groups-cron").with(FetchingGoogleGroupCronServlet.class);
    serve("/fetching-google-groups").with(CronServlet.class);

    serve("/fetching-report-instances-cron").with(FetchingReportInstanceCronServlet.class);
    serve("/fetching-report-instances").with(GoogleCloudStorageServlet.class);

//	    serve("/leap-year-patch-cron").with(LeapYearPatchCronServlet.class); // Were used as one-off patch
//	    serve("/leap-year-patch").with(LeapYearPatch.class);

//	    serve("/leap-year-patch-cron2").with(LeapYearPatchCronServlet2.class);// Were used as one-off patch
//	    serve("/leap-year-patch2").with(LeapYearPatch2.class);

    serve("/clean-up-instances-cron").with(CleanUpInstanceCronServlet.class);
    serve("/clean-up-instances").with(CleanupAgentServlet.class);

    serve("/fetching-deleted-instances-cron").with(FetchingDeletedInstanceCronServlet.class);

    serve("/send-notifications-cron").with(SendNotificationCronServlet.class);
    serve("/send-notifications").with(SendNotificationServlet.class);

    serve("/bigquery-audit-logs-cron").with(AuditLogCronServlet.class);
    serve("/bigquery-audit-logs").with(AuditLogMapReduceServlet.class);

    serve("/find-dublicate-groups").with(CustomServlet.class);

    serve("/sync-users-groups").with(SyncUsersGroups.class);

  }

}
