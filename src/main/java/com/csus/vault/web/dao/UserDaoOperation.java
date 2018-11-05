package com.csus.vault.web.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;

public class UserDaoOperation {
	
	private JdbcConnection jdbcConn = new JdbcConnection();
	private Connection conn = null;
	
	
	/*
	 *  This function checks whether a user exists in the database.
	 *  @return isPresent = new: if user not present
	 *  				    authorizeUser: if user exists but does not have a password
	 *  					exist: if user is present in database
	 */
	public String verify(VaultUser user) throws SQLException {
		String isPresent = "";
		conn = jdbcConn.getConnection();
		if (null != conn && null != user) {
			System.out.println("UserDaoOperation:verify:: inside verify()");
			try {
            	PreparedStatement query = conn.prepareStatement("select * from vault_user where user_email = ?");
            	query.setString(1, user.getUserEmail());
            	ResultSet rs = query.executeQuery();
            	while(rs.next()) {
	            	if(rs.getString("user_email").equalsIgnoreCase(user.getUserEmail())){
	                	System.out.println("UserDaoOperation:verify:: user already present in database.");
	                	isPresent = "exist";
	                	if(rs.getString("user_password").isEmpty()) {
	                		System.out.println("UserDaoOperation:verify:: user already present in database but is an authorized user");
	                		isPresent="authorizeUser";
	                	}
	                } else {
	                	System.out.println("UserDaoOperation:verify:: user does not exist in database.");
	                	isPresent = "new";
	                }
            	}
            	query.close();
            } catch (Exception ex) {
            	System.out.println("UserDaoOperation:verify:: Exeption: " + ex.getMessage());
            	throw(ex);
            } finally {
            	conn.close();
            }
		}
	    return isPresent;
	}
	
	/*
	 * This function saves a new user to the database.
	 */
	public void register(VaultUser user) throws SQLException {
		conn = jdbcConn.getConnection();
		if(conn != null && user != null) {
			System.out.println("UserDaoOperation:register:: inside register()");
			try {
				PreparedStatement query = conn.prepareStatement("insert into vault_user (user_firstName, user_lastName, " + 
						"user_phone, user_password, password_salt, user_publicKey, user_createdTS, user_updatedTS, user_email) " + 
						"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
				query.setString(1, user.getUser_firstName());
				query.setString(2, user.getUser_lastName());
				query.setString(3, user.getUserPhone());
				query.setString(4, user.getUserPassword());
				query.setString(5, user.getPasswordSalt());
				query.setBytes(6, user.getUser_publicKey());
				query.setDate(7, (Date) user.getUser_createdTS());
				query.setDate(8, (Date) user.getUser_updatedTS());
				query.setString(9, user.getUserEmail());
				query.executeUpdate();
				System.out.println("UserDaoOperation:register:: saved user: " + user.getUserEmail());
				conn.commit();
				query.close();
			} catch (Exception ex) {
            	System.out.println("UserDaoOperation:register:: Unable to register the User Record: Exception: "+ ex.getMessage());
            	conn.rollback();
            } finally {
            	conn.close();
            }
		}
	}

	/*
	 *  This function retrieves the user detail for the given email id from database.
	 */
	public VaultUser getUserDetailByEmail(String userEmail) throws SQLException {
		VaultUser user = null;
		conn = jdbcConn.getConnection();
		if (null != conn && !userEmail.isEmpty()) {
			System.out.println("UserDaoOperation:getUserDetailByEmail:: inside verify()");
            try {
            	PreparedStatement query = conn.prepareStatement("select * from vault_user where userEmail = ?");
            	query.setString(1, userEmail);
            	ResultSet rs = query.executeQuery();
            	while(rs.next()) {
            		user = new VaultUser();
            		user.setUserId(rs.getInt("user_id"));
            		user.setPasswordSalt(rs.getString("password_salt"));
            		user.setUser_createdTS(rs.getDate("user_createdTS"));
            		user.setUser_firstName(rs.getString("user_firstName"));
            		user.setUser_lastName(rs.getString("user_lastName"));
            		user.setUser_publicKey(rs.getBytes("user_publicKey"));
            		user.setUser_updatedTS(rs.getDate("user_updatedTS"));
            		user.setUserEmail(rs.getString("user_email"));
            		user.setUserPassword(rs.getString("user_password"));
            		user.setUserPhone(rs.getString("user_phone"));
            	}
            	query.close();
            } catch (Exception ex) {
            	System.out.println("UserDaoOperation:getUserDetailByEmail:: Exeption: " + ex.getMessage());
            } finally {
            	conn.close();
            }
		}
		return user;
	}

	/*
	 *  This function updates the user attributes if modified.
	 */
	public void update(VaultUser user) throws SQLException {
		conn = jdbcConn.getConnection();
		if(conn != null && user != null) {
			System.out.println("UserDaoOperation:register:: inside register()");
			try {
				PreparedStatement query = conn.prepareStatement("update vault_user SET user_password=?, password_salt=?, user_updatedTS=? "+
						"WHERE user_email=?");
				query.setString(1, user.getUserPassword());
				query.setString(2, user.getPasswordSalt());
				query.setDate(3, (Date) user.getUser_updatedTS());
				query.setString(4, user.getUserEmail());
				query.executeUpdate();
				conn.commit();
				System.out.println("UserDaoOperation:update:: updated user: " + user.getUserEmail());
				query.close();
			} catch (Exception ex) {
            	System.out.println("UserDaoOperation:update:: Unable to update the User Record: Exception: "+ ex.getMessage());
            	conn.rollback();
            } finally {
            	conn.close();
            }
		}
	}

	public void saveAuthorizedUser(VaultAuthorizedUser authUser) throws SQLException {
		conn = jdbcConn.getConnection();
		if(conn != null && authUser != null) {
			System.out.println("UserDaoOperation:register:: inside register()");
			try {
				PreparedStatement query = conn.prepareStatement("insert into VaultAuthorizedUser (willId, vault_userId, " + 
						"authorizedTS, authorizedView, authorizedUpdate) VALUES (?, ?, ?, ?, ?)");
				query.setInt(1, authUser.getWillId());
				query.setInt(2, authUser.getVault_userId());
				query.setDate(3, (Date) authUser.getAuthorizedTS());
				query.setString(4, authUser.getAuthorizedView());
				query.setString(5, authUser.getAuthorizedUpdate());
				query.executeUpdate();
				System.out.println("UserDaoOperation:saveAuthorizedUser:: saved authorized user: " + authUser.getVault_userId());
				conn.commit();
				query.close();
			} catch (Exception ex) {
            	System.out.println("UserDaoOperation:saveAuthorizedUser:: Unable to save the User Record: Exception: "+ ex.getMessage());
            	conn.rollback();
            } finally {
            	conn.close();
            }
		}
	}
}