package com.csus.vault.web.model;

import java.sql.Timestamp;

// This class stores the Digital Will uploaded by the user
public class DigitalWillBlock {
	
	private Timestamp timeStamp;
	private byte[] data;
	private String previousHash;
	private String hash;
	private String email;
	private int nounce;
	
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] bytes) {
		this.data = bytes;
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
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getNounce() {
		return nounce;
	}
	
	public void setNounce(int nounce) {
		this.nounce = nounce;
	}
}