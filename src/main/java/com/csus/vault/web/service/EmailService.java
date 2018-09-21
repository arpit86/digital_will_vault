package com.csus.vault.web.service;



import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.csus.vault.web.model.UserKey;

public class EmailService {

	public void sendEmailContainingTheKeyPair(UserKey user) {
		String to = user.getEmail();
		//String to = "s.shweta.87@gmail.com";
	    String from = "s.shweta.87@gmail.com";  
	    String host = "localhost";//or IP address  
	  
	    System.out.println("Before the session");
	    
	    //Get the session object  
	    Properties properties = System.getProperties();  
	    properties.setProperty("mail.smtp.host", host);  
	    Session session = Session.getDefaultInstance(properties);  
	  
	    //compose the message  
	    try{  
	         MimeMessage message = new MimeMessage(session);  
	         message.setFrom(new InternetAddress(from));  
	         message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
	         message.setSubject("Welcome to Digital Vault");  
	         message.setText("Please save the following tokens securely:\n"
	         		+ "Public token: " + user.getPublicKey()
	         		+ "\nPrivate token: " + user.getPrivateKey());

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

}
