package com.cloudsherpas.http;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.NotificationDao;
import com.cloudsherpas.domain.Notification;
import com.cloudsherpas.google.api.SendMail;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
public class SendNotificationServlet extends HttpServlet{
  
	private final NotificationDao notificationDao;
	
	@Inject
	public SendNotificationServlet(NotificationDao notificationDao){
		this.notificationDao = notificationDao;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Date currentDate = new Date();
		GregorianCalendar oneDayAgo = new GregorianCalendar();
		oneDayAgo.setTime(currentDate);
		oneDayAgo.add(GregorianCalendar.DATE, -1);
		
	    List<Notification> notifications = notificationDao.getNotificationsByPeriod(oneDayAgo);
	    for(Notification email: notifications){
		  SendMail.sendEmail(email.getFromEmail(), email.getToEmail(),email.getSubject(),email.getEmailBody());
	    }
	}
		
}
