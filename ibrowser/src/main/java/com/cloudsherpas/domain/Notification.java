package com.cloudsherpas.domain;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Notification  extends BaseEntity {
	
	@Persistent
	private String fromEmail;
	
	@Persistent
	private String toEmail;
	
	@Persistent
	private String subject;
	
	@Persistent	
	private Text emailBody;
	
	
	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void setEmailBody(String emailBody) {
		if (emailBody == null)
			this.emailBody = null;
		else
			this.emailBody = new Text(emailBody);
	}

	public String getEmailBody() {
		if (this.emailBody == null)
			return null;
		else
			return this.emailBody.getValue();
	}
	
}
