package com.csus.vault.web;

import java.util.ArrayList;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.csus.vault.web.model.DigitalWillBlock;

public class BlockChainContext implements ApplicationContextAware {

	private ApplicationContext context;
	private ArrayList<DigitalWillBlock> blockchain;
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
		
	}
	
	public ApplicationContext getContext() {
		return context;
	}

	public ArrayList<DigitalWillBlock> getBlockchain() {
		return blockchain;
	}

	public void setBlockchain(ArrayList<DigitalWillBlock> blockchain) {
		this.blockchain = blockchain;
	}
}