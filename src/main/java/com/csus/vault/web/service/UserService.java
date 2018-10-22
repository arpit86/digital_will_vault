package com.csus.vault.web.service;

import java.nio.charset.Charset;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.csus.vault.web.dao.UserDaoImpl;
import com.csus.vault.web.model.VaultUser;

public class UserService {

	public void register(VaultUser user) {
		UserDaoImpl userDao = new UserDaoImpl();
		try {
			generateKeyPair(user);
			generatePasswordHashAndSalt(user);
			userDao.register(user);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public boolean verify(VaultUser user) {
		UserDaoImpl userDao = new UserDaoImpl();
		return userDao.verify(user);
	}
	
	public VaultUser verifyUser(String userEmail) {
		UserDaoImpl userDao = new UserDaoImpl();
		return userDao.verifyUser(userEmail);
	}
	
	private void generateKeyPair(VaultUser user) throws NoSuchAlgorithmException {
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        user.setUser_publicKey(keyGen.genKeyPair().getPublic().getEncoded());
        
        //Send an email to user with private key to the user email
        new EmailService().sendEmailContainingThePrivateKey(keyGen.genKeyPair().getPrivate().getEncoded(), user.getUserEmail());
        
        System.out.println("Public key is saved in database and the Private key is memailed to user.");
	}

	private void generatePasswordHashAndSalt(VaultUser user) {
		SecureRandom random = new SecureRandom();
		int ITERATIONS = 1000;
		int KEY_LENGTH = 128;
		byte[] salt = new byte[128];
		random.nextBytes(salt);
		
		char[] password = user.getUserPassword().toCharArray();
		
		PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		    user.setUserPassword(new String(skf.generateSecret(spec).getEncoded(), Charset.forName("UTF-8")));
		    user.setPasswordSalt(new String(salt, Charset.forName("UTF-8")));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
		} finally {
			spec.clearPassword();
		}
	}
		
	public boolean isPasswordValid(char[] password, byte[] salt, byte[] expectedHash) {
		int ITERATIONS = 1000;
		int KEY_LENGTH = 128;
		
		PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] pwdHash = skf.generateSecret(spec).getEncoded();
			if (pwdHash.length != expectedHash.length) 
				return false;
			for (int i = 0; i < pwdHash.length; i++) {
				if (pwdHash[i] != expectedHash[i]) 
					return false;
			}
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			spec.clearPassword();
		}	
	    return true;
	}
}