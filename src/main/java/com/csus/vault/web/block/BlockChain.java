package com.csus.vault.web.block;

import java.util.ArrayList;

public class BlockChain {
	
	private BlockStructureWithMultiple currentBlock;
	private BlockStructureWithMultiple headBlock;
	private ArrayList<BlockStructureWithMultiple> blockList;
	
	public BlockStructureWithMultiple getCurrentBlock() {
		return currentBlock;
	}

	private void setCurrentBlock(BlockStructureWithMultiple currentBlock) {
		this.currentBlock = currentBlock;
	}

	public BlockStructureWithMultiple getHeadBlock() {
		return headBlock;
	}

	private void setHeadBlock(BlockStructureWithMultiple headBlock) {
		this.headBlock = headBlock;
	}

	public ArrayList<BlockStructureWithMultiple> getBlockList() {
		return blockList;
	}
	
	// Constructor
	public BlockChain() {
		blockList = new ArrayList<BlockStructureWithMultiple>();
	}

	public void acceptIncomingBlock(BlockStructureWithMultiple blockStructure) {
		//Check whether this is the first block or Genesis block
		if(headBlock == null) {
			headBlock = blockStructure;
			headBlock.setPreviousHash(null);
		}
		currentBlock = blockStructure;
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