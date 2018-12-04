package com.csus.vault.web.service;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.csus.key.service.KeyManager;
import com.csus.vault.web.dao.UserDaoOperation;
import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;

public class UserService {
	
	private UserDaoOperation userDao = null;
	private BlockManagerService blockService = null;
	private final int ITERATIONS = 1000;
	private final int KEY_LENGTH = 128;
	
	/*
	 *  Register a new user
	 */
	public void register(VaultUser user, PeerConnectionService peer) {
		try {
			userDao = new UserDaoOperation();
			KeyManager keyManager = new KeyManager();
			user.setUser_publicKey(keyManager.generateKeyPair(user.getUserEmail()));
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
		VaultUser tempUser = getUserDetailByEmail(user.getUserEmail());
		peer.connectToBootNode(user.getUserEmail());
		//start the server listening thread
		peer.run();
		blockService = new BlockManagerService();
		blockService.createBlockWithPublicKeyTransaction(tempUser, peer);
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

	public void saveAuthorizeUserToUserTbl(VaultUser user) throws SQLException {
		userDao = new UserDaoOperation();
		userDao.register(user);
	}
	
	public void saveAuthorizeUserToAuthTbl(VaultAuthorizedUser authUser) throws SQLException {
		userDao = new UserDaoOperation();
		userDao.saveAuthorizedUser(authUser);
	}
}