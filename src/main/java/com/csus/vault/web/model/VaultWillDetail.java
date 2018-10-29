package com.csus.vault.web.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the vault_will_detail database table.
 * 
 */
@Entity
@Table(name="vault_will_detail")
@NamedQuery(name="VaultWillDetail.findAll", query="SELECT v FROM VaultWillDetail v")
public class VaultWillDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="will_id")
	private int willId;

	private int vault_userId;

	@Column(name="will_content")
	private byte[] willContent;

	@Temporal(TemporalType.DATE)
	private Date will_createdTS;

	@Temporal(TemporalType.DATE)
	private Date will_updatedTS;

	public VaultWillDetail() {
	}

	public int getWillId() {
		return this.willId;
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

}