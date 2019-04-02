/*
 * package com.cloudsherpas.model;
 * 
 * import java.io.IOException; import java.util.ArrayList; import java.util.LinkedList; import
 * java.util.List;
 * 
 * import com.cloudsherpas.GlobalConstants; import
 * com.cloudsherpas.google.api.GoogleCloudStorageApi; import
 * com.google.api.services.storage.Storage; import com.google.api.services.storage.model.Objects;
 * import com.google.api.services.storage.model.StorageObject;
 * 
 * public class BucketItem implements GlobalConstants{
 * 
 * 
 * public static ArrayList<String> getDeletedObjects() throws IOException{ ArrayList<String> results
 * = new ArrayList<String>(); GoogleCloudStorageApi gcsApi = new GoogleCloudStorageApi();
 * gcsApi.init(); Storage.Objects.List listObject = gcsApi.storage.objects().list(DELETED_BUCKET);
 * Objects objects; do { objects = listObject.execute(); listObject.setMaxResults(500L); if (objects
 * != null && objects.getItems() != null && !objects.getItems().isEmpty()) { for (StorageObject
 * object : objects.getItems()) { //String fileNameParams[] = object.getName().split("/");
 * results.add(object.getName()); // AAA5555PW- deleted 2014-03-15/AAA5555PW_100_15032014.pdf
 * //gcsApi.storage.objects().copy(DELETED_BUCKET, object.getName(),
 * INCOMING_BUCKET,fileNameParams[1], null).execute(); //logger.log(Level.INFO, "File \"" +
 * fileNameParams[1] + "\" successfully copied to " + INCOMING_BUCKET + " bucket.");
 * 
 * //gcsApi.storage.objects().delete(DELETED_BUCKET, object.getName()).execute();
 * //logger.log(Level.INFO, "File \"" + object.getName() + "\" successfully deleted from " +
 * ERRORS_BUCKET + " bucket."); } } listObject.setPageToken(objects.getNextPageToken()); } while
 * (null != objects.getNextPageToken()); return results;
 * 
 * }
 * 
 * }
 */