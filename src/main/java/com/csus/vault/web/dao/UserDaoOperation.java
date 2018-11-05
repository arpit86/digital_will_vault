package com.csus.vault.web.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

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
            	//VaultUser userElement = (VaultUser) query.executeQuery();
                if(null != rs && rs.getString("user_email").equalsIgnoreCase(user.getUserEmail())){
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
            } catch (Exception ex) {
            	System.out.println("UserDaoOperation:verify:: Exeption: " + ex.getMessage());
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
				PreparedStatement query = conn.prepareStatement("insert into vault_user (user_id, user_firstName, user_lastName, " + 
						"user_phone, user_password, password_salt, user_publicKey, user_createdTS, user_updatedTS, user_email) " + 
						"VALUES (, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				query.setString(2, user.getUser_firstName());
				query.setString(3, user.getUser_lastName());
				query.setString(4, user.getUserPhone());
				query.setString(5, user.getUserPassword());
				query.setString(6, user.getPasswordSalt());
				query.setBytes(7, user.getUser_publicKey());
				query.setDate(8, (Date) user.getUser_createdTS());
				query.setDate(9, (Date) user.getUser_updatedTS());
				query.setString(10, user.getUserEmail());
				query.executeUpdate();
				System.out.println("UserDaoOperation:register:: saved user: " + user.getUserEmail());
			} catch (Exception ex) {
            	System.out.println("UserDaoOperation:register:: Unable to register the User Record: Exception: "+ ex.getMessage());
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
            	PreparedStatement query = conn.prepareStatement("select * from VaultUser where userEmail = ?");
            	query.setString(1, userEmail);
            	user = (VaultUser) query.executeQuery();
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
				PreparedStatement query = conn.prepareStatement("UPDATE VaultUser SET userPassword=?, passwordSalt=?, user_updatedTS=? "+
						"WHERE userEmail=?");
				query.setString(1, user.getUserPassword());
				query.setString(2, user.getPasswordSalt());
				query.setDate(3, (Date) user.getUser_updatedTS());
				query.setString(4, user.getUserEmail());
				query.executeUpdate();
				System.out.println("UserDaoOperation:update:: updated user: " + user.getUserEmail());
			} catch (Exception ex) {
            	System.out.println("UserDaoOperation:update:: Unable to update the User Record: Exception: "+ ex.getMessage());
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
				PreparedStatement query = conn.prepareStatement("insert into VaultAuthorizedUser (authorized_userId, willId, vault_userId, " + 
						"authorizedTS, authorizedView, authorizedUpdate) VALUES ( , ?, ?, ?, ?, ?)");
				query.setInt(2, authUser.getWillId());
				query.setInt(3, authUser.getVault_userId());
				query.setDate(4, (Date) authUser.getAuthorizedTS());
				query.setString(5, authUser.getAuthorizedView());
				query.setString(6, authUser.getAuthorizedUpdate());
				query.executeUpdate();
				System.out.println("UserDaoOperation:saveAuthorizedUser:: saved authorized user: " + authUser.getVault_userId());
			} catch (Exception ex) {
            	System.out.println("UserDaoOperation:saveAuthorizedUser:: Unable to save the User Record: Exception: "+ ex.getMessage());
            } finally {
            	conn.close();
            }
		}
		
	}

}
