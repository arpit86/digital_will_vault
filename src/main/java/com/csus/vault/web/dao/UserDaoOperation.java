package com.csus.vault.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.csus.vault.web.model.VaultUser;

@Repository
@Transactional
public class UserDaoOperation {
	
	@PersistenceContext
    private EntityManager manager;
	
	/*
	 *  This function checks whether a user exists in the database.
	 *  @return isValid = true: if user not present
	 *  				  false: if user exists
	 */
	public boolean verify(VaultUser user) {
		boolean isValid = false;
		if (null != manager && null != user) {
			System.out.println("UserDaoOperation:verify:: inside verify()");
            try {
                VaultUser userElement = manager.find(VaultUser.class, user.getUserEmail());
                if(null != userElement && userElement.getUserEmail().equalsIgnoreCase(user.getUserEmail())){
                	System.out.println("UserDaoOperation:verify:: user already present in database.");
                    isValid = false;
                } else {
                	System.out.println("UserDaoOperation:verify:: user does not exist in database.");
                	isValid = true;
                }
            } catch (Exception ex) {
            	System.out.println("UserDaoOperation:verify:: Exeption: " + ex.getMessage());
            	throw(ex);
            }
		}
	    return isValid;
	}
	
	/*
	 * This function saves a new user to the database.
	 */
	public void register(VaultUser user) {
		if(manager != null && user != null) {
			System.out.println("UserDaoOperation:register:: inside register()");
			try {
				manager.persist(user);
				System.out.println("UserDaoOperation:register:: saved user: " + user.getUserEmail());
			} catch (Exception ex) {
            	System.out.println("UserDaoOperation:register:: Unable to register the User Record: Exception: "+ ex.getMessage());
            	throw(ex);
            }
		}
	}

	/*
	 *  This function retrieves the user detail for the given email id from database.
	 */
	public VaultUser getUserDetailByEmail(String userEmail) {
		VaultUser user = null;
		if (null != manager && !userEmail.isEmpty()) {
			System.out.println("UserDaoOperation:getUserDetailByEmail:: inside verify()");
            try {
                user = manager.find(VaultUser.class, userEmail);
            } catch (Exception ex) {
            	System.out.println("UserDaoOperation:getUserDetailByEmail:: Exeption: " + ex.getMessage());
            	throw(ex);
            }
		}
		return user;
	}

}
