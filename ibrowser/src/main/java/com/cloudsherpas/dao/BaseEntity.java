package com.cloudsherpas.dao;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

//@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class BaseEntity implements Serializable {
  
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Key key;
	
	@Persistent
	protected Date createdDate;
	
	@Persistent
	protected Date lastUpdatedDate;
	
	@NotPersistent
	protected String keyAsString;

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
	public String getKeyAsString() {
		if (keyAsString == null && key!=null) {
			keyAsString = KeyFactory.keyToString(key);
		}
		return keyAsString;
	}

	public void setKeyAsString() {
		keyAsString = KeyFactory.keyToString(key);
	} 
	public boolean isNew() {
	    return getKey() == null;
	}
	
}
