package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.google.api.GoogleCloudStorageApi;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;
import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

public class FileUploadHandler implements GlobalConstants{
   
	@Inject
	public FileUploadHandler(){
		
	}
	public String get(HttpServletRequest req, HttpServletResponse resp) {
		return null;
	}
	public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException, FileUploadException {
		
		GoogleCloudStorageApi gcsApi = new GoogleCloudStorageApi();
		gcsApi.init();
		
		if (ServletFileUpload.isMultipartContent(req)) {
		    ServletFileUpload fileUpload = new ServletFileUpload();
		    FileItemIterator items = fileUpload.getItemIterator(req);
		    while (items.hasNext()) {
		    	FileItemStream item = items.next();
		        if (!item.isFormField()) {
		        	InputStream inputStream = item.openStream();
		            InputStreamContent mediaContent = new InputStreamContent(item.getContentType(), inputStream);
		            mediaContent.setLength(inputStream.available());
		            StorageObject objectMetadata = new StorageObject()
		            .setName(item.getName())
		  		    .setAcl(ImmutableList.of(
		  		    new ObjectAccessControl().setEntity("allUsers").setRole("READER") //This is for all users without Authenticate
		              //new ObjectAccessControl().setEntity("allAuthenticatedUsers").setRole("READER")
		             ))
		  		  .setContentDisposition("attachment");
		  		
		  		Storage.Objects.Insert insertObject = gcsApi.storage.objects().insert(MESSAGE_OF_THE_DAY_IMAGES, objectMetadata, mediaContent);
		  		insertObject.setName(item.getName());
		  		
		  		if (mediaContent.getLength() > 0) {
		  			insertObject.getMediaHttpUploader().setDirectUploadEnabled(true);
		  		}
		  		insertObject.execute();
		  		
		  		System.out.println("File successfully upload to " + MESSAGE_OF_THE_DAY_IMAGES	+ " bucket.");
		  		String filePath = "http://storage.googleapis.com/"+MESSAGE_OF_THE_DAY_IMAGES+"/"+ item.getName();
		  		String callback = req.getParameter("CKEditorFuncNum");
		  		PrintWriter out = resp.getWriter();
		  		out.println("<script type=\"text/javascript\">");
		  		out.println("window.parent.CKEDITOR.tools.callFunction(" + callback
		  				+ ",'" + filePath + "',''" + ")");
		  		out.println("</script>");
		  		return;
		        }
		    
		    }
		}
	
	}     
}
