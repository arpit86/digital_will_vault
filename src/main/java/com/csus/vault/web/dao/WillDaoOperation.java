package com.csus.vault.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;
import com.csus.vault.web.service.BlockManagerService;
import com.csus.vault.web.service.PeerConnectionService;

public class WillDaoOperation {
	
	private JdbcConnection jdbcConn = new JdbcConnection();
	private Connection conn = null;
	private BlockManagerService blockService = null;
	private PeerConnectionService peer = null;
	
	public WillDaoOperation() {
		peer = PeerConnectionService.getInstance();
	}

	/*
	 *  This function will save the encrypted will to the database and
	 *  add user to vault_authorized_user table with update and view rights for the will.
	 */
	public void saveEncryptedWillToDB(byte[] encryptedData, VaultUser user, String willHash) throws SQLException {
		conn = jdbcConn.getConnection();
		if(conn != null && encryptedData != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			try {
				PreparedStatement query = conn.prepareStatement("insert into vault_will_detail(vault_userId, will_createdTS, will_updatedTS, " + 
						"will_content, will_hash) values (?, ?, ?, ?, ?)");
				query.setInt(1, user.getUserId());
				query.setDate(2, (java.sql.Date) new Date());
				query.setDate(3, (java.sql.Date) new Date());
				query.setBytes(4, encryptedData);
				query.setString(5, willHash);
	            query.executeUpdate();
	            conn.commit();
	            query.close();
				System.out.println("WillDaoOperation:saveEncryptedWillToDB:: saved will: " + user.getUserEmail());
				VaultWillDetail willInfo = getWillDetailbyUserId(user.getUserId());
				if(null != willInfo) {
					query = conn.prepareStatement("insert into vault_authorized_user(will_id, vault_userId, authorizedTS, authorized_view, " + 
							"authorized_update) values (?, ?, ?, ?, ?)");
					query.setInt(1, willInfo.getWillId());
					query.setInt(2, user.getUserId());
					query.setDate(3, (java.sql.Date) new Date());
					query.setString(4, "true");
					query.setString(5, "true");
                	query.executeUpdate();
                	conn.commit();
                	query.close();
                	blockService.createBlockWithWillUploadTransaction(willInfo, peer);
                }
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:saveEncryptedWillToDB:: Unable to save the Will Record: Exception: "+ ex.getMessage());
            	conn.rollback();
            } finally {
            	conn.close();
			}
		}
	}

	public VaultWillDetail getWillDetailbyUserId(int userId) throws SQLException {
		VaultWillDetail willElement = null;
		conn = jdbcConn.getConnection();
		if(conn != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			try {
				PreparedStatement query = conn.prepareStatement("select * from vault_will_detail where vault_userId = ?");
            	query.setInt(1,userId);
            	ResultSet rs = query.executeQuery();
            	while(rs.next()) {
            		willElement = new VaultWillDetail();
            		willElement.setWillId(rs.getInt("will_id"));
            		willElement.setVault_userId(userId);
            		willElement.setWill_createdTS(rs.getDate("will_createdTS"));
            		willElement.setWill_updatedTS(rs.getDate("will_updatedTS"));
            		willElement.setWillContent(rs.getBytes("will_content"));
            		willElement.setWillHash(rs.getString("will_hash"));
            	}
            	query.close();
			} catch (Exception ex) {
				System.out.println("WillDaoOperation:getWillDetailbyUserId:: Unable to retrieve the Will Record: Exception: "+ ex.getMessage());
			} finally {
            	conn.close();
			}
		}
		return willElement;
	}

