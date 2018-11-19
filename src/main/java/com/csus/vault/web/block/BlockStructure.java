package com.csus.vault.web.block;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.ArrayList;

import net.quux00.MerkleTree;

// This class stores the Digital Will uploaded by the user
public class BlockStructure implements java.io.Serializable {
	
	private final int DIFFICULTY = 3;
	private int nonceTemp;
	
	private ArrayList<Transaction> transactionList;
	private int blockNumber;
	private Timestamp timeStamp;
	private String previousHash;
	private String hash;
	private long nonce;
	private BlockStructure nextBlockStructure;
	private transient MerkleTree merkleTree;
	
	//Block constructor
	public BlockStructure(int blockNumber) {
		this.setBlockNumber(blockNumber);
		this.setTimeStamp(new Timestamp(System.currentTimeMillis()));
		this.transactionList = new ArrayList<Transaction>();
	}
	
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public int getBlockNumber() {
		return blockNumber;
	}

	private void setBlockNumber(int blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getPreviousHash() {
		return previousHash;
	}
	
	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public long getNonce() {
		return nonce;
	}
	
	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public ArrayList<Transaction> getTransactionList() {
		return transactionList;
	}

	public MerkleTree getMerkleTree() {
		return merkleTree;
	}

	public void setMerkleTree(MerkleTree merkleTree) {
		this.merkleTree = merkleTree;
	}
	
	public BlockStructure getNextBlockStructure() {
		return nextBlockStructure;
	}

	public void setNextBlockStructure(BlockStructure nextBlockStructure) {
		this.nextBlockStructure = nextBlockStructure;
	}

	/*
	 *  This function adds the transaction to the current block's transaction list
	 */
	public void addTransactionToBlock(Transaction transaction) {
		transactionList.add(transaction);
	}
	
	private String mineBlock(String prevHash) {
		System.out.println("BlockManagerService:mineBlock:: Mining block for Proof of Work consensus.");
		String minedHash = new String(new char[DIFFICULTY]).replace('\0', 'a');
		this.setHash(calculateHashWithMultiple(prevHash));
		String blockHash = this.getHash();
		while(!blockHash.substring( 0, DIFFICULTY).equals(minedHash)) {
			nonceTemp ++;
			blockHash = calculateHashWithMultiple(prevHash);
		}
		System.out.println("Mined value: " + blockHash);
		this.setNonce(nonceTemp);
		return blockHash;
	}
	
	private String verifyBlock(BlockStructure parentBlock) {
		String blockHeader = blockNumber + parentBlock.timeStamp.toString() + parentBlock.getPreviousHash() + parentBlock.getNonce();
		String blockHash = merkleTree.getRoot() + blockHeader;
		return applySha256ToBlockDataWithMultiple(blockHash);
	}
	
	private String calculateHashWithMultiple(String prevHash) {
		String blockHeader = blockNumber + timeStamp.toString() + prevHash + nonce;
		String blockHash = merkleTree.getRoot() + blockHeader;
		return applySha256ToBlockDataWithMultiple(blockHash);
	}
		
	/*
	 *  This function will calculate the Block hash in hexadecimal format.
	 *  It applies SHA-256 hashing algorithm to the block.
	 */
	private String applySha256ToBlockDataWithMultiple(String string) {
		StringBuffer hexDataValue = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        

			//Applying SHA-256 hashing algorithm to the input 
			byte[] dataHash = digest.digest(string.getBytes("UTF-8"));	        
			hexDataValue = new StringBuffer();
			for (int i = 0; i < dataHash.length; i++) {
				String hex = Integer.toHexString(0xff & dataHash[i]);
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
	
	/*
	 *  This function calculates the hash for the current block.
	 *  All blocks have their hash beginning with 'aaa' as DIFICULTY is 3.
	 */
	public void setBlockHash(BlockStructure parentBlock) {
		if(parentBlock != null) {
			this.setPreviousHash(parentBlock.getPreviousHash());
			//assign this new generated block to the parent
			parentBlock.setNextBlockStructure(this);
		} else {
			this.setPreviousHash(null);
		}
		// Generate the Merkle tree before mining the block
		buildMerkleTree();
		this.setHash(mineBlock(this.previousHash));
	}
	
	/*
	 *  This function calculates the hash of each transaction and stores it in a list.
	 *  This list of transaction hashes is the given to the constructor to generate a MerkleTree from bottom up.
	 */
	private void buildMerkleTree() {
		ArrayList<String> transactionHashList = new ArrayList<>();
		if(transactionList.size() == 4) {
			for(Transaction t: transactionList) {
				transactionHashList.add(applySha256ToBlockDataWithMultiple(t.toString()));
			}
			merkleTree = new MerkleTree(transactionHashList);
		} else {
			System.out.println("BlockManagerService:buildMerkleTree:: Number of transactions is not 4: " + transactionList.size());
		}
	}
	
	public boolean verifyNextBlock(BlockStructure newBlock) {
		boolean isValid = false;
		String minedHash = new String(new char[DIFFICULTY]).replace('\0', 'a');
		buildMerkleTree();
		
		//Check whether it is a valid block
		String blockHash = verifyBlock(newBlock);
		if(blockHash.substring( 0, DIFFICULTY).equals(minedHash) && newBlock.getHash().equals(blockHash)) {
			isValid = true;
		} 
		return isValid;
	}
}