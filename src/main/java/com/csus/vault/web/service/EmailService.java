package com.csus.vault.web.service;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class EmailService {
	
	private Session getSession() {
		Properties property = new Properties();
		property.put("mail.smtp.host", "smtp.gmail.com");
		property.put("mail.smtp.auth", "true");
		property.put("mail.smtp.port", "587");
		property.put("mail.smtp.starttls.enable", "true");
		
		Session session = Session.getInstance(property, new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
									return new PasswordAuthentication("s.shweta.87@gmail.com","1601Anniversary");
							}
						});
		return session;
	}

	public void sendEmailContainingThePrivateKey(byte[] data, String email) {
		//String receiver = email;
		String receiver = "s.shweta.87@gmail.com";
		String from = "s.shweta.87@gmail.com";
		
		System.out.println("Before the session");

		// Get the session object
		Session session = getSession();

		// Save the encoded Private key to a file in format <User_email>_priv.txt
		File file = new File("KeyPair/privateKey_" + email);
		
		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
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
			Transport trans = session.getTransport("smtp");
            trans.connect("smtp.gmail.com", 587, "s.shweta.87@gmail.com", "1601Anniversary");
			Transport.send(message, message.getAllRecipients());
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void sendEmailAuthorizeUserToRegister(byte[] privateKey, String userEmail, VaultWillDetail will) {
		//String receiver = email;
		String receiver = "s.shweta.87@gmail.com";
		String from = "s.shweta.87@gmail.com";

		System.out.println("Before the session");

		// Get the session object
		Session session = getSession();

		// Save the encoded Private key to a file in format <User_email>_priv.txt
		File file = new File("KeyPair/privateKey_" + userEmail);

		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
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
			Transport.send(message);
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void sendEmailToOwnerToSendWillContentToRequestor(String ownerEmail, VaultUser user, int willId) {
		//String receiver = email;
		String receiver = "s.shweta.87@gmail.com";
		String from = "s.shweta.87@gmail.com";

		System.out.println("Before the session");

		// Get the session object
		Session session = getSession();

		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			message.setSubject("Welcome to Digital Vault");
			message.setText("A view request was made by "+ user.getUser_firstName() + " " + user.getUser_lastName() +"for the will# "+ willId
					+ "\n IMPORTANT NOTE:  Please provide the user with the will content on email: " + user.getUserEmail());

			// Send message
			Transport.send(message);
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}