	public void saveModifiedWillToDB(byte[] encryptedData, VaultUser user, String willHash) throws SQLException {
		conn = jdbcConn.getConnection();
		if(conn != null && encryptedData != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			try {
				VaultWillDetail willInfo = getWillDetailbyUserId(user.getUserId());
				PreparedStatement query = conn.prepareStatement("insert into vault_will_detail_history (will_id, vault_userId, will_createdTS, " + 
						"will_updatedTS, will_content, will_hash) values (?, ?, ?, ?, ?, ?)");
				query.setInt(1, willInfo.getWillId());
				query.setInt(2, willInfo.getVault_userId());
				query.setDate(3, (java.sql.Date) willInfo.getWill_createdTS());
				query.setDate(4, (java.sql.Date) new Date());
				query.setBytes(5, willInfo.getWillContent());
				query.setString(6, willInfo.getWillHash());
				query.executeUpdate();
				conn.commit();
				query.close();
				query = conn.prepareStatement("update vault_will_detail SET will_updatedTS=?, will_content=?, will_hash=? where vault_userId=?");
				query.setDate(1, (java.sql.Date) new Date());
				query.setBytes(2, encryptedData);
				query.setString(3, willHash);
				query.setInt(4, user.getUserId());
				query.executeUpdate();
				conn.commit();
				query.close();
				System.out.println("WillDaoOperation:saveModifiedWillToDB:: saved will: " + user.getUserEmail());
				willInfo.setWillHash(willHash);
				willInfo.setWillContent(encryptedData);
				willInfo.setWill_updatedTS(new Date());
				blockService.createBlockWithWillUpdateTransaction(willInfo, peer);
            } catch (Exception ex) {
            	System.out.println("WillDaoOperation:saveModifiedWillToDB:: Unable to save the Will Record: Exception: "+ ex.getMessage());
            } finally {
            	conn.close();
			}
		
		}
	}

	public ArrayList<Integer> getListOfWillWithViewAccess(VaultUser user) throws SQLException {
		ArrayList<Integer> willIdList = new ArrayList<>();
		conn = jdbcConn.getConnection();
		if(conn != null) {
			System.out.println("WillDaoOperation:getListOfWillWithViewAccess:: inside getListOfWillWithViewAccess()");
			try {
				PreparedStatement query = conn.prepareStatement("select will_id from vault_authorized_user where vault_userId=? and authorized_view=?");
				query.setInt(1, user.getUserId());
				query.setString(2, "true");
				ResultSet rs = query.executeQuery();
				while(rs.next()) {
					willIdList.add(rs.getInt("will_id"));
				}
				query.close();
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:getListOfWillWithViewAccess:: Exception: "+ ex.getMessage());
            	throw(ex);
            } finally {
            	conn.close();
			}
		}
		return willIdList;
	}
	
	public VaultWillDetail getWillDetailbyWillId(int willId) throws SQLException {
		VaultWillDetail willElement = new VaultWillDetail();;
		conn = jdbcConn.getConnection();
		if(conn != null) {
			System.out.println("WillDaoOperation:getWillDetailbyWillId:: inside saveEncryptedWillToDB()");
			try {
				PreparedStatement query = conn.prepareStatement("select * from vault_will_detail where will_id=?");
				query.setInt(1, willId);
				ResultSet rs = query.executeQuery();
				while(rs.next()) {
					willElement = new VaultWillDetail();
            		willElement.setWillId(willId);
            		willElement.setVault_userId(rs.getInt("vault_userId"));
            		willElement.setWill_createdTS(rs.getDate("will_createdTS"));
            		willElement.setWill_updatedTS(rs.getDate("will_updatedTS"));
            		willElement.setWillContent(rs.getBytes("will_content"));
            		willElement.setWillHash(rs.getString("will_hash"));
				}
				query.close();
           } catch (Exception ex) {
				System.out.println("WillDaoOperation:getWillDetailbyWillId:: Unable to retrieve the Will Record: Exception: "+ ex.getMessage());
			} finally {
            	conn.close();
			}
		}
		return willElement;
	}

	public String requestOwnerForWill(VaultUser user, int willId) throws SQLException {
		String ownerEmail = "";
		conn = jdbcConn.getConnection();
		if(conn != null) {
			try {
				VaultWillDetail willInfo = getWillDetailbyWillId(willId);
				PreparedStatement query = conn.prepareStatement("select * from vault_user where vault_userId=?");
				query.setInt(1, willInfo.getVault_userId());
				ResultSet rs = query.executeQuery();
				while(rs.next()) {
					ownerEmail = rs.getString("user_email");
				}
				query.close();
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:getListOfWillWithViewAccess:: Exception: "+ ex.getMessage());
            } finally {
            	conn.close();
			}
		}
		return ownerEmail;
	}
}