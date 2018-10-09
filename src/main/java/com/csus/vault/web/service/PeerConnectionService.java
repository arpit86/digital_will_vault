package com.csus.vault.web.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.csus.vault.web.model.DigitalWillBlock;
import com.csus.vault.web.model.PeerInfo;

public class PeerConnectionService extends Thread{
	
	public static final int BOOT_PORT = 2999;
	private ArrayList <PeerInfo> peerList;
	private String email = "";
	private Integer port;
	private DigitalWillBlock block;
	private PrintWriter outWriter; 
	private BufferedReader inReader;
	PeerInfo peerInfo;
	 
	
	@SuppressWarnings("resource")
	public PeerConnectionService(String email, DigitalWillBlock block){
    	this.email = email;
    	this.block = block;
    	this.peerList = new ArrayList<PeerInfo>();
    	
    	// Establish a connection with boot node server on port 2999
        try {
        	Socket socket = new Socket("127.0.0.1", BOOT_PORT); 
            System.out.println("Connected to boot server"); 
            
            this.port= socket.getLocalPort();
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
            while (!data.equals("End")){
                try{
                	//if (inReader.ready()) {
                		peerEmail = data; 
                		System.out.println(peerEmail); 
                		peerPort = inReader.readLine();
                		System.out.println("received peer's port from server");
                		System.out.println(peerPort);
               	              
               			//Create an object to store the peer's information
               			peerInfo = new PeerInfo();
               			//get peer's email id
               			peerInfo.setEmail(peerEmail);
               			//get the peer's port number
               			peerInfo.setPort(Integer.parseInt(peerPort));
               			//peerInfo.setSocket(peerSocket);
               			peerList.add(peerInfo);
               			data = inReader.readLine();
               			System.out.println("received next peer's email or end from server");
                   	//}
                } catch(IOException io) { 
                    io.printStackTrace(); 
                }
    		} //end of while
            socket.close();
        } catch(UnknownHostException u){ 
            u.printStackTrace(); 
        } catch(IOException io) { 
            io.printStackTrace(); 
        }
	}
	
	/*public void sendToAll(DigitalWillBlock block){
    	for(PeerInfo p: peerList)	{
				//for each peer in the list broadcast the block				
		}
	}*/
	 
	/*public static void main(String[] args) throws IOException{
		//PeerClient.jar <email id> <block>. This is how peer client should be called by the GUI
		PeerClient peer = new PeerClient(args[0], args[1]);
		//start the server listening thread
		peer.run();
	}*/
	
	@SuppressWarnings("resource")
	public void run(){
		try {
			
			for(PeerInfo p: peerList) {
				Socket peerSocket = new Socket("127.0.0.1", p.getPort()); 
        		outWriter = new PrintWriter(peerSocket.getOutputStream(), true);
        		outWriter.println(email);
        		p.setSocket(peerSocket);
        		System.out.println("Connected to peer "+ p.getEmail());
			}
			
			//Start server socket for listening at same port used for connection to boot node
			ServerSocket serverSocket = new ServerSocket(port);
				
			//keep listening for incoming connections
			while(true) {
				System.out.println(email+" Server listening for connections on port: "+port);
				//accept is a blocking call meaning that code won't execute further until a peer connects to server
				Socket socket = serverSocket.accept();
									
				//peer connected at this point
				System.out.println("Connection was established on port: "+ socket.getPort());
				
				// takes input from the client socket 
		        inReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
		        String data = "";
		  		
		        	try { 
		        		data = inReader.readLine(); 
		        		System.out.println(email+" received connection request from user "+data);
		                	//create a peer object to store this peer's data
		    				PeerInfo peer = new PeerInfo();
		                	peer.setEmail(data);
		                	//get the peer's port number from socket
		    				peer.setPort(socket.getPort());
		    				peer.setSocket(socket);
		    				//Adding the peer to the Arraylist
		    				peerList.add(peer);
		    				System.out.println("Connected to "+ peer.getEmail() +" on port "+ socket.getPort());
		            } catch(IOException io){ 
		                io.printStackTrace(); 
		            } 
		            
		        //create a peer object to store this peer's data
				/*PeerInfo peer = new PeerInfo();
				
				//the only data the peer sends to other peer for now is his email id
				peer.setEmail(data);
				//get the peer's port number from socket
				peer.setPort(socket.getPort());
				peer.setSocket(socket);
				//Adding the peer to the Arraylist
				peerList.add(peer);*/
				//System.out.println("The peer trying to connect is: "+ peer.getEmail() +" on the port: "+ socket.getPort());
			} // end of while loop
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}