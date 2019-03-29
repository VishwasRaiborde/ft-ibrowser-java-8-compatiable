/*package com.cloudsherpas.domain;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class SavedBOGroup extends BaseEntity {

	@Persistent
	private String heading;
	
	@Persistent
	private String type;

	public Key getHeading() {
		return heading != null ? KeyFactory.stringToKey(heading) : null;
	}

	public void setHeading(Key heading) {
		this.heading = KeyFactory.keyToString(heading);
	}

	public Key getType() {
		return type != null ? KeyFactory.stringToKey(type) : null;
	}

	public void setType(Key type) {
		this.type = KeyFactory.keyToString(type);
	}

		
	
}
*/