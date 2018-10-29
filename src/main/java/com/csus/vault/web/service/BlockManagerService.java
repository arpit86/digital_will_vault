package com.csus.vault.web.service;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.csus.vault.web.block.BlockStructure;
import com.csus.vault.web.block.Transaction;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;

public class BlockManagerService {
	
	private final int DIFFICULTY = 3;
	private int nonce;
	private final String TRANSACTION_TYPE_PUBLIC_KEY_UPLOAD = "Key Upload";
	private final String TRANSACTION_TYPE_WILL_UPLOAD = "Will Upload";
	private final String TRANSACTION_TYPE_WILL_UPDATE = "Will Update";
	private final String TRANSACTION_TYPE_WILL_VIEW = "Will Viewed";
	
	/*
	 *  This function creates a block with single transaction of type: Public Key Upload
	 *  When a new user is created a private-public key is generated.  The private key is with the user
	 *  and the public key is available publicly to send will owner view request.
	 */
	public void createBlockWithPublicKeyTransaction(VaultUser user) {
		System.out.println("BlockManagerService:createBlockWithPublicKeyTransaction:: Inside the function.");
		Transaction trans = new Transaction();
		trans.setTransactionTS(new Date());
		trans.setTransactionType(TRANSACTION_TYPE_PUBLIC_KEY_UPLOAD);
		trans.setPublicKeyOrWillHash(user.getUser_publicKey());
		trans.setVault_userId(user.getUserId());
		trans.setWillId(0);
		generateBlock(trans);		
	}
	
	/*
	 *  This function creates a block with single transaction of type: Will Upload
	 *  When a user/owner uploads a digital will, the will is encrypted and uploaded to the database.
	 */
	public void createBlockWithWillUploadTransaction(VaultWillDetail will) {
		System.out.println("BlockManagerService:createBlockWithWillUploadTransaction:: Inside the function.");
		Transaction trans = new Transaction();
		trans.setTransactionTS(new Date());
		trans.setTransactionType(TRANSACTION_TYPE_WILL_UPLOAD);
		trans.setPublicKeyOrWillHash(new byte[0]);
		trans.setVault_userId(will.getVault_userId());
		trans.setWillId(will.getWillId());
		generateBlock(trans);		
	}
	
	/*
	 *  This function creates a block with single transaction of type: Will Updated
	 *  When a user/owner updates a digital will, the will is again encrypted and uploaded to the database.
	 */
	public void createBlockWithWillUpdateTransaction(VaultWillDetail will) {
		System.out.println("BlockManagerService:createBlockWithWillUpdateTransaction:: Inside the function.");
		Transaction trans = new Transaction();
		trans.setTransactionTS(new Date());
		trans.setTransactionType(TRANSACTION_TYPE_WILL_VIEW);
		trans.setPublicKeyOrWillHash(new byte[0]);
		trans.setVault_userId(will.getVault_userId());
		trans.setWillId(will.getWillId());
		generateBlock(trans);		
	}
	
	/*
	 *  This function creates a block with single transaction of type: Will Viewed
	 *  When an authorized user/owner views an existing digital will, the activity is captured and stored in database.
	 */
	public void createBlockWithWillViewedTransaction(VaultWillDetail will) {
		System.out.println("BlockManagerService:createBlockWithWillViewedTransaction:: Inside the function.");
		Transaction trans = new Transaction();
		trans.setTransactionTS(new Date());
		trans.setTransactionType(TRANSACTION_TYPE_WILL_UPDATE);
		trans.setPublicKeyOrWillHash(new byte[0]);
		trans.setVault_userId(will.getVault_userId());
		trans.setWillId(will.getWillId());
		generateBlock(trans);		
	}
	
	/*
	 *  This function creates block for single transactions.
	 */
	private void generateBlock(Transaction transaction) {
		System.out.println("BlockManagerService:generateBlock:: Create a block for the transaction:" + transaction.getTransactionType());
		BlockStructure block = new BlockStructure();
		block.setTimeStamp(new Timestamp(System.currentTimeMillis()));
		block.setTransaction(transaction);
		block.setUserId(transaction.getVault_userId());
		
		//Need to obtain the block number from the blockchain to figure out if this is a genesis block
		/*blockchain.size() <= 0 */
		//PeerConnectionService peer = new PeerConnectionService(willBlock.getEmail(), block);
		//start the server listening thread
		//peer.start();
		if(block.getPreviousHash().isEmpty()) {
			block.setPreviousHash("0");
		} else {
			/* blockchain.get(blockchain.size()-1).getHash() */
			block.setPreviousHash("Previous block hash");
		}
		String blockHash = mineBlock(block);
		block.setHash(blockHash);
		
		//Broadcast this block to the Peer.
	}
	
	private String mineBlock(BlockStructure willBlock) {
		System.out.println("BlockManagerService:mineBlock:: Mining block for Proof of Work consensus.");
		String minedHash = new String(new char[DIFFICULTY]).replace('\0', 'a');
		String prevHash = willBlock.getPreviousHash();
		Timestamp timeStamp = willBlock.getTimeStamp();
		Transaction data = willBlock.getTransaction();
		willBlock.setHash(calculateHash(prevHash, timeStamp, 0, data));
		String blockHash = willBlock.getHash();
		while(!blockHash.substring( 0, DIFFICULTY).equals(minedHash)) {
			nonce ++;
			blockHash = calculateHash(prevHash, timeStamp, nonce, data);
		}
		System.out.println("Mined value: " + blockHash);
		willBlock.setNonce(nonce);
		return blockHash;
	}

	private String calculateHash(String prevHash, Timestamp timestamp, long blockNonce, Transaction data) {
		return applySha256ToBlockData(prevHash + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp) + Integer.toString(nonce) + data);
	}
		
	/*
	 *  This function will calculate the Block hash in hexadecimal format.
	 *  It applies SHA-256 hashing algorithm to the block.
	 */
	private String applySha256ToBlockData(String string) {
		StringBuffer hexDataValue = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        

			//Applying SHA-256 hashing algorithm to the input 
			byte[] blockHash = digest.digest(string.getBytes("UTF-8"));	        
			hexDataValue = new StringBuffer();
			for (int i = 0; i < blockHash.length; i++) {
				String hex = Integer.toHexString(0xff & blockHash[i]);
				if(hex.length() == 1) {
					hexDataValue.append('0');
				}
				hexDataValue.append(hex);
			}
		} catch(Exception e) {
			System.out.println("BlockManagerService:applySha256ToBlockData:: Exeption: " + e.getMessage());
		}
		return hexDataValue.toString();
	}
}