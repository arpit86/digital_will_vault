package com.csus.vault.web.service;

import java.io.IOException;
import java.security.PrivateKey;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.csus.vault.web.dao.WillEncryptDecryptDaoImpl;
import com.csus.vault.web.model.DigitalWillBlock;
import com.csus.vault.web.model.UserKey;

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

	public void upload(MultipartFile file, UserKey user) {
		try {
			byte[] bytes = file.getBytes();
			DigitalWillBlock willBlock = new DigitalWillBlock();
			willBlock.setData(bytes);
			willBlock.setEmail(user.getEmail());
			willBlock.setTimeStamp(new Timestamp(new Date().getTime()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
