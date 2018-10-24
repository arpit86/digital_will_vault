package com.csus.vault.web.model;

import java.util.Date;

/*@Entity
@Table(name="vault_transaction_detail")*/
public class VaultTransactionDetail {
	
	/*@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="transaction_id", unique=true, nullable=false)*/
	private int transactionId;
	
	private int transactionTypeId;
	
	private Date transactionTS;
	
	private long userID;

	public int getTransactionId() {
		return transactionId;
	}

	public int getTransactionTypeId() {
		return transactionTypeId;
	}

	public void setTransactionTypeId(int transactionTypeId) {
	}

	public Date getTransactionTS() {
		return transactionTS;
	}

	public void setTransactionTS(Date transactionTS) {
		this.transactionTS = transactionTS;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}
}