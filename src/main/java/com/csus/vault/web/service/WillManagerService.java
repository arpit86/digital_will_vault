package com.csus.vault.web.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.web.multipart.MultipartFile;

import com.csus.vault.web.dao.WillDaoOperation;
import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class WillManagerService {
	
	private WillDaoOperation willDao = null;
	private BlockManagerService blockService = null;
	private EmailService emailService = null;
	//private PeerConnectionService peer;
	
	public void upload(MultipartFile file, VaultUser user, PeerConnectionService peer) {
		try {
			byte[] bytes = file.getBytes();
			
			// Encrypting the file data with user's Public key
			byte[] encryptedData = encryptUploadedWillWithSymKey(bytes, user.getUserEmail());
			String willHash = applySha256ToEncryptedWill(encryptedData.toString());
			
			// Saving the encrypted will to database
			willDao = new WillDaoOperation();
			willDao.saveEncryptedWillToDB(encryptedData, user, willHash, peer);
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
	
    public SecretKeySpec getSecretKey(String userEmail) throws IOException{
        byte[] keyBytes = Files.readAllBytes(new File("SecretKey/symKey_"+ userEmail).toPath());
        return new SecretKeySpec(keyBytes, "AES");
    }
    
    public void generateSecretKey(String userEmail) {
    	SecureRandom rnd = new SecureRandom();
        byte [] key = new byte [16];
        rnd.nextBytes(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        writeToFile("SecretKey/symKey_"+ userEmail, secretKey.getEncoded());
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
	 *  This function will encrypt the uploaded will text with user's public key.
	 */
	private byte[] encryptUploadedWillWithSymKey(byte[] willData, String userEmail) {
		byte[] encryptData = null;
		try {
			Cipher encrypt = Cipher.getInstance("AES");
			encrypt.init(Cipher.ENCRYPT_MODE, getSecretKey(userEmail));
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
	
	public byte[] decryptWillDataWithSymKey(byte[] encryptData, String userEmail) {
		byte[] originalData = null;
		try {
			Cipher decrypt = Cipher.getInstance("AES");
			decrypt.init(Cipher.DECRYPT_MODE, getSecretKey(userEmail));
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
			authorizeUser.setUser_createdTS(new Date());
			authorizeUser.setUser_updatedTS(new Date());
			userService.generateKeyPairForAuthorizedUser(authorizeUser, will);
			userService.saveAuthorizeUserToUserTbl(authorizeUser);
		}
		VaultUser temp = userService.getUserDetailByEmail(authorizeUser.getUserEmail());
		authUser = new VaultAuthorizedUser();
		authUser.setVault_userId(temp.getUserId());
		authUser.setAuthorizedTS(new Date());
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
			
			//blockService = new BlockManagerService();
			//blockService.createBlockWithWillViewedTransaction(willId, user.getUserId(), peer);
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
		byte[] encryptedTokenHash = encryptUploadedWillWithSystemPublicKey(tokenHash);
		
		String tokenFile = "SystemToken/token.txt";
		try {
			FileWriter writer = new FileWriter(new File(tokenFile));
			writer.write(new String(encryptedTokenHash) + "\n");
			writer.write(tokenHash + "\n");
			writer.write(userEmail + "\n");
			writer.write(requestorEmail + "\n");
			writer.write(willNo);
			writer.close();
		} catch (IOException ex) {
			System.out.println("WillManagerService:generateSystemToken:: IOException: " + ex.getMessage());
		}
	
		// Send an email to owner to provide the token to the requester
		emailService = new EmailService();
		emailService.sendEmailToOwnerWithGeneratedSystemToken(userEmail, tokenFile, requestorEmail);
	}
	
	private byte[] encryptUploadedWillWithSystemPublicKey(String tokenHash) {
		byte[] encryptData = null;
		try {
			Cipher encrypt = Cipher.getInstance("RSA");
			encrypt.init(Cipher.ENCRYPT_MODE, getPublic("System"));
			encryptData = encrypt.doFinal(tokenHash.getBytes());
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

	public String verifySystemToken(MultipartFile file, VaultUser user) {
		String isValid = "failed";
		try {
			FileReader reader = new FileReader(file.getName());
			BufferedReader buffReader = new BufferedReader(reader);
			String encryptedTokenHash = buffReader.readLine();
			String tokenHash = buffReader.readLine();
			String userEmail = buffReader.readLine();
			String requestorEmail = buffReader.readLine();
			String willNo = buffReader.readLine();
			buffReader.close();
			if(!userEmail.isEmpty() && !requestorEmail.isEmpty() && !willNo.isEmpty()) {
				String calculateTokenHash = applySha256ToEncryptedWill(userEmail+requestorEmail+willNo);
				if(tokenHash.equals(calculateTokenHash)) {
					isValid = "success:"+willNo+":"+userEmail;
				} else {
					String decryptedHash = decryptTokenHashWithSystemPrivateKey(encryptedTokenHash.getBytes());
					if(tokenHash.equals(decryptedHash))
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
	
	public String decryptTokenHashWithSystemPrivateKey(byte[] encryptData) {
		byte[] originalData = null;
		try {
			Cipher decrypt = Cipher.getInstance("RSA");
			decrypt.init(Cipher.DECRYPT_MODE, getPrivate("System"));
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