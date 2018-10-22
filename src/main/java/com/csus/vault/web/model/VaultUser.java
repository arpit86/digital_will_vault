package com.csus.vault.web.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the vault_user database table.
 * 
 */
@Entity
@Table(name="vault_user")
@NamedQuery(name="VaultUser.findAll", query="SELECT v FROM VaultUser v")
public class VaultUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id", unique=true, nullable=false)
	private int userId;

	@Column(name="password_salt", nullable=false, length=256)
	private String passwordSalt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date user_createdTS;

	@Column(name="user_email", nullable=false, length=45)
	private String userEmail;

	@Column(nullable=false, length=45)
	private String user_firstName;

	@Column(length=45)
	private String user_lastName;

	@Column(name="user_password", nullable=false, length=256)
	private String userPassword;

	@Column(name="user_phone")
	private BigInteger userPhone;

	@Column(length=2048)
	private byte[] user_publicKey;

	@Temporal(TemporalType.TIMESTAMP)
	private Date user_updatedTS;

	public VaultUser() {
	}

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

	public BigInteger getUserPhone() {
		return this.userPhone;
	}

	public void setUserPhone(BigInteger userPhone) {
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