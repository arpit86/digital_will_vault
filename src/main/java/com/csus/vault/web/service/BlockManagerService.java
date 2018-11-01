package com.csus.vault.web.service;

import java.util.Date;

import com.csus.vault.web.block.Transaction;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class BlockManagerService {
	
	private final String TRANSACTION_TYPE_PUBLIC_KEY_UPLOAD = "Key Upload";
	private final String TRANSACTION_TYPE_WILL_UPLOAD = "Will Upload";
	private final String TRANSACTION_TYPE_WILL_UPDATE = "Will Update";
	private final String TRANSACTION_TYPE_WILL_VIEW = "Will Viewed";
	
	/*
	 *  This function creates a block with single transaction of type: Public Key Upload
	 *  When a new user is created a private-public key is generated.  The private key is with the user
	 *  and the public key is available publicly to send will owner view request.
	 */
	public void createBlockWithPublicKeyTransaction(VaultUser user, PeerConnectionService peer) {
		System.out.println("BlockManagerService:createBlockWithPublicKeyTransaction:: Inside the function.");
		Transaction trans = new Transaction();
		trans.setTransactionTS(new Date());
		trans.setTransactionType(TRANSACTION_TYPE_PUBLIC_KEY_UPLOAD);
		trans.setPublicKeyOrWillHash(user.getUser_publicKey());
		trans.setVault_userId(user.getUserId());
		trans.setWillId(0);
		peer.sendToAll(trans);		
	}
	
	/*
	 *  This function creates a block with single transaction of type: Will Upload
	 *  When a user/owner uploads a digital will, the will is encrypted and uploaded to the database.
	 */
	public void createBlockWithWillUploadTransaction(VaultWillDetail will, PeerConnectionService peer) {
		System.out.println("BlockManagerService:createBlockWithWillUploadTransaction:: Inside the function.");
		Transaction trans = new Transaction();
		trans.setTransactionTS(new Date());
		trans.setTransactionType(TRANSACTION_TYPE_WILL_UPLOAD);
		trans.setPublicKeyOrWillHash(new byte[0]);
		trans.setVault_userId(will.getVault_userId());
		trans.setWillId(will.getWillId());
		peer.sendToAll(trans);	
	}
	
	/*
	 *  This function creates a block with single transaction of type: Will Updated
	 *  When a user/owner updates a digital will, the will is again encrypted and uploaded to the database.
	 */
	public void createBlockWithWillUpdateTransaction(VaultWillDetail will, PeerConnectionService peer) {
		System.out.println("BlockManagerService:createBlockWithWillUpdateTransaction:: Inside the function.");
		Transaction trans = new Transaction();
		trans.setTransactionTS(new Date());
		trans.setTransactionType(TRANSACTION_TYPE_WILL_VIEW);
		trans.setPublicKeyOrWillHash(new byte[0]);
		trans.setVault_userId(will.getVault_userId());
		trans.setWillId(will.getWillId());
		peer.sendToAll(trans);		
	}
	
	/*
	 *  This function creates a block with single transaction of type: Will Viewed
	 *  When an authorized user/owner views an existing digital will, the activity is captured and stored in database.
	 */
	public void createBlockWithWillViewedTransaction(VaultWillDetail will, PeerConnectionService peer) {
		System.out.println("BlockManagerService:createBlockWithWillViewedTransaction:: Inside the function.");
		Transaction trans = new Transaction();
		trans.setTransactionTS(new Date());
		trans.setTransactionType(TRANSACTION_TYPE_WILL_UPDATE);
		trans.setPublicKeyOrWillHash(new byte[0]);
		trans.setVault_userId(will.getVault_userId());
		trans.setWillId(will.getWillId());
		peer.sendToAll(trans);		
	}
}