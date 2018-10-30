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
	
	public void verifyBlockChain(String previousHash) {
		if(headBlock == null) {
			System.err.println("BlockChain:verifyBlockChain:: Genesis block not present.");
		}
		
		boolean isValid = headBlock.verifyBlockChainValidity(null);
		if(isValid) {
			System.out.println("BlockChain:verifyBlockChain:: The block chain is valid");
		} else {
			System.out.println("BlockChain:verifyBlockChain:: The block chain is invalid");
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