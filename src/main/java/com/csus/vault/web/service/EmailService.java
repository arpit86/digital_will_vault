package com.csus.vault.web.service;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class EmailService {

	public void sendEmailContainingThePrivateKey(byte[] data, String email) {
		String to = email;
		// String to = "s.shweta.87@gmail.com";
		String from = "s.shweta.87@gmail.com";
		String host = "localhost";// or IP address

		System.out.println("Before the session");

		// Get the session object
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);

		// Save the encoded Private key to a file in format <User_email>_priv.txt
		File file = new File("KeyPair/privateKey_" + email);
		
		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("Welcome to Digital Vault");
			message.setText("Please save the attached file securely on your computer.\n"
					+ "IMPORTANT NOTE:  The will uploaded can only be read with help of this file.\n"
					+ "ONCE THIS FILE IS CORRUPTED, THE WILL NO LONGER BE AVAILABLE OR RETRIEVED.");
			// Attaching the file containing encoded Private key
			MimeBodyPart mBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			mBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			mBodyPart.setDataHandler(new DataHandler(source));
			mBodyPart.setFileName("privateKey");
			multipart.addBodyPart(mBodyPart);
			message.setContent(multipart);

			// Send message
			System.out.println("The email sent was:\n" + message.toString());

			/*
			 * Run telnet before uncommenting the below code. We need to enable telnet
			 * client if connection is refused.
			 */
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void sendEmailAuthorizeUserToRegister(byte[] privateKey, String userEmail, VaultWillDetail will) {
		String to = userEmail;
		// String to = "s.shweta.87@gmail.com";
		String from = "s.shweta.87@gmail.com";
		String host = "localhost";// or IP address

		System.out.println("Before the session");

		// Get the session object
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);

		// Save the encoded Private key to a file in format <User_email>_priv.txt
		File file = new File("KeyPair/privateKey_" + userEmail);

		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("Welcome to Digital Vault");
			message.setText("Please save the attached file securely on your computer.\n"
					+ "IMPORTANT NOTE:  Please register to the Digital Vault Application.\n"
					+ "YOU HAVE BEEN AUTHORIZED TO VIEW THE WILL #" + will.getWillId());
			// Attaching the file containing encoded Private key
			MimeBodyPart mBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			mBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			mBodyPart.setDataHandler(new DataHandler(source));
			mBodyPart.setFileName("privateKey");
			multipart.addBodyPart(mBodyPart);
			message.setContent(multipart);

			// Send message
			System.out.println("The email sent was:\n" + message.toString());

			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void sendEmailToOwnerToSendWillContentToRequestor(String ownerEmail, VaultUser user, int willId) {
		String to = ownerEmail;
		// String to = "s.shweta.87@gmail.com";
		String from = "s.shweta.87@gmail.com";
		String host = "localhost";// or IP address

		System.out.println("Before the session");

		// Get the session object
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);

		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("Welcome to Digital Vault");
			message.setText("A view request was made by "+ user.getUser_firstName() + " " + user.getUser_lastName() +"for the will# "+ willId
					+ "\n IMPORTANT NOTE:  Please provide the user with the will content on email: " + user.getUserEmail());

			// Send message
			System.out.println("The email sent was:\n" + message.toString());

			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}