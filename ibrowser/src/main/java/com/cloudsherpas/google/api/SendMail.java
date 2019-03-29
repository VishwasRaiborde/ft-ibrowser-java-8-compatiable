package com.cloudsherpas.google.api;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.cloudsherpas.GlobalConstants;

public class SendMail implements GlobalConstants {
	
	public static void sendEmail(String fromEmail,String toEmail, String subject, String messageBody) {
		Properties props = new Properties();
		props.put("mail.host", "smtp.google.com");
		props.put("mail.transport.protocol", "smtp");
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(true);

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(fromEmail));
			
			String[] recipients = toEmail.split("[,]");
			List<InternetAddress> addresses = Arrays.asList(
					new InternetAddress(recipients[0]), new InternetAddress(recipients[1]));
			
			msg.addRecipients(Message.RecipientType.TO, addresses.toArray(new InternetAddress[]{}));
			msg.setSubject(subject);
			msg.setText(messageBody);
			Transport.send(msg);
		} catch (AddressException addressException) {
			addressException.printStackTrace();
		} catch (MessagingException messageException) {
			messageException.printStackTrace();
		}
	}
}
