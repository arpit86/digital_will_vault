package com.csus.vault.web.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="vault_authorized_user")
public class VaultAuthorizedUser {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	private int id;
	
	private VaultWillDetail will;
	
	private List<VaultUser> authorizedUsers;
	
	private Date user_authorizedTS;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public VaultWillDetail getWill() {
		return will;
	}

	public void setWill(VaultWillDetail will) {
		this.will = will;
	}

	public List<VaultUser> getAuthorizedUsers() {
		return authorizedUsers;
	}

	public void setAuthorizedUsers(List<VaultUser> authorizedUsers) {
		this.authorizedUsers = authorizedUsers;
	}

	public Date getUser_authorizedTS() {
		return user_authorizedTS;
	}

	public void setUser_authorizedTS(Date user_authorizedTS) {
		this.user_authorizedTS = user_authorizedTS;
	}

}
