package com.csus.vault.web.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnection {
	
	// JDBC driver name and database URL
	public final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	public final String DB_URL = "jdbc:mysql://localhost:3306/digitalVaultdb";

   // Database credentials
   static final String USER = "shweta";
   static final String PASS = "Shweta1601";
	   
   public Connection getConnection() {
	   Connection connection = null;
	   try{
		   Class.forName(JDBC_DRIVER);
		   connection = DriverManager.getConnection(DB_URL,USER,PASS);
	   }catch(SQLException ex){
	      //Handle errors for JDBC
	      ex.printStackTrace();
	   }catch(Exception ex){
	      //Handle errors for Class.forName
	      ex.printStackTrace();
	   }
	   return connection;
	}
}