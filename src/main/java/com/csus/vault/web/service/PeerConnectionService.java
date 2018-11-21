package com.csus.vault.web.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.List;

import com.csus.vault.web.block.BlockChain;
import com.csus.vault.web.block.BlockStructure;
import com.csus.vault.web.block.PeerInfo;
import com.csus.vault.web.block.Transaction;

import net.quux00.MerkleTree;

public class PeerConnectionService {

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
		System.out.println(email+" adding transaction "+ T + " to own pool before sending to peers");
		transactionPool.add(T);
		for (PeerInfo p : peerList) {
			try {
			ObjectOutputStream transactionDataWriter;
			System.out.println(email+" sending transaction "+ T + " to peer "+ p.getEmail());
			do
			{
				transactionDataWriter = p.getObjectOutputStream();
			} while(transactionDataWriter==null);
				transactionDataWriter.writeObject(T);
				//transactionDataWriter.flush();
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}
	
	public void sendAllTxtoPeer(Integer peerIndex) {
		try {
			ObjectOutputStream transactionDataWriter = peerList.get(peerIndex).getObjectOutputStream();
		for (Transaction t : transactionPool) {
				System.out.println(email +" sending transaction "+ t + " from pool to new peer "+ peerList.get(peerIndex).getEmail());
				transactionDataWriter.writeObject(t);
				//transactionDataWriter.flush();
				}
		} catch (IOException io) {
			io.printStackTrace();
		} 
	}
	
	public void sendAllBlockstoPeer(Integer peerIndex) {
		try {
			ObjectOutputStream BlockDataWriter = peerList.get(peerIndex).getObjectOutputStream();
			for (BlockStructure b : blockChain.getBlockList()) {
				System.out.println(email +" sending block "+ b + " from blockchain to new peer "+ peerList.get(peerIndex).getEmail());
				BlockDataWriter.writeObject(b);	
				//BlockDataWriter.flush();
				}
		} catch (IOException io) {
			io.printStackTrace();
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
				ObjectOutputStream ObjectWriter = new ObjectOutputStream(peerSocket.getOutputStream());
				//outWriter = new PrintWriter(peerSocket.getOutputStream(), true);
				ObjectWriter.writeUTF(email);
				p.setSocket(peerSocket);				
				ObjectInputStream ObjectReader = new ObjectInputStream(new BufferedInputStream(peerSocket.getInputStream()));
				p.setObjectInputStream(ObjectReader);
				p.setObjectOutputStream(ObjectWriter);
				System.out.println(email+ " connected to peer " + p.getEmail());
				StartMessageHandlerThread(peerList.indexOf(p));
			}
			StartListenThread();
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
			blockInfo.setNonce(++nonceTemp);
			blockHash = calculateHashWithMultiple(blockInfo);
		}
		System.out.println("Mined value: " + blockHash);
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
		if (blockChain.getHeadBlock()==null)
		{
			block.setPreviousHash(null);
		} else
		{
			BlockStructure parentBlock = blockChain.getBlockList().get(blockChain.getBlockList().size() - 1);	
			block.setPreviousHash(parentBlock.getHash());
			// assign this new generated block to the parent
			parentBlock.setNextBlockStructure(block);
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
		List<String> transactionHashList = new ArrayList<String>();
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
				try {
				// keep listening for incoming connections
					//ObjectInputStream ObjectReader = new ObjectInputStream(new BufferedInputStream(peerList.get(Integer.parseInt(getName())).getSocket().getInputStream()));
				ObjectInputStream ObjectReader = peerList.get(Integer.parseInt(getName())).getObjectInputStream();
				int result = 0;
				while (true) {
					if (isMiner) {
						System.out.println(email+" listening for incoming transactions from peer " + peerList.get(Integer.parseInt(getName())).getEmail());
					} else {
						System.out.println(email+" listening for incoming transactions or blocks from peer " + peerList.get(Integer.parseInt(getName())).getEmail());
					}
					Object DataObject =  ObjectReader.readObject();
						if (DataObject instanceof Transaction) {
							System.out.println(email+ " received transaction from peer " + peerList.get(Integer.parseInt(getName())).getEmail());
							Transaction transaction = (Transaction) DataObject;
							if (transactionPool.contains(transaction))
							{
								System.out.println("Transaction "+transaction+" recieved by "+ email+ " from "+peerList.get(Integer.parseInt(getName())).getEmail()+ " already present in pool. Ignoring.");
							}
							else
							{
								transactionPool.add(transaction);
								System.out.println("Transaction "+transaction+" recieved by "+ email+ " from "+peerList.get(Integer.parseInt(getName())).getEmail()+ " added to pool.");
							}
							if (isMiner) {
								if (transactionPool.size() == TX_COUNT_TRESHOLD) {
									BlockStructure block = new BlockStructure(blockChain.nextBlockNumber());
									for (Transaction t : transactionPool) {
										block.addTransactionToBlock(t);
									}
									//already setting timestamp in constructor
									//block.setTimeStamp(new Timestamp(System.currentTimeMillis()));
									setBlockHash(block);
									blockChain.acceptIncomingBlock(block);
									UpdateBlockChainFile(block);
									//blockChain.verifyBlockChain(block.getPreviousHash());
									System.out.println("miner Waiting for 10 seconds before broadcasting block");
									Thread.sleep(10000);
									// broadcast the block to all peers.
									for (PeerInfo p : peerList) {
										ObjectOutputStream DataObjectWriter;
										do
										{
											DataObjectWriter = p.getObjectOutputStream();
										} while(DataObjectWriter==null);										
										DataObjectWriter.writeObject(block);
										//DataObjectWriter.flush();
									}
									// delete transactions from pool
									transactionPool.clear();
								}
							}
						} else if (DataObject instanceof BlockStructure) {
							System.out.println("Block recieved by "+ email+ " from "+peerList.get(Integer.parseInt(getName())).getEmail());
							BlockStructure blockObj = (BlockStructure) DataObject;
							// Verify block here with the local transaction pool "Txpool".
							System.out.println(email + " verifying transactions in block recieved from "+peerList.get(Integer.parseInt(getName())).getEmail());
							VerifyandAddBlock(blockObj);
						}					
				}
				} catch (IOException | ClassNotFoundException | InterruptedException io) {
					io.printStackTrace();
				}
			}
		}.start();
	}
	
	private synchronized void VerifyandAddBlock(BlockStructure block) throws IOException {
		 int result = verifyBlockTransactions(block);
			if (result == 0 || result == 1)
			{
				int status = blockChain.verifyBlock(block);
				if (status == 1)
					System.out.println(email + " genesis block expected and non-genesis block received.");
				else if (status == 2)
					System.out.println(email + " hash of current block does not match the prevhash of received block.");
				else if (status == 3)
					System.out.println(email + " block failed nonce verification.");
				else
				{
					System.out.println(email + " block passed verification. Added to chain.");
					blockChain.acceptIncomingBlock(block);
					UpdateBlockChainFile(block);
					if(result == 0)
					{
						// delete transactions from pool if a new mined block is received
						transactionPool.clear();
					}
				}
			} 
			else if(result == 2)
			{
				System.out.println(email + " Some transactions in the block not found in pool. Rejecting block.");
			}
	}
	
	private void UpdateBlockChainFile(BlockStructure block) throws IOException {
		BufferedWriter bw = null;
		FileWriter fw = null;
		String s;
		File file = new File(email+"_blockchain.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		fw = new FileWriter(file.getAbsoluteFile(), true);
		bw = new BufferedWriter(fw);
		
		bw.write("Block number: "+block.getBlockNumber());
		bw.newLine();
		bw.write("Block hash: "+block.getHash());
		bw.newLine();
		bw.write("Block nonce: "+block.getNonce());
		bw.newLine();
		bw.write("Previous Block hash: "+block.getPreviousHash());
		bw.newLine();
		bw.write("Block timestamp: "+block.getTimeStamp());
		bw.newLine();
		bw.write("Transactions in Block: "+block.getTransactionList().size());
		bw.newLine();
		for (Transaction t : block.getTransactionList()) {
			bw.write("Transaction " + block.getTransactionList().indexOf(t)+" type: " +t.getTransactionType());
			bw.newLine();
			bw.write("Transaction " + block.getTransactionList().indexOf(t)+" userid: " +t.getVault_userId());
			bw.newLine();
			bw.write("Transaction " + block.getTransactionList().indexOf(t)+" willid: " +t.getWillId());
			bw.newLine();
			bw.write("Transaction " + block.getTransactionList().indexOf(t)+" hash: " +t.hashCode());
			bw.newLine();
			s = new String(t.getPublicKeyOrWillHash());
			bw.write("Transaction " + block.getTransactionList().indexOf(t)+" publickeyorhash: " +s);
			bw.newLine();
			bw.write("Transaction " + block.getTransactionList().indexOf(t)+" timestamp: " +t.getTransactionTS());
			bw.newLine();
		}
		bw.newLine();
		bw.newLine();
		bw.close();
	}
	
	private int verifyBlockTransactions(BlockStructure block) {
		int txcount = 0;
		int txnotpresentcount = 0;
		System.out.println(email+ " tx pool size= "+ transactionPool.size());
		for (Transaction t : block.getTransactionList()) {
			System.out.println(email+ " verifying tx "+ ++txcount);
			if(!transactionPool.contains(t))
			{
				System.out.println(email+ " tx not present in pool.");
				txnotpresentcount++;
			}
			else
			{
				System.out.println(email+ " tx present "+ txcount);
			}
		}
		if (txnotpresentcount == TX_COUNT_TRESHOLD)
		{
			return 1;
		}
		else if(txnotpresentcount==0)
		{
			System.out.println(email+ " verified all tx in block.");
			return 0;
		}
		return 2;
	}
	
	private void StartListenThread() {
		new Thread("listen_server") {
			public void run() {
				try {
				// Start server socket for listening at same port used for connection to boot node
				ServerSocket serverSocket = new ServerSocket(port);
				// keep listening for incoming connections
				while (true) {
					System.out.println(email + " Server listening for connection on port: " + port);
					// accept is a blocking call meaning that code won't execute further until a
					// peer connects to server
					Socket socket = serverSocket.accept();	
					ObjectOutputStream ObjectWriter = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream ObjectReader = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					// takes input from the client socket
					String data = "";
					try {
						data = ObjectReader.readUTF();
						System.out.println(email+" server recieved connection request on port: " + socket.getPort()+" from user "+ data);

						// create a peer object to store this peer's data
						PeerInfo peer = new PeerInfo();
						peer.setEmail(data);
						//ObjectInputStream ObjectReader = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
						// get the peer's port number from socket
						peer.setPort(socket.getPort());
						peer.setSocket(socket);
						peer.setObjectInputStream(ObjectReader);
						peer.setObjectOutputStream(ObjectWriter);
						// Adding the peer to the Arraylist
						peerList.add(peer);
						System.out.println(email + " Connected to " + peer.getEmail() + " on port " + socket.getPort());
						//send all transactions from pool to this peer
						System.out.println(email+" sending all transaction from pool to " + peer.getEmail() + " on port " + socket.getPort());
						sendAllBlockstoPeer(peerList.indexOf(peer));
						sendAllTxtoPeer(peerList.indexOf(peer));
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
		}.start();
	}

}
