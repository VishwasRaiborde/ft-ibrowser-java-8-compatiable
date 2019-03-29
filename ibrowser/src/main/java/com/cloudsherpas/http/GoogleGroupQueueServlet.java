package com.cloudsherpas.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.log.Log;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.GroupDao;
import com.cloudsherpas.domain.GoogleGroup;
import com.cloudsherpas.google.api.DirectoryApi;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.Directory.Groups.List;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.inject.Inject;

public class GoogleGroupQueueServlet extends HttpServlet implements GlobalConstants {
	private final GroupDao groupDao;
	
	@Inject
	public GoogleGroupQueueServlet(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String key = req.getParameter("key");
		GoogleGroup gg = groupDao.get(key);
		
		try {
			DirectoryApi directoryApi = new DirectoryApi();
			Directory admin = directoryApi.getAdmin();

			List list = admin.groups().list();
			list.setDomain(DOMAIN);
			Groups groups = list.execute();
			
			boolean hasTestGroup = false;
			while (groups != null) {
				for (Group group : groups.getGroups()) {
					Log.info(group.getName() + "; " + group.getEmail());
					if (group.getName().equals(gg.getName())) {
						hasTestGroup = true;
						break;
					}
				}
				if (!hasTestGroup) {
					Group group = new Group();
					group.setName(gg.getName());
					group.setDescription(gg.getDescription());
					group.setEmail(gg.getName().trim().replace(" ", "_") + "@" + DOMAIN);
					directoryApi.getAdmin().groups().insert(group).execute();
					directoryApi.getAdmin().batch();
				}
				
				if (groups.getNextPageToken() == null) {
					break;
				}
				
				list.setPageToken(groups.getNextPageToken());
				groups = list.execute();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
