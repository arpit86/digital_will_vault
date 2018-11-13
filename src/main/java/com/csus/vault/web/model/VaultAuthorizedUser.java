package com.csus.vault.web.model;

import java.util.Date;

/**
 * The persistent class for the vault_authorized_user database table.
 * 
 */
public class VaultAuthorizedUser{
	
	private int authorized_userId;
	private String authorizedUpdate;
	private String authorizedView;
	private Date authorizedTS;
	private int vault_userId;
	private int willId;

	public VaultAuthorizedUser() {}

	public int getAuthorized_userId() {
		return this.authorized_userId;
	}

	public void setAuthorized_userId(int authorized_userId) {
		this.authorized_userId = authorized_userId;
	}

	public String getAuthorizedUpdate() {
		return this.authorizedUpdate;
	}

	public void setAuthorizedUpdate(String authorizedUpdate) {
		this.authorizedUpdate = authorizedUpdate;
	}

	public String getAuthorizedView() {
		return this.authorizedView;
	}

	public void setAuthorizedView(String authorizedView) {
		this.authorizedView = authorizedView;
	}

	public Date getAuthorizedTS() {
		return this.authorizedTS;
	}

	public void setAuthorizedTS(Date authorizedTS) {
		this.authorizedTS = authorizedTS;
	}

	public int getVault_userId() {
		return this.vault_userId;
	}

	public void setVault_userId(int vault_userId) {
		this.vault_userId = vault_userId;
	}

	public int getWillId() {
		return this.willId;
	}

	public void setWillId(int willId) {
		this.willId = willId;
	}

}