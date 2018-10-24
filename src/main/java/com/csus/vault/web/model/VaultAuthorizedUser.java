package com.csus.vault.web.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the vault_authorized_user database table.
 * 
 */
@Entity
@Table(name="vault_authorized_user")
@NamedQuery(name="VaultAuthorizedUser.findAll", query="SELECT v FROM VaultAuthorizedUser v")
public class VaultAuthorizedUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int authorized_userId;

	@Column(name="authorized_update")
	private byte authorizedUpdate;

	@Column(name="authorized_view")
	private byte authorizedView;

	@Temporal(TemporalType.DATE)
	private Date authorizedTS;

	private int vault_userId;

	@Column(name="will_id")
	private int willId;

	public VaultAuthorizedUser() {
	}

	public int getAuthorized_userId() {
		return this.authorized_userId;
	}

	public void setAuthorized_userId(int authorized_userId) {
		this.authorized_userId = authorized_userId;
	}

	public byte getAuthorizedUpdate() {
		return this.authorizedUpdate;
	}

	public void setAuthorizedUpdate(byte authorizedUpdate) {
		this.authorizedUpdate = authorizedUpdate;
	}

	public byte getAuthorizedView() {
		return this.authorizedView;
	}

	public void setAuthorizedView(byte authorizedView) {
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