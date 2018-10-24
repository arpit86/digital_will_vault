package com.csus.vault.web.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;
import com.csus.vault.web.service.BlockManagerService;

public class WillDaoOperation {
	
	@Autowired
    EntityManagerFactory emf;
	
	private BlockManagerService blockService = null;
	

	/*
	 *  This function will save the encrypted will to the database and
	 *  add user to vault_authorized_user table with update and view rights for the will.
	 */
	public void saveEncryptedWillToDB(byte[] encryptedData, VaultUser user) {
		if(emf != null && encryptedData != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			EntityManager em1 = null;
			EntityManager em2 = null;
			
			try {
				VaultWillDetail will = new VaultWillDetail();
				will.setVault_userId(user.getUserId());
				will.setWill_createdTS(new Date());
				will.setWill_updatedTS(new Date());
				will.setWillContent(encryptedData);
				VaultAuthorizedUser authUser = new VaultAuthorizedUser();
				em1 = emf.createEntityManager();
				em1.getTransaction().begin();
				em1.persist(will);
				System.out.println("WillDaoOperation:saveEncryptedWillToDB:: saved will: " + user.getUserEmail());
				em1.getTransaction().commit();
				VaultWillDetail willInfo = getWillDetailbyUserId(user.getUserId());
				if(null != willInfo) {
                	authUser.setAuthorizedTS(new Date());
                	authUser.setVault_userId(will.getVault_userId());
                	authUser.setWillId(will.getWillId());
                	authUser.setAuthorizedUpdate(true);
                	authUser.setAuthorizedView(true);
                	em2 = emf.createEntityManager();
                    em2.getTransaction().begin();
                    em2.persist(authUser);
                	em2.getTransaction().commit();
                	blockService.createBlockWithWillUploadTransaction(willInfo);
                }
			} catch (Exception e) {
            	em1.getTransaction().rollback();
            	em2.getTransaction().rollback();
            	System.out.println("WillDaoOperation:saveEncryptedWillToDB:: Unable to save the Will Record: Exception: "+e.getMessage());
            } finally {
            	// Close EntityManager
            	if(null != em1 && null != em2){
            		em1.close();
            		em2.close();
            	}
			}
		}
	}


	public VaultWillDetail getWillDetailbyUserId(int userId) {
		VaultWillDetail willElement = null;	
		if(emf != null) {
			System.out.println("WillDaoOperation:saveEncryptedWillToDB:: inside saveEncryptedWillToDB()");
			EntityManager em = null;
					
			try {
				em = emf.createEntityManager();
                em.getTransaction().begin();
                willElement = em.find(VaultWillDetail.class, userId);
                em.getTransaction().commit();
                if(null != willElement) {
                	return willElement;
                }
			} catch (Exception ex) {
				em.getTransaction().rollback();
            	System.out.println("WillDaoOperation:getWillDetailbyUserId:: Unable to retrieve the Will Record: Exception: "+ ex.getMessage());
			} finally {
				if(em != null)
					em.close();
			}
		}
		return willElement;
	}

	
}