package com.csus.vault.web.block;

import java.sql.Timestamp;

// This class stores the Digital Will uploaded by the user
public class BlockStructure {
	
	private Timestamp timeStamp;
	private Transaction transaction = new Transaction();
	private String previousHash;
	private String hash;
	private long userId;
	private long nonce;
	
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public String getPreviousHash() {
		return previousHash;
	}
	
	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public long getNonce() {
		return nonce;
	}
	
	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}