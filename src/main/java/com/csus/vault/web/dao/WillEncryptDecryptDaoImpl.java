package com.csus.vault.web.dao;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.csus.vault.web.model.DigitalWillBlock;

public class WillEncryptDecryptDaoImpl implements WillEncryptDecryptDao {
	
	private final int DIFFICULTY = 3;
	private int nonce;

	public WillEncryptDecryptDaoImpl() { }
	
	@Override
	public byte[] encryptUploadedFileWithPrivKey(byte[] data, PrivateKey signingKey) {
		
		byte[] encryptData = null;
		
		try {
			Cipher encrypt = Cipher.getInstance("RSA");
			encrypt.init(Cipher.ENCRYPT_MODE, signingKey);
			encryptData = encrypt.doFinal(data);
		
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

	@Override
	public PublicKey getPublicKey(String email) {
		/*
		 * Check whether the user'email is registered in the database.
		 * Obtain the public key associated with the email of the user.
		 * 
		 */
		
		
		return null;
	}

	@Override
	public byte[] decryptBlockDataWithPubKey(byte[] encryptData, String email) {
		
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

	@Override
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