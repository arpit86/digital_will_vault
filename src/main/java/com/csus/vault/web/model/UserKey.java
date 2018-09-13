package com.csus.vault.web.model;

import java.security.PrivateKey;
import java.security.PublicKey;

// This class stores the private-public key pair in database
public class UserKey {
	
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private PrivateKey privateKey;
	private PublicKey publicKey;

	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public void setPrivateKey(PrivateKey privateK) {
		this.privateKey = privateK;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(PublicKey publicK) {
		this.publicKey = publicK;
	}
}