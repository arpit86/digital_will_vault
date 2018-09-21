package com.csus.vault.web.dao;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.csus.vault.web.model.DigitalWillBlock;

public interface WillEncryptDecryptDao {
	
	public byte[] encryptUploadedFileWithPrivKey(byte[] data, PrivateKey signingKey);
	
	public PublicKey getPublicKey(String email);
	
	public byte[] decryptBlockDataWithPubKey(byte[] encryptData, String email);

	public String mineBlock(DigitalWillBlock willBlock);

}
