package com.csus.vault.web.model;

import java.util.Date;

/**
 * The persistent class for the vault_will_detail database table.
 * 
 */
public class VaultWillDetail{
	
	private int willId;
	private int vault_userId;
	private byte[] willContent;
	private Date will_createdTS;
	private Date will_updatedTS;
	private String willHash;

	public VaultWillDetail() {}

	public int getWillId() {
		return this.willId;
	}
	
	public void setWillId(int willId) {
		this.willId = willId;
	}

	public int getVault_userId() {
		return this.vault_userId;
	}

	public void setVault_userId(int vault_userId) {
		this.vault_userId = vault_userId;
	}

	public byte[] getWillContent() {
		return this.willContent;
	}

	public void setWillContent(byte[] willContent) {
		this.willContent = willContent;
	}

	public Date getWill_createdTS() {
		return this.will_createdTS;
	}

	public void setWill_createdTS(Date will_createdTS) {
		this.will_createdTS = will_createdTS;
	}

	public Date getWill_updatedTS() {
		return this.will_updatedTS;
	}

	public void setWill_updatedTS(Date will_updatedTS) {
		this.will_updatedTS = will_updatedTS;
	}

	public String getWillHash() {
		return willHash;
	}

	public void setWillHash(String willHash) {
		this.willHash = willHash;
	}
}