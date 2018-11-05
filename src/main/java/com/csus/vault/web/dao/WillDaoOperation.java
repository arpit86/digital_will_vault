package com.csus.vault.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;
import com.csus.vault.web.model.VaultWillDetailHistory;
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

	public void saveModifiedWillToDB(byte[] encryptedData, VaultUser user, String willHash) {
		if(entityManagerFactory != null && encryptedData != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			manager = entityManagerFactory.createEntityManager();			
			try {
				VaultWillDetail willInfo = getWillDetailbyUserId(user.getUserId());
				VaultWillDetailHistory histInfo = new VaultWillDetailHistory();
				histInfo.setWill_id(willInfo.getWillId());
				histInfo.setVault_userId(willInfo.getVault_userId());
				histInfo.setWill_createdTS(willInfo.getWill_createdTS());
				histInfo.setWill_updatedTS(new Date());
				histInfo.setWillContent(willInfo.getWillContent());
				histInfo.setWillHash(willInfo.getWillHash());
				manager.persist(histInfo);
				willInfo.setWill_updatedTS(new Date());
				willInfo.setWillContent(encryptedData);
				willInfo.setWillHash(willHash);
				manager.persist(willInfo);
				System.out.println("WillDaoOperation:saveModifiedWillToDB:: saved will: " + user.getUserEmail());
				blockService.createBlockWithWillUpdateTransaction(willInfo, peer);
            } catch (Exception ex) {
            	System.out.println("WillDaoOperation:saveModifiedWillToDB:: Unable to save the Will Record: Exception: "+ ex.getMessage());
            	throw(ex);
            } finally {
            	if(manager!= null)
            		manager.close();
			}
		
		}
	}

	@SuppressWarnings("rawtypes")
	public ArrayList getListOfWillWithViewAccess(VaultUser user) {
		ArrayList willIdList = null;
		if(entityManagerFactory != null) {
			manager = entityManagerFactory.createEntityManager();
			System.out.println("WillDaoOperation:getListOfWillWithViewAccess:: inside getListOfWillWithViewAccess()");
			try {
				Query query = manager.createNativeQuery("select willId from VaultAuthorizedUser where vault_userId = :vault_userId and authorizedView = :authorizedView");
				query.setParameter("vault_userId", user.getUserId());
				query.setParameter("authorizedView", "true");
				willIdList = (ArrayList) query.getResultList();
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:getListOfWillWithViewAccess:: Exception: "+ ex.getMessage());
            	throw(ex);
            }
		}
		return willIdList;
	}
	
	public VaultWillDetail getWillDetailbyWillId(int willId) {
		VaultWillDetail willElement = null;	
		if(entityManagerFactory != null) {
			manager = entityManagerFactory.createEntityManager();
			System.out.println("WillDaoOperation:getWillDetailbyWillId:: inside saveEncryptedWillToDB()");
			try {
				willElement = manager.find(VaultWillDetail.class, willId);
                if(null != willElement) {
                	return willElement;
                }
			} catch (Exception ex) {
				System.out.println("WillDaoOperation:getWillDetailbyWillId:: Unable to retrieve the Will Record: Exception: "+ ex.getMessage());
				throw(ex);
			} finally {
            	if(manager!= null)
            		manager.close();
			}
		}
		return willElement;
	}

	public String requestOwnerForWill(VaultUser user, int willId) {
		String ownerEmail = "";
		if(entityManagerFactory != null) {
			manager = entityManagerFactory.createEntityManager();
			try {
				VaultWillDetail willInfo = getWillDetailbyWillId(willId);
				Query query = manager.createNativeQuery("select u from VaultUser u where u.vault_userId = :will_OwnerId");
				query.setParameter("will_OwnerId", willInfo.getVault_userId());
				VaultUser owner = (VaultUser) query.getSingleResult();
				ownerEmail = owner.getUserEmail();
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:getListOfWillWithViewAccess:: Exception: "+ ex.getMessage());
            	throw(ex);
            } finally {
            	if(manager!= null)
            		manager.close();
			}
		}
		return ownerEmail;
	}
}