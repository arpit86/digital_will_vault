package com.csus.vault.web.dao;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.csus.vault.web.model.DigitalWillBlock;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class WillDaoOperation {
	
	@Autowired
    EntityManagerFactory emf;
	
	private final int DIFFICULTY = 3;
	private int nonce;

	public WillDaoOperation() { }
	
	public byte[] encryptUploadedWillWithPubKey(byte[] willData, String userEmail) {
		
		byte[] encryptData = null;
		
		try {
			Cipher encrypt = Cipher.getInstance("RSA");
			encrypt.init(Cipher.ENCRYPT_MODE, getPublicKey(userEmail));
			encryptData = encrypt.doFinal(willData);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
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
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return publicKey;
	}
	
	/*
	 *  Thie function will save the encrypted will to the database
	 */
	public void saveEncryptedWillToDB(byte[] encryptedData, VaultUser user) {
		if(emf != null && encryptedData != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			EntityManager em = null;
			
			try {
				VaultWillDetail will = new VaultWillDetail();
				will.setOwner(user);
				will.setWill_createdTS(new Date());
				will.setWill_updatedTS(new Date());
				will.setWill_content(encryptedData);
				em = emf.createEntityManager();
				em.getTransaction().begin();
				em.persist(will);
				System.out.println("WillDaoOperation:saveEncryptedWillToDB:: saved will: " + user.getUserEmail());
				em.getTransaction().commit();
            } catch (Exception e) {
            	em.getTransaction().rollback();
            	System.out.println("WillDaoOperation:saveEncryptedWillToDB:: Unable to save the Will Record: Exception: "+e.getMessage());
            } finally {
            	// Close EntityManager
            	if(null != em){
            		em.close();
            	}
			}
		}
	}

	public byte[] decryptWillDataWithPrivateKey(byte[] encryptData, String email) {
		
		byte[] originalData = null;
		
		try {
			Cipher decrypt = Cipher.getInstance("RSA");
			decrypt.init(Cipher.DECRYPT_MODE, getPublicKey(email));
			originalData = decrypt.doFinal(encryptData);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		return originalData;
	}

	public String mineBlock(DigitalWillBlock willBlock) {
		
		String minedHash = new String(new char[DIFFICULTY]).replace('\0', 'a');
		String prevHash = willBlock.getPreviousHash();
		Timestamp timeStamp = willBlock.getTimeStamp();
		byte[] data = willBlock.getData();
		int blockNonce = willBlock.getNonce();
		willBlock.setHash(calculateHash(prevHash, timeStamp, blockNonce, data));
		String blockHash = willBlock.getHash();
		while(!blockHash.substring( 0, DIFFICULTY).equals(minedHash)) {
			nonce ++;
			blockHash = calculateHash(prevHash, timeStamp, nonce, data);
		}
		System.out.println("Mined value: " + blockHash);
		willBlock.setNonce(nonce);
		return blockHash;
	}

	private String calculateHash(String prevHash, Timestamp timestamp, int blockNonce, byte[] data) {
		return applySha256ToBlockData(prevHash + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp) + Integer.toString(nonce) + data);
	}
	

	public String applySha256ToBlockData(String string) {
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        

			//Applies sha256 to our input, 
			byte[] blockHash = digest.digest(string.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < blockHash.length; i++) {
				String hex = Integer.toHexString(0xff & blockHash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	
}