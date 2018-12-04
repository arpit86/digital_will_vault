package com.csus.vault.web.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.web.multipart.MultipartFile;

import com.csus.key.service.KeyManager;
import com.csus.vault.web.dao.WillDaoOperation;
import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class WillManagerService {
	
	private WillDaoOperation willDao = null;
	private BlockManagerService blockService = null;
	private EmailService emailService = null;
	private KeyManager keyManager = null;
	//private PeerConnectionService peer;
	
	public void upload(MultipartFile file, VaultUser user, PeerConnectionService peer) {
		try {
			byte[] bytes = file.getBytes();
			
			// Encrypting the file data with user's Public key
			byte[] encryptedData = encryptUploadedWillWithSymKey(bytes, user.getUserEmail());
			String willHash = applySha256ToEncryptedWill(encryptedData.toString());
			
			// Saving the encrypted will to database
			willDao = new WillDaoOperation();
			VaultWillDetail will = willDao.saveEncryptedWillToDB(encryptedData, user, willHash);
			blockService = new BlockManagerService();
			blockService.createBlockWithWillUploadTransaction(will, peer);
		} catch(IOException io) {
			System.out.println("WillManagerService:upload:: Exeption: " + io.getMessage());
		} catch (SQLException ex) {
			System.out.println("WillManagerService:upload:: SQLExeption: " + ex.getMessage());
		}
	}
	
	public void uploadUpdatedWill(MultipartFile updateWillFile, VaultUser user, PeerConnectionService peer) {
		try {
			byte[] bytes = updateWillFile.getBytes();
			
			// Encrypting the file data with user's Public key
			byte[] encryptedData = encryptUploadedWillWithSymKey(bytes, user.getUserEmail());
			String willHash = applySha256ToEncryptedWill(encryptedData.toString());
			
			// Saving the encrypted will to database
			willDao = new WillDaoOperation();
			willDao.saveModifiedWillToDB(encryptedData, user, willHash, peer);
		} catch(IOException io) {
			System.out.println("WillManagerService:uploadUpdatedWill:: IOExeption: " + io.getMessage());
		} catch (SQLException ex) {
			System.out.println("WillManagerService:uploadUpdatedWill:: SQLExeption: " + ex.getMessage());
		}
	}
	
	/*
	 *  This function will encrypt the uploaded will text with user's public key.
	 */
	private byte[] encryptUploadedWillWithSymKey(byte[] willData, String userEmail) {
		byte[] encryptData = null;
		try {
			Cipher encrypt = Cipher.getInstance("AES");
			keyManager = new KeyManager();
			encrypt.init(Cipher.ENCRYPT_MODE, keyManager.getSecretKey(userEmail));
			encryptData = encrypt.doFinal(willData);
		} catch (InvalidKeyException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: InvalidKeyException: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: NoSuchAlgorithmException: " + ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: NoSuchPaddingException: " + ex.getMessage());
		} catch (IllegalBlockSizeException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: IllegalBlockSizeException: " + ex.getMessage());
		} catch (BadPaddingException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: BadPaddingException: " + ex.getMessage());
		} catch (Exception ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: Exception: " + ex.getMessage());
		} 
		return encryptData;
	}
	
	public byte[] decryptWillDataWithSymKey(byte[] encryptData, String userEmail) {
		byte[] originalData = null;
		try {
			Cipher decrypt = Cipher.getInstance("AES");
			keyManager = new KeyManager();
			decrypt.init(Cipher.DECRYPT_MODE, keyManager.getSecretKey(userEmail));
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

	public void addAuthorizedWillUser(VaultUser authorizeUser, VaultWillDetail will) throws SQLException {
		UserService userService = new UserService();
		VaultAuthorizedUser authUser = null;
		if(userService.verify(authorizeUser).equalsIgnoreCase("new")) {
			KeyManager keyManager = new KeyManager();
			try {
				authorizeUser.setUser_publicKey(keyManager.generateKeyPairForAuthorizedUser(authorizeUser.getUserEmail(), will.getWillId()));
				userService.saveAuthorizeUserToUserTbl(authorizeUser);
			} catch (NoSuchAlgorithmException e) {
				System.out.println("WillManagerService:addAuthorizedWillUser:: NoSuchAlgorithmException: " + e.getMessage());
			}
		}
		VaultUser temp = userService.getUserDetailByEmail(authorizeUser.getUserEmail());
		authUser = new VaultAuthorizedUser();
		authUser.setVault_userId(temp.getUserId());
		authUser.setAuthorizedUpdate("false");
		authUser.setAuthorizedView("true");
		authUser.setWillId(will.getWillId());
		userService.saveAuthorizeUserToAuthTbl(authUser);
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

	public ArrayList<String> getListOfWillWithViewAccess(VaultUser user) {
		ArrayList<String> willList = new ArrayList<String>();
		try {
			willDao = new WillDaoOperation();
			willList = willDao.getListOfWillWithViewAccess(user);
		} catch (SQLException e) {
			System.out.println("BlockManagerService:getListOfWillWithViewAccess:: SQLExeption: " + e.getMessage());
		}	
		return willList;
	}

	public void requestOwnerForWill(VaultUser user, String willOwnerName) {
		willDao = new WillDaoOperation();
		try {
			String ownerData = willDao.requestOwnerForWill(user, willOwnerName);
			String[] ownerInfo = ownerData.split(":");
			String ownerEmail = ownerInfo[0];
			String willId = ownerInfo[1];
			
			// Send an email to user with private key to the user email
			emailService = new EmailService();
			emailService.sendEmailToOwnerToSendWillContentToRequestor(ownerEmail, user, willId);
		} catch (SQLException e) {
			System.out.println("BlockManagerService:requestOwnerForWill:: SQLExeption: " + e.getMessage());
		}
	}

	public void requestPublicKey(String userEmail, String pubKeyEmail) {
		emailService = new EmailService();
		emailService.sendPublicKeyToUser(userEmail, pubKeyEmail);
	}

	public void generateSystemToken(String userEmail, String requestorEmail, String willNo) {
		String tokenHash = applySha256ToEncryptedWill(userEmail+requestorEmail+willNo);
		byte[] encryptedTokenHash = encryptUploadedDataWithSystemSecretKey(tokenHash);
		try {
			KeyManager keyManager = new KeyManager();
			byte[] encryptedSecretKey = encryptUploadedDataWithSystemPublicKey(new String(keyManager.getSecretKey(userEmail).getEncoded()));
			FileWriter writer = new FileWriter(new File("SystemToken/token_"+requestorEmail));
			writer.write(new String(encryptedTokenHash, "UTF-8") + "\n");
			writer.write(tokenHash + "\n");
			writer.write(new Date() + "\n");
			writer.write(userEmail + "\n");
			writer.write(requestorEmail + "\n");
			writer.write(willNo + "\n");
			writer.write(new String(encryptedSecretKey, "UTF-8"));
			writer.close();
		} catch (IOException ex) {
			System.out.println("WillManagerService:generateSystemToken:: IOException: " + ex.getMessage());
		} catch (Exception ex) {
			System.out.println("WillManagerService:generateSystemToken:: Exception: " + ex.getMessage());
		}
	
		// Send an email to owner to provide the token to the requester
		emailService = new EmailService();
		emailService.sendEmailToOwnerWithGeneratedSystemToken(userEmail, "SystemToken/token_"+requestorEmail, requestorEmail);
	}
	
	private byte[] encryptUploadedDataWithSystemSecretKey(String tokenHash) {
		byte[] encryptData = null;
		try {
			Cipher encrypt = Cipher.getInstance("AES");
			keyManager = new KeyManager();
			encrypt.init(Cipher.ENCRYPT_MODE, keyManager.getSecretKey("System"));
			encryptData = encrypt.doFinal(tokenHash.getBytes());
		} catch (InvalidKeyException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: InvalidKeyException: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: NoSuchAlgorithmException: " + ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: NoSuchPaddingException: " + ex.getMessage());
		} catch (IllegalBlockSizeException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: IllegalBlockSizeException: " + ex.getMessage());
		} catch (BadPaddingException ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: BadPaddingException: " + ex.getMessage());
		} catch (Exception ex) {
			System.out.println("WillManagerService:encryptUploadedWillWithSymKey:: Exception: " + ex.getMessage());
		} 
		return encryptData;
	}

	private byte[] encryptUploadedDataWithSystemPublicKey(String tokenHash) {
		byte[] encryptData = null;
		try {
			Cipher encrypt = Cipher.getInstance("RSA");
			keyManager = new KeyManager();
			encrypt.init(Cipher.ENCRYPT_MODE, keyManager.getPublic("System"));
			encryptData = encrypt.doFinal(tokenHash.getBytes());
		} catch (InvalidKeyException ex) {
			System.out.println("WillManagerService:encryptUploadedDataWithSystemPublicKey:: InvalidKeyException: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("WillManagerService:encryptUploadedDataWithSystemPublicKey:: NoSuchAlgorithmException: " + ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			System.out.println("WillManagerService:encryptUploadedDataWithSystemPublicKey: NoSuchPaddingException: " + ex.getMessage());
		} catch (IllegalBlockSizeException ex) {
			System.out.println("WillManagerService:encryptUploadedDataWithSystemPublicKey:: IllegalBlockSizeException: " + ex.getMessage());
		} catch (BadPaddingException ex) {
			System.out.println("WillManagerService:encryptUploadedDataWithSystemPublicKey:: BadPaddingException: " + ex.getMessage());
		} catch (Exception ex) {
			System.out.println("WillManagerService:encryptUploadedDataWithSystemPublicKey:: Exception: " + ex.getMessage());
		} 
		return encryptData;
	}

	public String verifySystemToken(MultipartFile file, VaultUser user) {
		String isValid = "failed";
		try {
			FileReader reader = new FileReader("SystemToken/token_" + user.getUserEmail());
			BufferedReader buffReader = new BufferedReader(reader);
			String encryptedTokenHash = buffReader.readLine();
			String tokenHash = buffReader.readLine();
			String tokenDate = buffReader.readLine();
			String userEmail = buffReader.readLine();
			String requestorEmail = buffReader.readLine();
			String willNo = buffReader.readLine();
			String encryptedSecretKey = buffReader.readLine();
			buffReader.close();
			if(!userEmail.isEmpty() && !requestorEmail.isEmpty() && !willNo.isEmpty()) {
				String calculateTokenHash = applySha256ToEncryptedWill(userEmail+requestorEmail+willNo);
				/*byte[] calcEncryptedTokenHash = encryptUploadedDataWithSystemSecretKey(calculateTokenHash);
				String hash = new String(calcEncryptedTokenHash, "UTF-8");*/
				if(calculateTokenHash.equals(tokenHash)) {
					isValid = "success:"+willNo+":"+userEmail;
				}
			}
		} catch (FileNotFoundException ex) {
			System.out.println("WillManagerService:verifySystemToken:: FileNotFoundException: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("WillManagerService:verifySystemToken:: IOException: " + ex.getMessage());
		}
		return isValid;
	}
	
	public String decryptTokenHashWithSystemSecretKey(byte[] encryptData) {
		byte[] originalData = null;
		try {
			Cipher decrypt = Cipher.getInstance("AES");
			keyManager = new KeyManager();
			decrypt.init(Cipher.DECRYPT_MODE, keyManager.getSecretKey("System"));
			originalData = decrypt.doFinal(encryptData);
		} catch (InvalidKeyException ex) {
			System.out.println("WillManagerService:decryptTokenHashWithSystemPrivateKey:: InvalidKeyException: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("WillManagerService:decryptTokenHashWithSystemPrivateKey:: NoSuchAlgorithmException: " + ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			System.out.println("WillManagerService:decryptTokenHashWithSystemPrivateKey:: NoSuchPaddingException: " + ex.getMessage());
		} catch (IllegalBlockSizeException ex) {
			System.out.println("WillManagerService:decryptTokenHashWithSystemPrivateKey:: IllegalBlockSizeException: " + ex.getMessage());
		} catch (BadPaddingException ex) {
			System.out.println("WillManagerService:decryptTokenHashWithSystemPrivateKey:: BadPaddingException: " + ex.getMessage());
		} catch (Exception ex) {
			System.out.println("WillManagerService:decryptTokenHashWithSystemPrivateKey:: Exception: " + ex.getMessage());
		}
		return new String(originalData);
	}

	public String getWillDetailbyWillId(String willNo, String ownerEmail, VaultUser user, PeerConnectionService peer) {
		String willData = null;
		try {
			willDao = new WillDaoOperation();
			VaultWillDetail willDetail = willDao.getWillDetailbyWillId(Integer.parseInt(willNo));
			byte[] decryptedData = decryptWillDataWithSymKey(willDetail.getWillContent(), ownerEmail);
			willData = new String(decryptedData, "UTF-8");
			blockService = new BlockManagerService();
			blockService.createBlockWithWillViewedTransaction(willDetail.getWillId(), user.getUserId(), peer);
		} catch (SQLException e) {
			System.out.println("BlockManagerService:getWillDetailbyWillId:: SQLExeption: " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			System.out.println("BlockManagerService:getWillDetailbyWillId:: UnsupportedEncodingException: " + e.getMessage());
		}
		return willData;
	}
}