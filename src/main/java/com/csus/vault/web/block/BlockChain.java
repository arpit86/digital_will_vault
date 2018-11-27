package com.csus.vault.web.block;

import java.util.ArrayList;

public class BlockChain {
	
	private BlockStructure currentBlock;
	private BlockStructure headBlock;
	private ArrayList<BlockStructure> blockList;
	
	public BlockStructure getCurrentBlock() {
		return currentBlock;
	}

	private void setCurrentBlock(BlockStructure currentBlock) {
		this.currentBlock = currentBlock;
	}

	public BlockStructure getHeadBlock() {
		return headBlock;
	}

	private void setHeadBlock(BlockStructure headBlock) {
		this.headBlock = headBlock;
	}

	public ArrayList<BlockStructure> getBlockList() {
		return blockList;
	}
	
	// Constructor
	public BlockChain() {
		blockList = new ArrayList<BlockStructure>();
	}

	public void acceptIncomingBlock(BlockStructure blockStructure) {
		//Check whether this is the first block or Genesis block
		if(headBlock == null) {
			setHeadBlock(blockStructure);
			headBlock.setPreviousHash(null);
		}
		setCurrentBlock(blockStructure);
		blockList.add(blockStructure);
	}
	
	public int verifyBlock(BlockStructure bobj) {
		if(headBlock == null) {
			if (bobj.getPreviousHash() != null)
			{
				return 1; //genesis block expected and non-genesis block received.
			}
		}
		else
		{
			if(!currentBlock.getHash().equals(bobj.getPreviousHash()))
			{
				System.out.println("previous hash: " + bobj.getPreviousHash()+" , current block hash: "+ currentBlock.getHash());
				return 2; // hash of current block does not match the prevhash of received block.
			}
		}
		BlockStructure tempblock = new BlockStructure(this.nextBlockNumber());
		ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
		transactionList = bobj.getTransactionList();
		for (Transaction t : transactionList) {
			tempblock.addTransactionToBlock(t);
		}
		boolean isValid = tempblock.verifyNextBlock(bobj);
		if(isValid) {
			return 0; //block passed nonce verification.
		} else {
			return 3; // block failed nonce verification. 
		}
	}
	
	public int nextBlockNumber() {
		int blockNumber;
		if(headBlock == null) {
			blockNumber = 0;
		} else {
			blockNumber = currentBlock.getBlockNumber() + 1;
		}
		return blockNumber;
	}
}