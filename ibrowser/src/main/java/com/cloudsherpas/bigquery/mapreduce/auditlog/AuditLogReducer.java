package com.cloudsherpas.bigquery.mapreduce.auditlog;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.utils.DateUtils;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.mapreduce.Reducer;
import com.google.appengine.tools.mapreduce.ReducerInput;

@SuppressWarnings({"serial","unchecked"})
public class AuditLogReducer extends Reducer<String, Entity, String> implements GlobalConstants {

private String getFileHeader(){
StringBuffer sb = new StringBuffer()
.append("\"Access Date\"").append(DEFAULT_DELIMITER)
.append("\"Access Time\"").append(DEFAULT_DELIMITER)
.append("\"Access Date(UK)\"").append(DEFAULT_DELIMITER)
.append("\"Access Time(UK)\"").append(DEFAULT_DELIMITER)
.append("\"User\"").append(DEFAULT_DELIMITER)
.append("\"Branch\"").append(DEFAULT_DELIMITER)
.append("\"Report Name\"").append(DEFAULT_DELIMITER)
.append("\"Report Code\"").append(DEFAULT_DELIMITER)
.append("\"OU Number\"").append(DEFAULT_DELIMITER)
.append("\"Report Save Date\"").append(DEFAULT_DELIMITER)
.append("\"Report File Name\"").append(DEFAULT_DELIMITER)
.append("\"Report File Size\"").append(DEFAULT_DELIMITER)
.append("\"Heading\"").append(DEFAULT_DELIMITER)
.append("\"Type\"").append(DEFAULT_DELIMITER)
.append("\"Kept for\"").append(DEFAULT_DELIMITER)
.append("\"Frequency\"").append(DEFAULT_DELIMITER)
.append("\"Device\"").append(DEFAULT_DELIMITER)
.append("\"User Groups\"").append(DEFAULT_DELIMITER)
.append("\n");

return sb.toString();
}

@Override
public void reduce(String key, ReducerInput<Entity> values) {
MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
if (key == null || key.isEmpty() || values == null) {
return;
}
SimpleDateFormat UKFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
UKFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));

while (values.hasNext()) {
StringBuffer sb = new StringBuffer();
if(cache.get("pipelineId")!=null){
sb.append(getFileHeader());
cache.delete("pipelineId");
}
Entity value = values.next();
String UKDate = UKFormat.format(value.getProperty("viewDate")).split(" ")[0];
String UKTime = UKFormat.format(value.getProperty("viewDate")).split(" ")[1];
sb
.append("\"").append((DateUtils.getDateFormat(value.getProperty("viewDate"),DateUtils.dateFormat6))).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append((DateUtils.getDateFormat(value.getProperty("viewDate"),DateUtils.dateFormat7))).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(UKDate).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(UKTime).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("email")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("userBranch")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("title")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("code")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("groupCode")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append((DateUtils.getDateFormat(value.getProperty("reportDate"),DateUtils.dateFormat6))).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("fileName")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("fileSize")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("heading")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("type")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("deletion")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("frequency")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append(value.getProperty("deviceType")).append("\"").append(DEFAULT_DELIMITER)
.append("\"").append((value.getProperty("userGroups") !=null ? ((Text)value.getProperty("userGroups")).getValue() : "")).append("\"").append(DEFAULT_DELIMITER)
.append("\n");

System.out.println(sb.toString());
emit(sb.toString());
}
}
}

