package com.csus.vault.web.model;

import java.util.Date;

/**
 * The persistent class for the vault_user database table.
 * 
 */
public class VaultUser{
	private int userId;
	private String passwordSalt;
	private Date user_createdTS;
	private String userEmail;
	private String user_firstName;
	private String user_lastName;
	private String userPassword;
	private String userPhone;
	private byte[] user_publicKey;
	private Date user_updatedTS;

	public VaultUser() {}

	public int getUserId() {
		return this.userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPasswordSalt() {
		return this.passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public Date getUser_createdTS() {
		return this.user_createdTS;
	}

	public void setUser_createdTS(Date user_createdTS) {
		this.user_createdTS = user_createdTS;
	}

	public String getUserEmail() {
		return this.userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUser_firstName() {
		return this.user_firstName;
	}

	public void setUser_firstName(String user_firstName) {
		this.user_firstName = user_firstName;
	}

	public String getUser_lastName() {
		return this.user_lastName;
	}

	public void setUser_lastName(String user_lastName) {
		this.user_lastName = user_lastName;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserPhone() {
		return this.userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public byte[] getUser_publicKey() {
		return this.user_publicKey;
	}

	public void setUser_publicKey(byte[] user_publicKey) {
		this.user_publicKey = user_publicKey;
	}

	public Date getUser_updatedTS() {
		return this.user_updatedTS;
	}

	public void setUser_updatedTS(Date user_updatedTS) {
		this.user_updatedTS = user_updatedTS;
	}

}