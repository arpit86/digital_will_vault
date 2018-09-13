package com.csus.vault.web.service;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import com.csus.vault.web.dao.UserDaoImpl;
import com.csus.vault.web.model.UserKey;

public class UserService {

	public void register(UserKey user) {
		
		try {
			//Generate the private-public key pair
			generateKeyPair(user);
			
			//Save the user details in the database
			UserDaoImpl userDao = new UserDaoImpl();
			userDao.register(user);
			
			//Send an email to user with private-public key pair
			new EmailService().sendEmailContainingTheKeyPair(user);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private void generateKeyPair(UserKey user) throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        user.setPublicKey(keyGen.genKeyPair().getPublic());
        user.setPrivateKey(keyGen.genKeyPair().getPrivate());
        
        System.out.println("Public key: " + user.getPublicKey().toString());
        System.out.println("Private key: "+ user.getPrivateKey().toString());
	}
	
	

}
