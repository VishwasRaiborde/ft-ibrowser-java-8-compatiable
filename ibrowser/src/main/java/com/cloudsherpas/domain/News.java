package com.cloudsherpas.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class News extends BaseEntity {
	
	@Persistent
	private String title;
	
	@Persistent
	private Text description;
	
	@Persistent
	private Date date;
	
	@NotPersistent
	private String dateAsString;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		if(!"".equals(description) && description!=null){
			return description.getValue();
		}
		return null;
	}

	public void setDescription(String description) {
			this.description = new Text(description);		
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getDateAsString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if (dateAsString == null && date!=null) {
			dateAsString = dateFormat.format(date);
		}
		return dateAsString;
	}
	public void setDateAsString(String dateAsString) {
		this.dateAsString = dateAsString;
	}
}
