package com.cloudsherpas.domain;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.BaseEntity;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class AppUser extends BaseEntity {

	@Persistent
	private String email;
	
	@Persistent
	private List<String> groups = new ArrayList<String>();

	/*@Persistent
	private List<Key> favouriteReportsKey;*/
	
	@Persistent
	private String buyingOfficeCode;
	
	@NotPersistent
	private Boolean isAdmin = Boolean.FALSE;
	
	@NotPersistent
	private String userBranch;
	
	public String getBuyingOfficeCode() {
		return buyingOfficeCode;
	}

	public void setBuyingOfficeCode(String buyingOfficeCode) {
		this.buyingOfficeCode = buyingOfficeCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	/*public List<Key> getFavouriteReportsKey() {
		if (favouriteReportsKey == null) {
			favouriteReportsKey = new ArrayList<Key>();
		}
		return favouriteReportsKey;
	}
	public void setFavouriteReportsKey(List<Key> favouriteReportsKey) {
		this.favouriteReportsKey = favouriteReportsKey;
	}*/
	
	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
    
	public String getUserBranch() {
		return userBranch;
	}

	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}

	public boolean isAdmin() {
		if (groups != null && !groups.isEmpty()) {
			for (String groupName : groups) {
				if (groupName.toUpperCase().contains(GlobalConstants.ADMIN_GROUP)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
