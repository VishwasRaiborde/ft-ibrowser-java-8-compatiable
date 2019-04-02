package com.cloudsherpas.bigquery.mapreduce.auditlog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.KindConstants;
import com.cloudsherpas.bigquery.mapreduce.BigQueryMapReduceServlet;
import com.cloudsherpas.bigquery.mapreduce.inputs.CsDatastoreInput;
import com.cloudsherpas.utils.DateUtils;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.Input;
import com.google.appengine.tools.mapreduce.Reducer;

@SuppressWarnings("serial")
public class AuditLogMapReduceServlet extends BigQueryMapReduceServlet implements GlobalConstants {

  @Override
  protected String getJobName() {
    return "Audit log transfer to the GCS";
  }

  @Override
  protected Input<Entity> getInput() {
    // return new DatastoreInput(KindConstants.AUDIT_LOG, SHARD);
    return new CsDatastoreInput(KindConstants.AUDIT_LOG, SHARD);
  }

  @Override
  protected Reducer<String, Entity, String> getReducer() {
    return new AuditLogReducer();
  }

  @Override
  protected String getFilenamePattern() {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, -1);
    String dateStr = DateUtils.dateFormat2.format(calendar.getTime());

    return GCS_BUCKET_NAME + "_" + dateStr + ".csv";
  }

  @Override
  protected String getTableId() {
    return "audit_log";
  }

  @Override
  protected List<TableFieldSchema> getFieldsSchema() {
    List<TableFieldSchema> fields = new ArrayList<>();

    TableFieldSchema fieldKey = new TableFieldSchema();
    fieldKey.setName("key");
    fieldKey.setType("STRING");

    TableFieldSchema fieldEmail = new TableFieldSchema();
    fieldEmail.setName("email");
    fieldEmail.setType("STRING");

    TableFieldSchema fieldTitle = new TableFieldSchema();
    fieldTitle.setName("title");
    fieldTitle.setType("STRING");

    TableFieldSchema fieldCode = new TableFieldSchema();
    fieldCode.setName("code");
    fieldCode.setType("STRING");

    TableFieldSchema fieldGroupCode = new TableFieldSchema();
    fieldGroupCode.setName("groupCode");
    fieldGroupCode.setType("STRING");

    TableFieldSchema fieldHeading = new TableFieldSchema();
    fieldHeading.setName("heading");
    fieldHeading.setType("STRING");

    TableFieldSchema fieldType = new TableFieldSchema();
    fieldType.setName("type");
    fieldType.setType("STRING");

    TableFieldSchema fieldDeletion = new TableFieldSchema();
    fieldDeletion.setName("deletion");
    fieldDeletion.setType("STRING");

    TableFieldSchema fieldFrequency = new TableFieldSchema();
    fieldFrequency.setName("frequency");
    fieldFrequency.setType("STRING");

    TableFieldSchema fieldReportDate = new TableFieldSchema();
    fieldReportDate.setName("reportDate");
    fieldReportDate.setType("TIMESTAMP");

    TableFieldSchema fieldViewDate = new TableFieldSchema();
    fieldViewDate.setName("viewDate");
    fieldViewDate.setType("TIMESTAMP");

    TableFieldSchema fieldDeviceType = new TableFieldSchema();
    fieldDeviceType.setName("deviceType");
    fieldDeviceType.setType("STRING");

    TableFieldSchema fieldFileName = new TableFieldSchema();
    fieldFileName.setName("fileName");
    fieldFileName.setType("STRING");

    TableFieldSchema fieldFileSize = new TableFieldSchema();
    fieldFileSize.setName("fileSize");
    fieldFileSize.setType("STRING");

    TableFieldSchema fieldUserBranch = new TableFieldSchema();
    fieldUserBranch.setName("userBranch");
    fieldUserBranch.setType("STRING");

    TableFieldSchema fieldUserGroups = new TableFieldSchema();
    fieldUserGroups.setName("userGroups");
    fieldUserGroups.setType("STRING");

    fields.add(fieldKey);
    fields.add(fieldEmail);
    fields.add(fieldTitle);
    fields.add(fieldCode);
    fields.add(fieldGroupCode);
    fields.add(fieldHeading);
    fields.add(fieldType);
    fields.add(fieldDeletion);
    fields.add(fieldFrequency);
    fields.add(fieldReportDate);
    fields.add(fieldViewDate);
    fields.add(fieldDeviceType);
    fields.add(fieldFileName);
    fields.add(fieldFileSize);
    fields.add(fieldUserBranch);
    fields.add(fieldUserGroups);
    return fields;
  }

}
