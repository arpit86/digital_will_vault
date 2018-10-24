package com.csus.vault.web.block;

import java.util.Date;

public class Transaction {
	
	private String transactionType;
	private long vault_userId;
	private byte[] publicKey;
	private long willId;
	private Date transactionTS;
	
	public String getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public long getVault_userId() {
		return vault_userId;
	}
	
	public void setVault_userId(long vault_userId) {
		this.vault_userId = vault_userId;
	}
	
	public Date getTransactionTS() {
		return transactionTS;
	}
	
	public void setTransactionTS(Date transactionTS) {
		this.transactionTS = transactionTS;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public long getWillId() {
		return willId;
	}

	public void setWillId(long willId) {
		this.willId = willId;
	}
}