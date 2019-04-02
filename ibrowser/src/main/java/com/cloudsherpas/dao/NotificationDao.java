package com.cloudsherpas.dao;

import java.util.GregorianCalendar;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.Notification;

public class NotificationDao extends BaseDao<Notification> {

  public NotificationDao() {
    super(Notification.class);
  }

  public void saveNotification(String fromEmail, String toEmail, String subject, String emailBody) {
    Notification notification = new Notification();
    notification.setFromEmail(fromEmail);
    notification.setToEmail(toEmail);
    notification.setSubject(subject);
    notification.setEmailBody(emailBody);
    persist(notification);
  }

  public List<Notification> getNotificationsByPeriod(GregorianCalendar date) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Notification.class);
    q.setFilter("createdDate >= :dateParam");
    try {
      List<Notification> list = (List<Notification>) q.execute(date.getTime());
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }
}
