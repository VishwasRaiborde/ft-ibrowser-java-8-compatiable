package com.cloudsherpas.domain;

import java.util.List;
import java.util.Set;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;

@PersistenceCapable
public class GoogleGroupMember extends BaseEntity{

	@Persistent
	private Set<String> parentEmails;
	
	@Persistent
	private String parentEmail;
	
	@Persistent
	private List<String> memberEmails;
	
	@NotPersistent
	private Boolean visited = Boolean.FALSE;
	
	public String getParentEmail() {
		return parentEmail;
	}

	public void setParentEmail(String parentEmail) {
		this.parentEmail = parentEmail;
	}

	public Set<String> getParentEmails() {
		return parentEmails;
	}

	public void setParentEmails(Set<String> parentEmails) {
		this.parentEmails = parentEmails;
	}
	public List<String> getMemberEmails() {
		return memberEmails;
	}

	public void setMemberEmails(List<String> memberEmails) {
		this.memberEmails = memberEmails;
	}

	public Boolean getVisited() {
		return visited;
	}

	public void setVisited(Boolean visited) {
		this.visited = visited;
	}
	
	

}
