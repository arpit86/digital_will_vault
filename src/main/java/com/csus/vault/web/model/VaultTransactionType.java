package com.csus.vault.web.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the vault_transaction_type database table.
 * 
 */
@Entity
@Table(name="vault_transaction_type")
@NamedQuery(name="VaultTransactionType.findAll", query="SELECT v FROM VaultTransactionType v")
public class VaultTransactionType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="transaction_type_id", unique=true, nullable=false)
	private int transactionTypeId;

	@Column(name="transaction_type_value", nullable=false, length=45)
	private String transactionTypeValue;

	public VaultTransactionType() {
	}

	public int getTransactionTypeId() {
		return this.transactionTypeId;
	}

	public void setTransactionTypeId(int transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}

	public String getTransactionTypeValue() {
		return this.transactionTypeValue;
	}

	public void setTransactionTypeValue(String transactionTypeValue) {
		this.transactionTypeValue = transactionTypeValue;
	}

}