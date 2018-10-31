package com.csus.vault.web.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.web.multipart.MultipartFile;

import com.csus.vault.web.dao.UserDaoOperation;
import com.csus.vault.web.dao.WillDaoOperation;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class WillManagerService {
	
	private WillDaoOperation willDao = null;
		
	public void upload(MultipartFile file, VaultUser user) {
		try {
			byte[] bytes = file.getBytes();
			
			// Encrypting the file data with user's Public key
			byte[] encryptedData = encryptUploadedWillWithPubKey(bytes, user.getUserEmail());
			
			// Saving the encrypted will to database
			willDao = new WillDaoOperation();
			willDao.saveEncryptedWillToDB(encryptedData, user);
		} catch(IOException io) {
			System.out.println("WillManagerService:upload:: Exeption: " + io.getMessage());
		}
	}
	
	
	/*
	 *  This function will encrypt the uploaded will text with user's public key.
	 */
	private byte[] encryptUploadedWillWithPubKey(byte[] willData, String userEmail) {
		
		byte[] encryptData = null;
		
		try {
			Cipher encrypt = Cipher.getInstance("RSA");
			encrypt.init(Cipher.ENCRYPT_MODE, getPublicKey(userEmail));
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
		} 
		return encryptData;
	}
	
	/*
	 * Obtain the public key associated with the email of the user.
	 */
	private PublicKey getPublicKey(String userEmail) {
		PublicKey publicKey = null;
		try {
			VaultUser user = new UserDaoOperation().getUserDetailByEmail(userEmail);
			publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(user.getUser_publicKey()));
		} catch (InvalidKeySpecException ex) {
			System.out.println("WillManagerService:getPublicKey:: InvalidKeySpecException: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("WillManagerService:getPublicKey:: NoSuchAlgorithmException: " + ex.getMessage());
		}
		return publicKey;
	}
	
	/*
	 * Obtain the private key from the file uploaded by the user.
	 */
	private PrivateKey getPrivateKey(byte[] privateKeyByte) {
		PrivateKey privateKey = null;
		try {
			privateKey = KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(privateKeyByte));
		} catch (InvalidKeySpecException ex) {
			System.out.println("WillManagerService:getPrivateKey:: InvalidKeySpecException: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("WillManagerService:getPrivateKey:: InvalidKeySpecException: " + ex.getMessage());
		}
		return privateKey;
	}
	
	@SuppressWarnings("unused")
	private byte[] decryptWillDataWithPrivateKey(byte[] encryptData, byte[] privateKeyByte) {
		byte[] originalData = null;
		try {
			Cipher decrypt = Cipher.getInstance("RSA");
			decrypt.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKeyByte));
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
		}
		return originalData;
	}


	public void addAuthorizedWillUser(ArrayList<VaultUser> authorizedUserList, VaultWillDetail will) {
		
		UserService userService = new UserService();
		for(VaultUser u: authorizedUserList) {
			if(userService.verify(u)) {
				u.setUser_createdTS(new Date());
				u.setUser_updatedTS(new Date());
				userService.registerAuthorizeUser(u);
				
			}
		}
		
	}


	public boolean checkWillAuthorization(VaultUser user) {
		// TODO Auto-generated method stub
		return false;
	}


	public String retrieveWillData(MultipartFile privateKey, VaultUser user) {
		String originalWill = "";
		try {
			willDao = new WillDaoOperation();
			VaultWillDetail will = willDao.getWillDetailbyUserId(user.getUserId());
			originalWill = new String(decryptWillDataWithPrivateKey(will.getWillContent(), privateKey.getBytes()), Charset.forName("UTF-8"));
		} catch (IOException io) {
			System.out.println("WillManagerService:retrieveWillData:: IOException: " + io.getMessage());
		}
		return originalWill;
	}


	public VaultWillDetail getWillDetailbyUserId(VaultUser user) {
		willDao = new WillDaoOperation();
		return willDao.getWillDetailbyUserId(user.getUserId());
	}
}