package com.csus.vault.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.csus.vault.web.model.VaultUser;

public class UserDaoOperation {
	
	@Autowired
	private EntityManagerFactory emf;
	
	/*
	 *  This function checks whether a user exists in the database.
	 *  @return isValid = true: if user not present
	 *  				  false: if user exists
	 */
	public boolean verify(VaultUser user) {
		boolean isValid = false;
		
		if (null != emf && null != user) {
			System.out.println("UserDaoOperation:verify:: inside verify()");
            EntityManager em = null;
            
            try {
                em = emf.createEntityManager();
                em.getTransaction().begin();
                VaultUser userElement = em.find(VaultUser.class, user.getUserEmail());
                
                if(null != userElement && userElement.getUserEmail().equalsIgnoreCase(user.getUserEmail())){
                	System.out.println("UserDaoOperation:verify:: user already present in database.");
                    isValid = false;
                } else {
                	System.out.println("UserDaoOperation:verify:: user does not exist in database.");
                	isValid = true;
                }
                em.getTransaction().commit();
            } catch (Exception e) {
            	em.getTransaction().rollback();
            	System.out.println("UserDaoOperation:verify:: Exeption: " + e.getMessage());
            } finally {
            	// Close EntityManager
            	if(null != em){
            		em.close();
            	}
            }
		}
	    return isValid;
	}
	
	/*
	 * This function saves a new user to the database.
	 */
	public void register(VaultUser user) {
		if(emf != null && user != null) {
			System.out.println("UserDaoOperation:register:: inside register()");
			EntityManager em = null;
						
			try {
				em = emf.createEntityManager();
				em.getTransaction().begin();
				em.persist(user);
				System.out.println("UserDaoOperation:register:: saved user: " + user.getUserEmail());
				em.getTransaction().commit();
			} catch (Exception e) {
            	em.getTransaction().rollback();
            	System.out.println("UserDaoOperation:register:: Unable to register the User Record: Exception: "+e.getMessage());
            } finally {
            	// Close EntityManager
            	if(null != em){
            		em.close();
            	}
			}
		}
	}

	/*
	 *  This function retrieves the user detail for the given email id from database.
	 */
	public VaultUser getUserDetailByEmail(String userEmail) {
		VaultUser user = null;
		
		if (null != emf && !userEmail.isEmpty()) {
			System.out.println("UserDaoOperation:getUserDetailByEmail:: inside verify()");
            EntityManager em = null;
            
            try {
                em = emf.createEntityManager();
                em.getTransaction().begin();
                user = em.find(VaultUser.class, userEmail);
                em.getTransaction().commit();
            } catch (Exception e) {
            	em.getTransaction().rollback();
            	System.out.println("UserDaoOperation:getUserDetailByEmail:: Exeption: " + e.getMessage());
            } finally {
            	// Close EntityManager
            	if(null != em){
            		em.close();
            	}
            }
		}
		return user;
	}

}
