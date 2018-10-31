package com.csus.vault.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.csus.vault.web.model.VaultAuthorizedUser;
import com.csus.vault.web.model.VaultUser;

@Repository
@Transactional
public class UserDaoOperation {
	
	@PersistenceContext
    private EntityManager manager;
	
	/*
	 *  This function checks whether a user exists in the database.
	 *  @return isPresent = new: if user not present
	 *  				    authorizeUser: if user exists but does not have a password
	 *  					exist: if user is present in database
	 */
	public String verify(VaultUser user) {
		String isPresent = "";
		if (null != manager && null != user) {
			System.out.println("UserDaoOperation:verify:: inside verify()");
            try {
                VaultUser userElement = manager.find(VaultUser.class, user.getUserEmail());
                if(null != userElement && userElement.getUserEmail().equalsIgnoreCase(user.getUserEmail())){
                	System.out.println("UserDaoOperation:verify:: user already present in database.");
                	isPresent = "exist";
                	if(userElement.getUserPassword().isEmpty()) {
                		System.out.println("UserDaoOperation:verify:: user already present in database but is an authorized user");
                		isPresent="authorizeUser";
                	}
                } else {
                	System.out.println("UserDaoOperation:verify:: user does not exist in database.");
                	isPresent = "new";
                }
            } catch (Exception ex) {
            	System.out.println("UserDaoOperation:verify:: Exeption: " + ex.getMessage());
            }
		}
	    return isPresent;
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
            }
		}
		return user;
	}

	/*
	 *  This function updates the user attributes if modified.
	 */
	public void update(VaultUser user) {
		if(manager != null && user != null) {
			System.out.println("UserDaoOperation:register:: inside register()");
			try {
				VaultUser userElement = manager.find(VaultUser.class, user.getUserEmail());
				if(userElement != null && userElement.getUserEmail().equalsIgnoreCase(user.getUserEmail()))
					userElement = manager.merge(user);
				System.out.println("UserDaoOperation:update:: updated user: " + user.getUserEmail());
			} catch (Exception ex) {
            	System.out.println("UserDaoOperation:update:: Unable to update the User Record: Exception: "+ ex.getMessage());
            }
		}
	}

	public void saveAuthorizedUser(VaultAuthorizedUser authUser) {
		if(manager != null && authUser != null) {
			System.out.println("UserDaoOperation:register:: inside register()");
			try {
				manager.persist(authUser);
				System.out.println("UserDaoOperation:saveAuthorizedUser:: saved authorized user: " + authUser.getVault_userId());
			} catch (Exception ex) {
            	System.out.println("UserDaoOperation:saveAuthorizedUser:: Unable to save the User Record: Exception: "+ ex.getMessage());
            }
		}
		
	}

}
