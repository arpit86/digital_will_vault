package com.csus.vault.web.block;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PeerInfo {
	
	int port;
	String email;
	Socket socket;
	ObjectInputStream ObjectReader;
	ObjectOutputStream ObjectWriter;
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void setObjectInputStream(ObjectInputStream Ois) {
		ObjectReader = Ois;
	}
	
	public void setObjectOutputStream(ObjectOutputStream Oos) {
		ObjectWriter = Oos;
	}
	
	public ObjectInputStream getObjectInputStream() {
		return ObjectReader;
	}
	
	public ObjectOutputStream getObjectOutputStream() {
		return ObjectWriter;
	}
}