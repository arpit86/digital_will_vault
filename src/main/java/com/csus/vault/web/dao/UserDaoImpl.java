package com.csus.vault.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.csus.vault.web.model.UserKey;
import com.csus.vault.web.model.VaultUser;

public class UserDaoImpl implements UserDao {

	@Autowired
	DataSource datasource;

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private EntityManagerFactory emf;
	
	@PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

	@Override
	public void register(UserKey user) {
		/*String sql = "insert into users values(?,?,?,?)";
	    jdbcTemplate.update(sql, new Object[] {user.getEmail(), user.getPrivateKey(), user.getPublicKey(), new Date()});*/
		System.out.println("inside database register");
	}

	public <E extends VaultUser> void register(VaultUser user) {
		final EntityManager em = emf.createEntityManager();
		em.createQuery("insert into VaultUser");
	    
	}

	public boolean verify(VaultUser user) {
		boolean isValid = false;
		
		final EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("from VaultUser where userEmail = :userEmail");
		query.setParameter("userEmail", user.getUserEmail());
		isValid = query.getResultList().isEmpty();
	    em.close();
	    
	    return isValid;
	}
	
	public VaultUser verifyUser(String userEmail) {
				
		final EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("from VaultUser where userEmail = :userEmail");
		query.setParameter("userEmail", userEmail);
		VaultUser dbUser = (VaultUser) query.getResultList().get(0);
	    em.close();
	    
	    return dbUser;
	}

}
