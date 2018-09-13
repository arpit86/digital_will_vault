package com.csus.vault.web.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.csus.vault.web.model.UserKey;

public class UserDaoImpl implements UserDao {

	@Autowired
	DataSource datasource;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void register(UserKey user) {
		/*String sql = "insert into users values(?,?,?,?)";
	    jdbcTemplate.update(sql, new Object[] {user.getEmail(), user.getPrivateKey(), user.getPublicKey(), new Date()});*/
		System.out.println("inside database register");
	}

}
