package com.csus.vault.web.dao;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class WillEncryptDecryptDaoImpl implements WillEncryptDecryptDao {

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
	
}