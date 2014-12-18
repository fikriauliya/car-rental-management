package jp.co.worksap.roster.utilities;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailServices {
	public static void sendEmail(String to, String subject, String content) {
		// Sender's email ID needs to be mentioned
		String from = "no-reply@crm.com";

		// Assuming you are sending email from localhost
		String host = "smtp.mandrillapp.com";

		// Get system properties
		Properties properties = System.getProperties();

		int port = 587;
		String username = "pahlevi.fikri.auliya@gmail.com";
		String password = "FJWyMXmmtIIpuQ87pqbv-w";

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.port", String.valueOf(port));
		properties.setProperty("mail.smtp.user", username);
		properties.setProperty("mail.smtp.auth", "true");

		final Session session = Session.getInstance(properties, null);
		session.setPasswordAuthentication(new URLName("smtp", host, -1, null, username, null),
				new PasswordAuthentication(username, password));

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

			// Set Subject: header field
			message.setSubject(subject);

			// Send the actual HTML message, as big as you like
			message.setContent(content, "text/html");

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
