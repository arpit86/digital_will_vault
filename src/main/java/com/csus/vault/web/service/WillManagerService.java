package com.csus.vault.web.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.csus.vault.web.dao.WillDaoOperation;
import com.csus.vault.web.model.DigitalWillBlock;
import com.csus.vault.web.model.VaultUser;

public class WillManagerService {
	
	
	WillDaoOperation willDao = new WillDaoOperation();

	public void upload(MultipartFile file, VaultUser user) {

		try {
			byte[] bytes = file.getBytes();
			
			// Encrypting the file data with user's Public key
			byte[] encryptedData = willDao.encryptUploadedWillWithPubKey(bytes, user.getUserEmail());
			
			// Saving the encrypted will to database
			willDao.saveEncryptedWillToDB(encryptedData, user);
			//byte[] decryptData = willEncryptDecryptDao.decryptBlockDataWithPubKey(signedData, "email");
			//System.err.println(decryptData);
		} catch(IOException io) {
			
		}
		
	}

	//adding to blockchain
	public void upload(MultipartFile file, VaultUser user, ArrayList<DigitalWillBlock> blockchain) {
		try {
			byte[] bytes = file.getBytes();
			DigitalWillBlock willBlock = new DigitalWillBlock();
			willBlock.setData(bytes);
			willBlock.setEmail(user.getUserEmail());
			willBlock.setTimeStamp(new Timestamp(new Date().getTime()));
			if(blockchain.size() <= 0) {
				willBlock.setPreviousHash("0");
			} else {
				willBlock.setPreviousHash(blockchain.get(blockchain.size()-1).getHash());
			}
			String hash = willDao.mineBlock(willBlock);
			willBlock.setHash(hash);
			blockchain.add(willBlock);
			
			//PeerClient.jar <email id> <block>. This is how peer client should be called by the GUI
			PeerConnectionService peer = new PeerConnectionService(willBlock.getEmail(), willBlock);
			//start the server listening thread
			peer.start();
			System.out.println("Blockchain size:" + blockchain.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}