package com.csus.vault.web.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.csus.vault.web.dao.UserDaoOperation;
import com.csus.vault.web.model.VaultUser;

public class UserService {
	
	private UserDaoOperation userDao = null;
	private BlockManagerService blockService = null;
	private EmailService emailService = null;
	private final int ITERATIONS = 1000;
	private final int KEY_LENGTH = 128;

	public void register(VaultUser user) {
		try {
			userDao = new UserDaoOperation();
			generateKeyPair(user);
			generatePasswordHashAndSalt(user);
			userDao.register(user);
			blockService = new BlockManagerService();
			blockService.createBlockWithPublicKeyTransaction(user);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public boolean verify(VaultUser user) {
		userDao = new UserDaoOperation();
		return userDao.verify(user);
	}
	
	public VaultUser getUserDetailByEmail(String userEmail) {
		userDao = new UserDaoOperation();
		return userDao.getUserDetailByEmail(userEmail);
	}
	
	/*
	 *  This function generate private-public key pair using RSA algorithm.
	 */
	private void generateKeyPair(VaultUser user) throws NoSuchAlgorithmException {
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
        byte[] privateKey = keyGen.genKeyPair().getPrivate().getEncoded();
        user.setUser_publicKey(publicKey);
        
        //Send an email to user with private key to the user email
        emailService = new EmailService();
        emailService.sendEmailContainingThePrivateKey(privateKey, user.getUserEmail());
        
        System.out.println("Public key is saved in database and the Private key is emailed to user.");
        // The public-private key is saved to KeyPair folder
        writeToFile("KeyPair/publicKey_" + user.getUserEmail(), publicKey);
        writeToFile("KeyPair/privateKey_" + user.getUserEmail(), privateKey);
	}
	
	private void writeToFile(String path, byte[] key) {
		try {
			File f = new File(path);
			f.getParentFile().mkdirs();
			FileOutputStream fos;
			fos = new FileOutputStream(f);
			fos.write(key);
	        fos.flush();
	        fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
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
	
	public void registerAuthorizeUser(VaultUser user) {
		try {
			userDao = new UserDaoOperation();
			generateKeyPair(user);
			userDao.register(user);
			blockService = new BlockManagerService();
			blockService.createBlockWithPublicKeyTransaction(user);
			emailService = new EmailService();
			emailService.sendEmailAuthorizeUserToRegister(user.getUserEmail());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public PrivateKey getPrivate(String filename, String algorithm) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }

    public PublicKey getPublic(String filename, String algorithm) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }
    
    public SecretKeySpec getSecretKey(String filename, String algorithm) throws IOException{
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        return new SecretKeySpec(keyBytes, algorithm);
    }
    
    public void GenerateSymmetricKey(int length, String algorithm) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
    	SecretKeySpec secretKey;
            SecureRandom rnd = new SecureRandom();
            byte [] key = new byte [16];
            rnd.nextBytes(key);
            secretKey = new SecretKeySpec(key, "AES");
            writeToFile("OneKey/secretKey_", secretKey.getEncoded());
    }
}