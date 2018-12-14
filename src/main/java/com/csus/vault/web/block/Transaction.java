package com.csus.vault.web.block;

import java.util.Arrays;
import java.util.Date;

public class Transaction implements java.io.Serializable {
	
	private static final long serialVersionUID = -1L;
	private String transactionType;
	private long vault_userId;
	private byte[] publicKeyOrWillHash;
	private long willId;
	private Date transactionTS;
	
	public String getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public long getVault_userId() {
		return vault_userId;
	}
	
	public void setVault_userId(long vault_userId) {
		this.vault_userId = vault_userId;
	}
	
	public Date getTransactionTS() {
		return transactionTS;
	}
	
	public void setTransactionTS(Date transactionTS) {
		this.transactionTS = transactionTS;
	}

	public byte[] getPublicKeyOrWillHash() {
		return publicKeyOrWillHash;
	}

	public void setPublicKeyOrWillHash(byte[] publicKeyOrWillHash) {
		this.publicKeyOrWillHash = publicKeyOrWillHash;
	}

	public long getWillId() {
		return willId;
	}

	public void setWillId(long willId) {
		this.willId = willId;
	}
	
	 @Override
	    public boolean equals(Object o) { 
	  
	        // If the object is compared with itself then return true   
	        if (o == this) { 
	            return true; 
	        } 
	  
	        /* Check if o is an instance of Complex or not 
	          "null instanceof [type]" also returns false */
	        if (!(o instanceof Transaction)) { 
	            return false; 
	        } 
	          
	        // typecast o to Complex so that we can compare data members  
	        Transaction c = (Transaction) o; 
	          
	        // Compare the data members and return accordingly  
	        return transactionType.equals(c.transactionType) 
	                && Long.compare(vault_userId, c.vault_userId) == 0
	                && Arrays.equals(publicKeyOrWillHash, c.publicKeyOrWillHash)
	                && Long.compare(willId, c.willId) == 0
	                && transactionTS.compareTo(c.transactionTS)==0; 
	    } 
	 
	 public int hashCode() {
		    int result = 42;
		    result = 42 * result + transactionTS.hashCode();
		    return result;
		  }
}