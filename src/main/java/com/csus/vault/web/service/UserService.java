package com.csus.vault.web.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.csus.vault.web.dao.UserDaoOperation;
import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class UserService {
	
	private UserDaoOperation userDao = null;
	private BlockManagerService blockService = null;
	private EmailService emailService = null;
	private final int ITERATIONS = 1000;
	private final int KEY_LENGTH = 128;
	//private PeerConnectionService peer;
	
	/*
	 *  Register a new user
	 */
	public void register(VaultUser user, PeerConnectionService peer) {
		try {
			userDao = new UserDaoOperation();
			generateKeyPair(user);
			generatePasswordHashAndSalt(user);
			userDao.register(user);
			if(userDao.getUserDetailByEmail(user.getUserEmail()).getUserId() == 1) {
				peer.setMiner(true);
			}
			peer.connectToBootNode(user.getUserEmail());
			//start the server listening thread
			peer.run();
			blockService = new BlockManagerService();
			blockService.createBlockWithPublicKeyTransaction(user,peer);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 *  Register a new authorized user
	 */
	public void registerAuthorizeUser(VaultUser user, PeerConnectionService peer) throws SQLException {
		userDao = new UserDaoOperation();
		generatePasswordHashAndSalt(user);
		userDao.update(user);
		peer.connectToBootNode(user.getUserEmail());
		//start the server listening thread
		peer.run();
		blockService = new BlockManagerService();
		blockService.createBlockWithPublicKeyTransaction(user, peer);
	}
	
	/*
	 *  This function checks whether a user exists in the database.
	 */
	public String verify(VaultUser user) throws SQLException {
		userDao = new UserDaoOperation();
		return userDao.verify(user);
	}
	
	/*
	 *  This function retrieves the user detail for the given email id.
	 */
	public VaultUser getUserDetailByEmail(String userEmail) throws SQLException {
		userDao = new UserDaoOperation();
		return userDao.getUserDetailByEmail(userEmail);
	}
	
	/*
	 *  This function generate private-public key pair using RSA algorithm.
	 */
	public void generateKeyPair(VaultUser user) throws NoSuchAlgorithmException {
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
        byte[] privateKey = keyGen.genKeyPair().getPrivate().getEncoded();
        user.setUser_publicKey(publicKey);
        
        //Send an email to user with private key to the user email
        emailService = new EmailService();
        emailService.sendEmailContainingThePrivateKey(privateKey, user.getUserEmail());
        
        System.out.println("Public key is saved in database and the Private key is emailed to user.");
        // The public-private key is saved to KeyPair folder: <keyType>_<userEmail>
        writeToFile("KeyPair/publicKey_" + user.getUserEmail(), publicKey);
        writeToFile("KeyPair/privateKey_" + user.getUserEmail(), privateKey);
	}
	
	/*
	 *  This function writes the byte[] data to the file path provided.
	 */
	private void writeToFile(String path, byte[] key) {
		try {
			File f = new File(path);
			f.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(f);
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

	public void generateKeyPairForAuthorizedUser(VaultUser user, VaultWillDetail will) {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(512);
			byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
			byte[] privateKey = keyGen.genKeyPair().getPrivate().getEncoded();
			user.setUser_publicKey(publicKey);

			// Send an email to user with private key to the user email
			emailService = new EmailService();
			emailService.sendEmailAuthorizeUserToRegister(privateKey, user.getUserEmail(), will);

			System.out.println("Public key is saved in database and the Private key is emailed to user.");
			// The public-private key is saved to KeyPair folder: <keyType>_<userEmail>
			writeToFile("KeyPair/publicKey_" + user.getUserEmail(), publicKey);
			writeToFile("KeyPair/privateKey_" + user.getUserEmail(), privateKey);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveAuthorizeUserToUserTbl(VaultUser user) throws SQLException {
		userDao = new UserDaoOperation();
		userDao.register(user);
	}
	
	public void saveAuthorizeUserToAuthTbl(VaultAuthorizedUser authUser) throws SQLException {
		userDao = new UserDaoOperation();
		userDao.saveAuthorizedUser(authUser);
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