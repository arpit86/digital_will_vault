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
	
	private Session getSession() {
		Properties property = new Properties();
		property.put("mail.smtp.auth", "true");
		property.put("mail.smtp.port", "587");
		property.put("mail.smtp.host", "smtp.gmail.com");
		property.put("mail.smtp.starttls.enable", "true");
		property.put("mail.smtp.user", "fall2018.blockchain@gmail.com");
		property.put("mail.smtp.password", "Fall2018");
		
		Session session = Session.getInstance(property);
		return session;
	}
	
	private void sendMessage(MimeMessage message, Session session) {
		try {
			Transport transport = session.getTransport("smtp");
			transport.connect(null,"fall2018.blockchain@gmail.com","Fall2018");
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException ex) {
			System.out.println("EmailService:sendMessage:: MessagingException: " + ex.getMessage());
		}
	}
	
	public void sendEmailContainingThePrivateKey(byte[] data, String email) {
		//String receiver = email;
		String receiver = "fall2018.blockchain@gmail.com";
		String from = "fall2018.blockchain@gmail.com";

		// Get the session object
		Session session = getSession();

		// Save the encoded Private key to a file in format <User_email>_priv.txt
		File file = new File("KeyPair/privateKey_" + email + ".txt");
		try {
			// compose the message
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			message.setFrom(new InternetAddress(from));
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
			sendMessage(message, session);
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void sendEmailAuthorizeUserToRegister(byte[] privateKey, String userEmail, VaultWillDetail will) {
		//String receiver = email;
		String receiver = "fall2018.blockchain@gmail.com";
		String from = "fall2018.blockchain@gmail.com";

		System.out.println("Before the session");

		// Get the session object
		Session session = getSession();

		// Save the encoded Private key to a file in format <User_email>_priv.txt
		File file = new File("KeyPair/privateKey_" + userEmail + ".txt");

		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			message.setSubject("Thank you for using Digital Will Key Manager, " + userEmail);
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
			sendMessage(message, session);
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void sendEmailToOwnerToSendWillContentToRequestor(String ownerEmail, VaultUser user, String willId) {
		//String receiver = email;
		String receiver = "fall2018.blockchain@gmail.com";
		String from = "fall2018.blockchain@gmail.com";

		// Get the session object
		Session session = getSession();

		try {
			// compose the message
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			message.setSubject("View Will Request from " + user.getUser_firstName() + " " + user.getUser_lastName());
			message.setText("A view request was made by "+ user.getUser_firstName() + " " + user.getUser_lastName() +"for the will# "+ willId
					+ "\n IMPORTANT NOTE:  Please provide the user with the will content on email: " + user.getUserEmail());

			// Send message
			sendMessage(message, session);
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void sendPublicKeyToUser(String requestUserEmail, String pubKeyEmail) {
		// String receiver = requestUserEmail;
		String receiver = "fall2018.blockchain@gmail.com";
		String from = "fall2018.blockchain@gmail.com";

		// Get the session object
		Session session = getSession();

		// Save the encoded Private key to a file in format <User_email>_priv.txt
		File file = new File("KeyPair/publicKey_" + pubKeyEmail + ".txt");

		try {
			// compose the message
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			message.setSubject("Welcome to Digital Vault");
			message.setText("Please save the attached file conataining the Public Key for email address: "+ pubKeyEmail);
			// Attaching the file containing encoded Public key
			MimeBodyPart mBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			mBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			mBodyPart.setDataHandler(new DataHandler(source));
			mBodyPart.setFileName("publicKey");
			multipart.addBodyPart(mBodyPart);
			message.setContent(multipart);

			// Send message
			sendMessage(message, session);
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void sendEmailToOwnerWithGeneratedSystemToken(String userEmail, String tokenFile, String requestorEmail) {
		// String receiver = userEmail;
		String receiver = "fall2018.blockchain@gmail.com";
		String from = "fall2018.blockchain@gmail.com";

		// Get the session object
		Session session = getSession();

		File file = new File(tokenFile);
		try {
			// compose the message
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			message.setSubject("Digital Vault System Token");
			message.setText("Please send the attached file conataining the System Token to view Will for email address: "+ requestorEmail);
			MimeBodyPart mBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			mBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			mBodyPart.setDataHandler(new DataHandler(source));
			mBodyPart.setFileName("SystemToken_"+ requestorEmail + ".txt");
			multipart.addBodyPart(mBodyPart);
			message.setContent(multipart);

			// Send message
			sendMessage(message, session);
			System.out.println("The email sent was:\n" + message.toString());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}