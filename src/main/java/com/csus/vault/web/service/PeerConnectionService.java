package com.csus.vault.web.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.csus.vault.web.block.BlockChain;
import com.csus.vault.web.block.BlockStructure;
import com.csus.vault.web.block.PeerInfo;
import com.csus.vault.web.block.Transaction;

import net.quux00.MerkleTree;

public class PeerConnectionService extends Thread {

	public static final int BOOT_PORT = 2999;
	public static final int TX_COUNT_TRESHOLD = 4;
	public static final int DIFFICULTY = 3;
	private int nonceTemp;

	private ArrayList<PeerInfo> peerList;
	private BlockChain blockChain;
	private String email = "";
	private Integer port;
	private PrintWriter outWriter;
	private BufferedReader inReader;
	private boolean isMiner = false;
	PeerInfo peerInfo;
	ArrayList<Transaction> transactionPool = new ArrayList<Transaction>();
	
	public void sendToAll(Transaction T) {
		PrintWriter transactionWriter;
		for (PeerInfo p : peerList) {
			Socket peerSocket = p.getSocket();
			try {
				transactionWriter = new PrintWriter(peerSocket.getOutputStream(), true);
				transactionWriter.println("transaction");
				ObjectOutputStream transactionDataWriter = new ObjectOutputStream(peerSocket.getOutputStream());
				transactionDataWriter.writeObject(T);
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}
	
	public boolean isMiner() {
		return isMiner;
	}

	public void setMiner(boolean isMiner) {
		this.isMiner = isMiner;
	}

	public void connectToBootNode(String email) {
		this.email = email;
		this.peerList = new ArrayList<PeerInfo>();
		blockChain = new BlockChain();

		// Establish a connection with boot node server on port 2999
		try {
			Socket socket = new Socket("127.0.0.1", BOOT_PORT);
			System.out.println("Connected to boot server");

			this.port = socket.getLocalPort();
			outWriter = new PrintWriter(socket.getOutputStream(), true);
			outWriter.println(email);
			System.out.println("Sent email to server");
			outWriter.println(port);
			System.out.println("Sent port to server");
			outWriter.println("End");
			System.out.println("Sent End to server");

			inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String peerEmail = "";
			String peerPort = "";
			String data = "init";
			data = inReader.readLine();
			System.out.println("received peer's email from server");
			// Get the list of peers from boot node server and connect to each of the peers
			while (!data.equals("End")) {
				try {
					peerEmail = data;
					System.out.println(peerEmail);
					peerPort = inReader.readLine();
					System.out.println("received peer's port from server");
					System.out.println(peerPort);

					// Create an object to store the peer's information
					peerInfo = new PeerInfo();
					// get peer's email id
					peerInfo.setEmail(peerEmail);
					// get the peer's port number
					peerInfo.setPort(Integer.parseInt(peerPort));
					// peerInfo.setSocket(peerSocket);
					peerList.add(peerInfo);
					data = inReader.readLine();
					System.out.println("received next peer's email or end from server");
				} catch (IOException io) {
					io.printStackTrace();
				}
			} // end of while
			socket.close();
		} catch (UnknownHostException u) {
			u.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	public void run() {
		try {
			for (PeerInfo p : peerList) {
				Socket peerSocket = new Socket("127.0.0.1", p.getPort());
				outWriter = new PrintWriter(peerSocket.getOutputStream(), true);
				outWriter.println(email);
				p.setSocket(peerSocket);
				System.out.println("Connected to peer " + p.getEmail());
				StartMessageHandlerThread(peerList.indexOf(p));
			}

			// Start server socket for listening at same port used for connection to boot node
			ServerSocket serverSocket = new ServerSocket(port);

			// keep listening for incoming connections
			while (true) {
				System.out.println(email + " Server listening for connection on port: " + port);
				// accept is a blocking call meaning that code won't execute further until a
				// peer connects to server
				Socket socket = serverSocket.accept();

				// peer connected at this point
				System.out.println("Connection was established on port: " + socket.getPort());

				// takes input from the client socket
				inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String data = "";

				try {
					data = inReader.readLine();
					System.out.println(email + " received connection request from user " + data);

					// create a peer object to store this peer's data
					PeerInfo peer = new PeerInfo();
					peer.setEmail(data);

					// get the peer's port number from socket
					peer.setPort(socket.getPort());
					peer.setSocket(socket);

					// Adding the peer to the Arraylist
					peerList.add(peer);
					System.out.println("Connected to " + peer.getEmail() + " on port " + socket.getPort());

					// Run a thread for each connected peer to handle transactions and block messages
					StartMessageHandlerThread(peerList.indexOf(peer));
				} catch (IOException io) {
					System.out.println("PeerConnectionService:run:: IOException: "+ io.getMessage());
				}
			} // end of while loop
		} catch (IOException io) {
			System.out.println("PeerConnectionService:run:: IOException: "+ io.getMessage());
		}
	}

	private String mineBlock(BlockStructure blockInfo) {
		System.out.println("PeerConnectionService:mineBlock:: Mining block for Proof of Work consensus.");
		String minedHash = new String(new char[DIFFICULTY]).replace('\0', 'a');
		blockInfo.setHash(calculateHashWithMultiple(blockInfo));
		String blockHash = blockInfo.getHash();
		while (!blockHash.substring(0, DIFFICULTY).equals(minedHash)) {
			nonceTemp++;
			blockHash = calculateHashWithMultiple(blockInfo);
		}
		System.out.println("Mined value: " + blockHash);
		blockInfo.setNonce(nonceTemp);
		return blockHash;
	}

	private String calculateHashWithMultiple(BlockStructure blockInfo) {
		String blockHeader = blockInfo.getBlockNumber() + blockInfo.getTimeStamp().toString()
				+ blockInfo.getPreviousHash() + blockInfo.getNonce();
		String blockHash = blockInfo.getMerkleTree().getRoot() + blockHeader;
		return applySha256ToBlockDataWithMultiple(blockHash);
	}

	/*
	 * This function will calculate the Block hash in hexadecimal format. It applies
	 * SHA-256 hashing algorithm to the block.
	 */
	private String applySha256ToBlockDataWithMultiple(String string) {
		StringBuffer hexDataValue = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// Applying SHA-256 hashing algorithm to the input
			byte[] dataHash = digest.digest(string.getBytes("UTF-8"));
			hexDataValue = new StringBuffer();
			for (int i = 0; i < dataHash.length; i++) {
				String hex = Integer.toHexString(0xff & dataHash[i]);
				if (hex.length() == 1) {
					hexDataValue.append('0');
				}
				hexDataValue.append(hex);
			}
		} catch (Exception e) {
			System.out.println("PeerConnectionService:applySha256ToBlockData:: Exeption: " + e.getMessage());
		}
		return hexDataValue.toString();
	}

	/*
	 * This function calculates the hash for the current block. All blocks have
	 * their hash beginning with 'aaa' as DIFICULTY is 3.
	 */
	private void setBlockHash(BlockStructure block) {
		BlockStructure parentBlock = blockChain.getBlockList().get(blockChain.getBlockList().size() - 1);
		if (parentBlock != null) {
			block.setPreviousHash(parentBlock.getHash());
			// assign this new generated block to the parent
			parentBlock.setNextBlockStructure(block);
		} else {
			block.setPreviousHash(null);
		}
		// Generate the Merkle tree before mining the block
		buildMerkleTree(block);
		block.setHash(mineBlock(block));
	}

	/*
	 * This function calculates the hash of each transaction and stores it in a
	 * list. This list of transaction hashes is the given to the constructor to
	 * generate a MerkleTree from bottom up.
	 */
	private void buildMerkleTree(BlockStructure block) {
		ArrayList<String> transactionHashList = new ArrayList<>();
		ArrayList<Transaction> transactionList = block.getTransactionList();
		if (transactionList.size() == 4) {
			for (Transaction t : transactionList) {
				transactionHashList.add(applySha256ToBlockDataWithMultiple(t.toString()));
			}
			block.setMerkleTree(new MerkleTree(transactionHashList));
		} else {
			System.out.println("PeerConnectionService:buildMerkleTree:: Number of transactions is not 4: " + transactionList.size());
		}
	}

	private void StartMessageHandlerThread(Integer peerIndex) {
		new Thread("" + peerIndex) {
			public void run() {
				BufferedReader dataReader;
				PrintWriter blockWriter;

				// keep listening for incoming connections
				while (true) {
					if (isMiner) {
						System.out.println("Listening for incoming transactions from peer " + getName());
					} else {
						System.out.println("Listening for incoming transactions or blocks from peer" + getName());
					}

					try {
						dataReader = new BufferedReader(new InputStreamReader(
								peerList.get(Integer.parseInt(getName())).getSocket().getInputStream()));
						String data = "";
						data = dataReader.readLine();
						if (data.equals("transaction")) {
							System.out.println("Transaction received from peer " + getName());
							// Not sure if using a different input stream reader after using one stream reader will work
							ObjectInputStream transactionReader = new ObjectInputStream(new BufferedInputStream(
									peerList.get(Integer.parseInt(getName())).getSocket().getInputStream()));
							Transaction transaction = (Transaction) transactionReader.readObject();
							transactionPool.add(transaction);
							System.out.println("Added transaction to pool");
							if (isMiner) {
								if (transactionPool.size() == TX_COUNT_TRESHOLD) {
									BlockStructure block = new BlockStructure(blockChain.nextBlockNumber());
									for (Transaction t : transactionPool) {
										block.addTransactionToBlock(t);
									}
									block.setTimeStamp(new Timestamp(System.currentTimeMillis()));
									if (block == blockChain.getHeadBlock()) {
										block.setPreviousHash(null);
									} else {
										block.setPreviousHash(blockChain.getBlockList()
												.get(blockChain.getBlockList().size() - 1).getHash());
									}
									setBlockHash(block);
									blockChain.acceptIncomingBlock(block);
									blockChain.verifyBlockChain(block.getPreviousHash());

									// broadcast the block to all peers.
									for (PeerInfo p : peerList) {
										Socket peerSocket = p.getSocket();
										blockWriter = new PrintWriter(peerSocket.getOutputStream(), true);
										blockWriter.println("block");
										ObjectOutputStream blockDataWriter = new ObjectOutputStream(
												peerSocket.getOutputStream());
										blockDataWriter.writeObject(block);
									}
									// delete transactions from pool
									transactionPool.clear();
								}
							}
						} else if (data.equals("block")) {
							System.out.println("Block received from peer " + getName());
							// Not sure if using a different input stream reader after using one stream
							// reader will work
							ObjectInputStream Blockreader = new ObjectInputStream(
									peerList.get(Integer.parseInt(getName())).getSocket().getInputStream());
							BlockStructure blockObj = (BlockStructure) Blockreader.readObject();
							// Verify block here with the local transaction pool "Txpool".
							blockChain.acceptIncomingBlock(blockObj);
							blockChain.verifyBlockChain(blockObj.getPreviousHash());
							// delete transactions from pool
							transactionPool.clear();
						}
					} catch (IOException | ClassNotFoundException io) {
						io.printStackTrace();
					}
				}
			}
		}.start();
	}

}
