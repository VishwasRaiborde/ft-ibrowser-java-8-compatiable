package com.cloudsherpas.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.GoogleGroupMemberDao;
import com.cloudsherpas.dao.GroupDao;
import com.cloudsherpas.domain.GoogleGroup;
import com.cloudsherpas.domain.GoogleGroupMember;
import com.cloudsherpas.google.api.DirectoryApi;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.Directory.Groups.List;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.Members;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class CronServlet extends HttpServlet implements GlobalConstants {

	private final GroupDao groupDao;
	private final GoogleGroupMemberDao googleGroupMemberDao;
	private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());


	@Inject
	public CronServlet(GroupDao groupDao,
			GoogleGroupMemberDao googleGroupMemberDao) {
		this.groupDao = groupDao;
		this.googleGroupMemberDao = googleGroupMemberDao;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		logger.log(Level.INFO,"Getting Domain \""+DOMAIN+"\" Groups");
		DirectoryApi directoryApi = new DirectoryApi();
		Directory admin = directoryApi.getAdmin();
		List list = admin.groups().list();
		logger.log(Level.INFO,"Admin List: "+list.toString());
		
		list.setDomain(DOMAIN);
		Groups groups = list.execute();
		logger.log(Level.INFO,"Admin Groups: "+groups.toString());

		java.util.List<GoogleGroup> gGroupList = new ArrayList<GoogleGroup>();
		java.util.List<GoogleGroupMember> googleGroupMemberList = new ArrayList<GoogleGroupMember>();

		logger.log(Level.INFO,"Admin Group List: "+gGroupList.toString());
		logger.log(Level.INFO,"Group Members: "+googleGroupMemberList.toString());

		while (groups != null) {
			for (Group group : groups.getGroups()) {
				String groupName = group.getName();

				
				String parentEmail = group.getEmail().toLowerCase();
				GoogleGroup gGroup = groupDao.getGroupByEmail(parentEmail);
				GoogleGroupMember googleGroupMember = googleGroupMemberDao.getGoogleGroupMemberByParentEmail(parentEmail);

				if(groupName==null || "".equals(groupName.trim()) ||
						(!groupName.toLowerCase().startsWith("_JL Branch:".toLowerCase()) &&
								!groupName.toLowerCase().startsWith("Branch:".toLowerCase()) &&
								!groupName.toLowerCase().startsWith("iBr".toLowerCase()) &&
								!groupName.toLowerCase().startsWith("_JL iBr".toLowerCase()) &&
								!groupName.toLowerCase().startsWith("iBrowser".toLowerCase())
								)){
					
					continue;
				}
				
				
				if (gGroup == null) {
					gGroup = new GoogleGroup();
					gGroup.setCreatedDate(new Date());
				}
				gGroup.setLastUpdatedDate(new Date());
				gGroup.setName(groupName);
				gGroup.setEmail(parentEmail);
				gGroup.setCode(groupName.trim().toUpperCase());
				gGroup.setIsSystem(groupName.contains("iBrowser-"));
				gGroupList.add(gGroup);

				com.google.api.services.admin.directory.Directory.Members.List memberList = admin.members().list(parentEmail);
				
				Members members = memberList.execute();

				java.util.List<String> memberEmailList = new ArrayList<String>();

				if (members != null && !members.isEmpty()
						&& members.getMembers() != null
						&& !members.getMembers().isEmpty()) {
					for (Member member : members.getMembers()) {
						if(member.getEmail()!=null && "GROUP".equals(member.getType())){
							memberEmailList.add(member.getEmail().toLowerCase());							
						}
						
					}
				}
				
				
				if (googleGroupMember == null) {
					googleGroupMember = new GoogleGroupMember();
					googleGroupMember.setCreatedDate(new Date());
				}
				googleGroupMember.setLastUpdatedDate(new Date());
				googleGroupMember.setParentEmail(parentEmail);
				googleGroupMember.setMemberEmails(memberEmailList);
				googleGroupMemberList.add(googleGroupMember);
			}

			if (groups.getNextPageToken() == null) {
				break;
			}

			list.setPageToken(groups.getNextPageToken());
			groups = list.execute();
			logger.log(Level.INFO,"Groups list.execute");

		}
		//googleGroupMemberDao.persistAll(googleGroupMemberList);
		groupDao.persistAll(gGroupList);
		logger.log(Level.INFO,"Groups persistAll");
		//
		if (googleGroupMemberList.size() > 0) {

			// first clean up old parent emails from the list
			for (GoogleGroupMember member : googleGroupMemberList) {
				member.setParentEmails(new HashSet<String>());
			}

			// recursively calculate new parent emails
			for (GoogleGroupMember member : googleGroupMemberList) {
				getParentEmails(member, googleGroupMemberList, 1);
			}

			googleGroupMemberDao.persistAll(googleGroupMemberList);
		}

		resp.getWriter().println((new Gson()).toJson(gGroupList));
		logger.log(Level.INFO,"Group sync completed.");
		
		logger.log(Level.INFO,"Old groups clean up started");
		java.util.List<GoogleGroup> allGroups = groupDao.getAll();
		for(GoogleGroup group: allGroups){
			String groupName = group.getName();
			if(groupName==null || "".equals(groupName.trim()) ||
					(!groupName.toLowerCase().startsWith("_JL Branch:".toLowerCase()) &&
							!groupName.toLowerCase().startsWith("Branch:".toLowerCase()) &&
							!groupName.toLowerCase().startsWith("iBr".toLowerCase()) &&
							!groupName.toLowerCase().startsWith("_JL iBr".toLowerCase()) &&
							!groupName.toLowerCase().startsWith("iBrowser".toLowerCase())
							)){
				
				GoogleGroupMember member = googleGroupMemberDao.getGoogleGroupMemberByParentEmail(group.getEmail());
				logger.log(Level.INFO,"delete: " + group.getName());
				groupDao.delete(group.getKey());
				if (member!=null) googleGroupMemberDao.delete(member.getKey());
				
			}			
		}
		logger.log(Level.INFO,"Old group cleanup completed.");		
		

		
	}

	
	public Set<String> getParentEmails(GoogleGroupMember member,
			java.util.List<GoogleGroupMember> list, int level) {
		if (member.getVisited())
			return member.getParentEmails();
		Set<String> result = new HashSet<String>();
		if (level >= 10) {
			logger.log(Level.INFO,"getParentEmails exiting due to recursive endless loop.");
			return result; // to prevent endless loop
		}
		for (GoogleGroupMember parent : list) {

			for (String child : parent.getMemberEmails()) {

				if (child.equals(member.getParentEmail())) {

					result.add(parent.getParentEmail());
					if (parent.getVisited()) {
						result.addAll(parent.getParentEmails());
					} else {
						result.addAll(getParentEmails(parent, list, level + 1));
					}

				}
			}
		}

		member.setParentEmails(result);
		member.setVisited(true);

		return result;

	}

}
