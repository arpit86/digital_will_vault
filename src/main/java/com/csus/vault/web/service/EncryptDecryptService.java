package com.csus.vault.web.service;

import java.io.IOException;
import java.security.PrivateKey;

import org.springframework.web.multipart.MultipartFile;

import com.csus.vault.web.dao.WillEncryptDecryptDaoImpl;

public class EncryptDecryptService {

	public void upload(MultipartFile file, PrivateKey privateKey) {

		try {
			byte[] bytes = file.getBytes();
			
			
	
			// Encrypting the file data with user's Private key
			byte[] signedData = new WillEncryptDecryptDaoImpl().encryptUploadedFileWithPrivKey(bytes, privateKey);
			byte[] decryptData = new WillEncryptDecryptDaoImpl().decryptBlockDataWithPubKey(signedData, "email");
			
		} catch(IOException io) {
			
		}
		
	}

}
