package com.cloudsherpas.google.api;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudsherpas.GlobalConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;

public class DirectoryApi implements GlobalConstants {
	
    private JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final List<String> SCOPES = Arrays.asList(
                "https://www.googleapis.com/auth/admin.directory.group", 
                "https://www.googleapis.com/auth/admin.directory.user.readonly");

    private Directory admin;
    private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
    
    public DirectoryApi() {
        try {

            GoogleCredential credential =
                    new GoogleCredential.Builder()
                            .setTransport(HTTP_TRANSPORT)
                            .setJsonFactory(JSON_FACTORY)
                            .setServiceAccountId(SERVICE_ACCOUNT_ID)
                            .setServiceAccountUser(ACCOUNT_USER)
                            .setServiceAccountScopes(SCOPES)
                            .setServiceAccountPrivateKeyFromP12File(new File(this.getClass().getClassLoader().getResource(SERVICE_ACCOUNT_PKCS12_FILE_PATH).getPath())).build();

            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            admin = new Directory.Builder(httpTransport, jsonFactory, credential)
                            .setApplicationName("iBrowser")
                            .setHttpRequestInitializer(credential).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

	}
    
    public Directory getAdmin() {
    	return admin;
    }
    
    public boolean isAdminUser(String email) {
    	try {
			com.google.api.services.admin.directory.Directory.Groups.List list = admin.groups().list();
			list.setUserKey(email);
			Groups groups = list.execute();
			for (Group group : groups.getGroups()) {
				if (group.getName().toUpperCase().contains(ADMIN_GROUP)) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    public List<String> getUserGroups(String email) {
    	List<String> ugl = new ArrayList<String>();
    	
    	try {
    		com.google.api.services.admin.directory.Directory.Groups.List list = admin.groups().list();
    		list.setUserKey(email);
    		Groups groups = list.execute();
    		for (Group group : groups.getGroups()) {
    			ugl.add(group.getName());
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return ugl;
    }
    
    public String checkGroupMember(String email) {
    	logger.log(Level.INFO, "IN checkGroupMember");
    	String userGroup = "";
    	
    	try {
    		com.google.api.services.admin.directory.Directory.Groups.List list = admin.groups().list();
    		list.setUserKey(email);
    		Groups groups = list.execute();
    		for (Group group : groups.getGroups()) {
    			userGroup = group.getName();
    			logger.log(Level.INFO, "Group Name: " + userGroup);
    			if(userGroup==null || "".equals(userGroup.trim()) || (!userGroup.toLowerCase().startsWith("_JL Branch:".toLowerCase()) &&
    					!userGroup.toLowerCase().startsWith("Branch:".toLowerCase()) && !userGroup.toLowerCase().startsWith("iBr".toLowerCase()) &&
    					!userGroup.toLowerCase().startsWith("_JL iBr".toLowerCase()) && !userGroup.toLowerCase().startsWith("iBrowser".toLowerCase()))){
    						continue;
    			}
    			
    			com.google.api.services.admin.directory.Directory.Members.Get item = admin.members().get(group.getEmail(), email);
    			logger.log(Level.INFO, "item: " + item);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return "";
    }
    
    public void delete(String reportGroup , String email) {
    	try {
    		com.google.api.services.admin.directory.Directory.Groups.List list = admin.groups().list();
    		list.setDomain(DOMAIN);
    		Groups groups = list.execute();
    		for (Group group : groups.getGroups()) {
    			if(group.getName().equals(reportGroup)){
    				admin.groups().delete(group.getId()).execute();
    				break;
    			}
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    
    public void getMembers(String reportGroup, String email) {
    	try {
    		com.google.api.services.admin.directory.Directory.Members.List list = admin.members().list(reportGroup);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
