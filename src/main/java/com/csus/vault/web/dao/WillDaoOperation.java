package com.csus.vault.web.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;
import com.csus.vault.web.service.BlockManagerService;

@Repository
@Transactional
public class WillDaoOperation {
	
	@PersistenceContext
	private EntityManager manager;
	
	private BlockManagerService blockService = null;
	

	/*
	 *  This function will save the encrypted will to the database and
	 *  add user to vault_authorized_user table with update and view rights for the will.
	 */
	public void saveEncryptedWillToDB(byte[] encryptedData, VaultUser user) {
		if(manager != null && encryptedData != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
						
			try {
				VaultWillDetail will = new VaultWillDetail();
				will.setVault_userId(user.getUserId());
				will.setWill_createdTS(new Date());
				will.setWill_updatedTS(new Date());
				will.setWillContent(encryptedData);
				VaultAuthorizedUser authUser = new VaultAuthorizedUser();
				manager.persist(will);
				System.out.println("WillDaoOperation:saveEncryptedWillToDB:: saved will: " + user.getUserEmail());
				VaultWillDetail willInfo = getWillDetailbyUserId(user.getUserId());
				if(null != willInfo) {
                	authUser.setAuthorizedTS(new Date());
                	authUser.setVault_userId(will.getVault_userId());
                	authUser.setWillId(will.getWillId());
                	authUser.setAuthorizedUpdate("true");
                	authUser.setAuthorizedView("true");
                	manager.persist(authUser);
                	blockService.createBlockWithWillUploadTransaction(willInfo);
                }
			} catch (Exception ex) {
            	System.out.println("WillDaoOperation:saveEncryptedWillToDB:: Unable to save the Will Record: Exception: "+ ex.getMessage());
            	throw(ex);
            }
		}
	}


	public VaultWillDetail getWillDetailbyUserId(int userId) {
		VaultWillDetail willElement = null;	
		if(manager != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			
			try {
				willElement = manager.find(VaultWillDetail.class, userId);
                if(null != willElement) {
                	return willElement;
                }
			} catch (Exception ex) {
				System.out.println("WillDaoOperation:getWillDetailbyUserId:: Unable to retrieve the Will Record: Exception: "+ ex.getMessage());
				throw(ex);
			}
		}
		return willElement;
	}
}