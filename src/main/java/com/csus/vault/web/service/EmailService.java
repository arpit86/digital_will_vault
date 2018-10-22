package com.csus.vault.web.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailService {

	public void sendEmailContainingThePrivateKey(byte[] data, String email) {
		String to = email;
		//String to = "s.shweta.87@gmail.com";
	    String from = "s.shweta.87@gmail.com";  
	    String host = "localhost";//or IP address  
	  
	    System.out.println("Before the session");
	    
	    //Get the session object  
	    Properties properties = System.getProperties();  
	    properties.setProperty("mail.smtp.host", host);  
	    Session session = Session.getDefaultInstance(properties);
	    
	    //Save the encoded Private key to a file in format <User_email>_priv.txt
	    File file = saveKeyToFile(data,email);
	  
	    //compose the message  
	    try{  
	         MimeMessage message = new MimeMessage(session);  
	         message.setFrom(new InternetAddress(from));  
	         message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
	         message.setSubject("Welcome to Digital Vault");  
	         message.setText("Please save the attached file securely on your computer.\n"
	         		+ "IMPORTANT NOTE:  The will uploaded can only be read with help of this file.\n"
	         		+ "ONCE THIS FILE IS CORRUPTED, THE WILL NO LONGER BE AVAILABLE OR CAN BE RETRIEVED.");
	         // Attaching the file containing encoded Private key		
	         MimeBodyPart mBodyPart = new MimeBodyPart();
	         Multipart multipart = new MimeMultipart();
	         mBodyPart = new MimeBodyPart();
	         DataSource source = new FileDataSource(file);
	         mBodyPart.setDataHandler(new DataHandler(source));
	         mBodyPart.setFileName(email+"_priv.txt");
	         multipart.addBodyPart(mBodyPart);
	         message.setContent(multipart);
	         
	         // Send message 
	         System.out.println("The email sent was:\n" + message.toString());
	         
	         /*
	          * Run telnet before uncommenting the below code.
	          * We need to enable telnet client if connection is refused.
	          */
	         //Transport.send(message);
	      } catch (MessagingException mex) {
	    	  mex.printStackTrace();
	      }  
	}

	@SuppressWarnings("resource")
	private File saveKeyToFile(byte[] data, String email) {
		
		File file = new File("C:\\temp\\" + email +"_priv.txt");
        try {
        	//save bytes[] into a file
            FileOutputStream fileOuputStream = new FileOutputStream(file);
            fileOuputStream.write(data);

            System.out.println("Done writing to file");
        } catch (IOException e) {
            e.printStackTrace();
        }
		return file;
	}

}