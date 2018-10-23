package com.csus.vault.web.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="vault_will_detail")
public class VaultWillDetail {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="will_id", unique=true, nullable=false)
	private int willId;
	
	private VaultUser owner;
	
	private Date will_createdTS;
	
	private Date will_updatedTS;
	
	private byte[] will_content;

	public int getWillId() {
		return willId;
	}

	public void setWillId(int willId) {
		this.willId = willId;
	}

	public VaultUser getOwner() {
		return owner;
	}

	public void setOwner(VaultUser owner) {
		this.owner = owner;
	}

	public Date getWill_createdTS() {
		return will_createdTS;
	}

	public void setWill_createdTS(Date will_createdTS) {
		this.will_createdTS = will_createdTS;
	}

	public Date getWill_updatedTS() {
		return will_updatedTS;
	}

	public void setWill_updatedTS(Date will_updatedTS) {
		this.will_updatedTS = will_updatedTS;
	}

	public byte[] getWill_content() {
		return will_content;
	}

	public void setWill_content(byte[] will_content) {
		this.will_content = will_content;
	}
}