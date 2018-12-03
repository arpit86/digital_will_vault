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
	
	/*
	 *  This function will save the encrypted will to the database and
	 *  add user to vault_authorized_user table with update and view rights for the will.
	 */
	public VaultWillDetail  saveEncryptedWillToDB(byte[] encryptedData, VaultUser user, String willHash) throws SQLException {
		VaultWillDetail willInfo = new VaultWillDetail();
		conn = jdbcConn.getConnection();
		if(conn != null && encryptedData != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			try {
				PreparedStatement query = conn.prepareStatement("insert into vault_will_detail(vault_userId, will_createdTS, will_updatedTS, " + 
						"will_content, will_hash) values (?, now(), now(), ?, ?)");
				query.setInt(1, user.getUserId());
				query.setBytes(2, encryptedData);
				query.setString(3, willHash);
	            query.executeUpdate();
	            System.out.println("WillDaoOperation:saveEncryptedWillToDB:: saved will: " + user.getUserEmail());
				willInfo = getWillDetailbyUserId(user.getUserId());
				if(null != willInfo) {
					addAuthorizedUserToDb(willInfo.getWillId(), user.getUserId());
                }
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:saveEncryptedWillToDB:: Unable to save the Will Record: Exception: "+ ex.getMessage());
            } finally {
            	conn.close();
			}
		}
		return willInfo;
	}

	private void addAuthorizedUserToDb(int will_id, int user_id) throws SQLException {
		conn = jdbcConn.getConnection();
		if(conn != null) {
			System.out.println("WillDaoOperation:addAuthorizedUserToDb:: inside addAuthorizedUserToDb()");
			try {
				PreparedStatement query = conn.prepareStatement("insert into vault_authorized_user(will_id, vault_userId, authorizedTS, authorized_view, " 
																+ "authorized_update) values (?, ?, now(), ?, ?)");
				query.setInt(1, will_id);
				query.setInt(2, user_id);
				query.setString(3, "true");
				query.setString(4, "true");
				query.executeUpdate();
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:saveEncryptedWillToDB:: Unable to save the Will Record: Exception: "+ ex.getMessage());
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
				PreparedStatement query = conn.prepareStatement("select * from vault_will_detail where vault_userId=?");
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

	public void saveModifiedWillToDB(byte[] encryptedData, VaultUser user, String willHash, PeerConnectionService peer) throws SQLException {
		VaultWillDetail willInfo = getWillDetailbyUserId(user.getUserId());
		conn = jdbcConn.getConnection();
		if(conn != null && encryptedData != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			try {
				PreparedStatement query = conn.prepareStatement("insert into vault_will_detail_history (will_id, vault_userId, will_createdTS, " + 
						"will_updatedTS, will_content, will_hash) values (?, ?, ?, now(), ?, ?)");
				query.setInt(1, willInfo.getWillId());
				query.setInt(2, willInfo.getVault_userId());
				query.setDate(3, (java.sql.Date) willInfo.getWill_createdTS());
				query.setBytes(4, willInfo.getWillContent());
				query.setString(5, willInfo.getWillHash());
				query.executeUpdate();
				updateWillInDB(encryptedData, user, willHash, peer);
				System.out.println("WillDaoOperation:saveModifiedWillToDB:: saved will: " + user.getUserEmail());
				willInfo.setWillHash(willHash);
				willInfo.setWillContent(encryptedData);
				willInfo.setWill_updatedTS(new Date());
				blockService = new BlockManagerService();
				blockService.createBlockWithWillUpdateTransaction(willInfo, peer);
            } catch (Exception ex) {
            	System.out.println("WillDaoOperation:saveModifiedWillToDB:: Unable to save the Will Record: Exception: "+ ex.getMessage());
            } finally {
            	conn.close();
			}
		}
	}

	private void updateWillInDB(byte[] encryptedData, VaultUser user, String willHash, PeerConnectionService peer) throws SQLException {
		conn = jdbcConn.getConnection();
		if(conn != null && encryptedData != null) {
			try {
				PreparedStatement query = conn.prepareStatement("update vault_will_detail SET will_updatedTS=now(), will_content=?, will_hash=? where vault_userId=?");
				query.setBytes(1, encryptedData);
				query.setString(2, willHash);
				query.setInt(3, user.getUserId());
				query.executeUpdate();
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:updateWillInDB:: Unable to update the Will Record: Exception: "+ ex.getMessage());
            } finally {
            	conn.close();
			}
		}
	}

	public ArrayList<String> getListOfWillWithViewAccess(VaultUser user) throws SQLException {
		ArrayList<String> willIdList = new ArrayList<>();
		conn = jdbcConn.getConnection();
		if(conn != null) {
			System.out.println("WillDaoOperation:getListOfWillWithViewAccess:: inside getListOfWillWithViewAccess()");
			try {
				PreparedStatement query = conn.prepareStatement("select concat(user_firstname, ' ', user_lastname ) AS fullname from vault_user where user_id in "
						+ "(select w.vault_userId from vault_will_detail w, vault_authorized_user a where a.vault_userId=? and w.will_id=a.will_id and a.authorized_view=?)");
				query.setInt(1, user.getUserId());
				query.setString(2, "true");
				ResultSet rs = query.executeQuery();
				while(rs.next()) {
					willIdList.add(rs.getString(1));
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
		VaultWillDetail willElement = new VaultWillDetail();
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

	public String requestOwnerForWill(VaultUser user, String willOwnerName) throws SQLException {
		String ownerData = "";
		VaultWillDetail willInfo = getWillDetailbyWillOwner(willOwnerName.trim());
		conn = jdbcConn.getConnection();
		if(conn != null) {
			try {
				PreparedStatement query = conn.prepareStatement("select * from vault_user where user_id=?");
				query.setInt(1, willInfo.getVault_userId());
				ResultSet rs = query.executeQuery();
				while(rs.next()) {
					ownerData = rs.getString("user_email");
				}
				ownerData = ownerData + ":" + willInfo.getWillId();
				query.close();
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:getListOfWillWithViewAccess:: Exception: "+ ex.getMessage());
            } finally {
            	conn.close();
			}
		}
		return ownerData;
	}

	private VaultWillDetail getWillDetailbyWillOwner(String willOwnerName) throws SQLException {
		String firstName = willOwnerName;
		String lastName = "";
		if(willOwnerName.contains(" ")) {
			String[] splitName = willOwnerName.split(" ");
			firstName = splitName[0];
			lastName = splitName[1];
		}
		VaultWillDetail willElement = new VaultWillDetail();
		conn = jdbcConn.getConnection();
		if(conn != null) {
			System.out.println("WillDaoOperation:getWillDetailbyWillId:: inside saveEncryptedWillToDB()");
			try {
				PreparedStatement query = conn.prepareStatement("select user_id from vault_user where user_firstName=? and user_lastName=?");
				query.setString(1, firstName);
				query.setString(2, lastName);
				ResultSet rs = query.executeQuery();
				while(rs.next()) {
					int userId = rs.getInt("user_id");
					willElement = getWillDetailbyUserId(userId);
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
}