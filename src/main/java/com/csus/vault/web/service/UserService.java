package com.csus.vault.web.service;

import java.nio.charset.Charset;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.csus.vault.web.dao.UserDaoOperation;
import com.csus.vault.web.model.VaultUser;

public class UserService {
	
	private UserDaoOperation userDao = new UserDaoOperation();
	private final int ITERATIONS = 1000;
	private final int KEY_LENGTH = 128;

	public void register(VaultUser user) {
		try {
			generateKeyPair(user);
			generatePasswordHashAndSalt(user);
			userDao.register(user);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public boolean verify(VaultUser user) {
		return userDao.verify(user);
	}
	
	public VaultUser getUserDetailByEmail(String userEmail) {
		return userDao.getUserDetailByEmail(userEmail);
	}
	
	/*
	 *  This function generate private-public key pair using RSA algorithm.
	 */
	private void generateKeyPair(VaultUser user) throws NoSuchAlgorithmException {
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        user.setUser_publicKey(keyGen.genKeyPair().getPublic().getEncoded());
        
        //Send an email to user with private key to the user email
        new EmailService().sendEmailContainingThePrivateKey(keyGen.genKeyPair().getPrivate().getEncoded(), user.getUserEmail());
        
        System.out.println("Public key is saved in database and the Private key is emailed to user.");
	}
	
	/*
	 *  This function generates a hash value of the password by applying the random generated salt.
	 */
	private void generatePasswordHashAndSalt(VaultUser user) {
		SecureRandom random = new SecureRandom();
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
	
	/*
	 *  This function checks whether the provided password is a valid password
	 */
	public boolean isPasswordValid(char[] password, byte[] salt, byte[] expectedHash) {
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