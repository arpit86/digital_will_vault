package com.csus.vault.web.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.web.multipart.MultipartFile;

import com.csus.vault.web.dao.WillDaoOperation;
import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class WillManagerService {
	
	private WillDaoOperation willDao = null;
	private BlockManagerService blockService = null;
	private EmailService emailService =null;
	private PeerConnectionService peer;
	
	public WillManagerService() {
		peer = PeerConnectionService.getInstance();
	}
	
	public void upload(MultipartFile file, VaultUser user) {
		try {
			byte[] bytes = file.getBytes();
			
			// Encrypting the file data with user's Public key
			byte[] encryptedData = encryptUploadedWillWithPubKey(bytes, user.getUserEmail());
			String willHash = applySha256ToEncryptedWill(encryptedData.toString());
			
			// Saving the encrypted will to database
			willDao = new WillDaoOperation();
			willDao.saveEncryptedWillToDB(encryptedData, user, willHash);
		} catch(IOException io) {
			System.out.println("WillManagerService:upload:: Exeption: " + io.getMessage());
		} catch (SQLException ex) {
			System.out.println("WillManagerService:upload:: SQLExeption: " + ex.getMessage());
		}
	}
	
	public void uploadUpdatedWill(MultipartFile updateWillFile, VaultUser user) {
		try {
			byte[] bytes = updateWillFile.getBytes();
			
			// Encrypting the file data with user's Public key
			byte[] encryptedData = encryptUploadedWillWithPubKey(bytes, user.getUserEmail());
			String willHash = applySha256ToEncryptedWill(encryptedData.toString());
			
			// Saving the encrypted will to database
			willDao = new WillDaoOperation();
			willDao.saveModifiedWillToDB(encryptedData, user, willHash);
		} catch(IOException io) {
			System.out.println("WillManagerService:uploadUpdatedWill:: IOExeption: " + io.getMessage());
		} catch (SQLException ex) {
			System.out.println("WillManagerService:uploadUpdatedWill:: SQLExeption: " + ex.getMessage());
		}
	}
	
	/*
	 * Obtain the private key from the file uploaded by the user.
	 */
	public PrivateKey getPrivate(String userEmail) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File("KeyPair/privateKey_" + userEmail).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

	/*
	 * Obtain the public key associated with the email of the user.
	 */
    public PublicKey getPublic(String userEmail) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File("KeyPair/publicKey_" + userEmail).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
	
	
	/*
	 *  This function will encrypt the uploaded will text with user's public key.
	 */
	private byte[] encryptUploadedWillWithPubKey(byte[] willData, String userEmail) {
		byte[] encryptData = null;
		try {
			Cipher encrypt = Cipher.getInstance("RSA");
			encrypt.init(Cipher.ENCRYPT_MODE, getPublic(userEmail));
			encryptData = encrypt.doFinal(willData);
		} catch (InvalidKeyException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithPubKey:: InvalidKeyException: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithPubKey:: NoSuchAlgorithmException: " + ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithPubKey:: NoSuchPaddingException: " + ex.getMessage());
		} catch (IllegalBlockSizeException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithPubKey:: IllegalBlockSizeException: " + ex.getMessage());
		} catch (BadPaddingException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithPubKey:: BadPaddingException: " + ex.getMessage());
		} catch (Exception ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithPubKey:: Exception: " + ex.getMessage());
		} 
		return encryptData;
	}
	
	public byte[] decryptWillDataWithPrivateKey(byte[] encryptData, String userEmail) {
		byte[] originalData = null;
		try {
			Cipher decrypt = Cipher.getInstance("RSA");
			decrypt.init(Cipher.DECRYPT_MODE, getPrivate(userEmail));
			originalData = decrypt.doFinal(encryptData);
		} catch (InvalidKeyException ex) {
			System.out.println("WillManagerService:decryptWillDataWithPrivateKey:: InvalidKeyException: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("WillManagerService:decryptWillDataWithPrivateKey:: NoSuchAlgorithmException: " + ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			System.out.println("WillManagerService:decryptWillDataWithPrivateKey:: NoSuchPaddingException: " + ex.getMessage());
		} catch (IllegalBlockSizeException ex) {
			System.out.println("WillManagerService:decryptWillDataWithPrivateKey:: IllegalBlockSizeException: " + ex.getMessage());
		} catch (BadPaddingException ex) {
			System.out.println("WillManagerService:decryptWillDataWithPrivateKey:: BadPaddingException: " + ex.getMessage());
		} catch (Exception ex) {
			System.out.println("WillManagerService:decryptWillDataWithPrivateKey:: Exception: " + ex.getMessage());
		}
		return originalData;
	}

	/*
	 *  This function will calculate the Block hash in hexadecimal format.
	 *  It applies SHA-256 hashing algorithm to the block.
	 */
	private String applySha256ToEncryptedWill(String string) {
		StringBuffer hexDataValue = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        

			//Applying SHA-256 hashing algorithm to the input 
			byte[] blockHash = digest.digest(string.getBytes("UTF-8"));	        
			hexDataValue = new StringBuffer();
			for (int i = 0; i < blockHash.length; i++) {
				String hex = Integer.toHexString(0xff & blockHash[i]);
				if(hex.length() == 1) {
					hexDataValue.append('0');
				}
				hexDataValue.append(hex);
			}
		} catch(Exception e) {
			System.out.println("BlockManagerService:applySha256ToBlockData:: Exeption: " + e.getMessage());
		}
		return hexDataValue.toString();
	}

	public void addAuthorizedWillUser(ArrayList<VaultUser> authorizedUserList, VaultWillDetail will) throws SQLException {
		UserService userService = new UserService();
		VaultAuthorizedUser authUser = null;
		for(VaultUser u: authorizedUserList) {
			if(userService.verify(u).equalsIgnoreCase("new")) {
				u.setUser_createdTS(new Date());
				u.setUser_updatedTS(new Date());
				userService.generateKeyPairForAuthorizedUser(u, will);
				userService.saveAuthorizeUserToUserTbl(u);
			}
			VaultUser temp = userService.getUserDetailByEmail(u.getUserEmail());
			authUser = new VaultAuthorizedUser();
			authUser.setVault_userId(temp.getUserId());
			authUser.setAuthorizedTS(new Date());
			authUser.setAuthorizedUpdate("false");
			authUser.setAuthorizedView("true");
			authUser.setWillId(will.getWillId());
			userService.saveAuthorizeUserToAuthTbl(authUser);
		}
	}

	public VaultWillDetail getWillDetailbyUserId(VaultUser user) {
		VaultWillDetail willDetail = null;
		try {
			willDao = new WillDaoOperation();
			willDetail = willDao.getWillDetailbyUserId(user.getUserId());
		} catch (SQLException e) {
			System.out.println("BlockManagerService:getWillDetailbyUserId:: SQLExeption: " + e.getMessage());
		}
		return willDetail;
	}

	public ArrayList<Integer> getListOfWillWithViewAccess(VaultUser user) {
		ArrayList<Integer> willList = new ArrayList<Integer>();
		try {
			willDao = new WillDaoOperation();
			willList = willDao.getListOfWillWithViewAccess(user);
		} catch (SQLException e) {
			System.out.println("BlockManagerService:getListOfWillWithViewAccess:: SQLExeption: " + e.getMessage());
		}	
		return willList;
	}

	public void requestOwnerForWill(VaultUser user, int willId) {
		willDao = new WillDaoOperation();
		try {
			String ownerEmail = willDao.requestOwnerForWill(user, willId);
			
			// Send an email to user with private key to the user email
			emailService = new EmailService();
			emailService.sendEmailToOwnerToSendWillContentToRequestor(ownerEmail, user, willId);
			
			blockService = new BlockManagerService();
			blockService.createBlockWithWillViewedTransaction(willId, user.getUserId(), peer);
		} catch (SQLException e) {
			System.out.println("BlockManagerService:requestOwnerForWill:: SQLExeption: " + e.getMessage());
		}
	}
}