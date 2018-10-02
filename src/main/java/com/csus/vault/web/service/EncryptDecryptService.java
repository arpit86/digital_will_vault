package com.csus.vault.web.service;

import java.io.IOException;
import java.security.PrivateKey;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.csus.vault.web.dao.WillEncryptDecryptDao;
import com.csus.vault.web.dao.WillEncryptDecryptDaoImpl;
import com.csus.vault.web.model.DigitalWillBlock;
import com.csus.vault.web.model.UserKey;

public class EncryptDecryptService {
	
	
	WillEncryptDecryptDao willEncryptDecryptDao = new WillEncryptDecryptDaoImpl();

	public void upload(MultipartFile file, PrivateKey privateKey) {

		try {
			byte[] bytes = file.getBytes();
			
			
	
			// Encrypting the file data with user's Private key
			byte[] signedData = willEncryptDecryptDao.encryptUploadedFileWithPrivKey(bytes, privateKey);
			byte[] decryptData = willEncryptDecryptDao.decryptBlockDataWithPubKey(signedData, "email");
			System.err.println(decryptData);
		} catch(IOException io) {
			
		}
		
	}

	public void upload(MultipartFile file, UserKey user, ArrayList<DigitalWillBlock> blockchain) {
		try {
			byte[] bytes = file.getBytes();
			DigitalWillBlock willBlock = new DigitalWillBlock();
			willBlock.setData(bytes);
			willBlock.setEmail(user.getEmail());
			willBlock.setTimeStamp(new Timestamp(new Date().getTime()));
			if(blockchain.size() <= 0) {
				willBlock.setPreviousHash("0");
			} else {
				willBlock.setPreviousHash(blockchain.get(blockchain.size()-1).getHash());
			}
			String hash = willEncryptDecryptDao.mineBlock(willBlock);
			willBlock.setHash(hash);
			blockchain.add(willBlock);
			
			//PeerClient.jar <email id> <block>. This is how peer client should be called by the GUI
			PeerClient peer = new PeerClient(willBlock.getEmail(), willBlock);
			//start the server listening thread
			peer.run();
			System.out.println("Blockchain size:" + blockchain.size());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
