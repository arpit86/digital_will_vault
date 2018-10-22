package com.csus.vault.web.model;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-10-20T22:40:06.254-0700")
@StaticMetamodel(VaultUser.class)
public class VaultUser_ {
	public static volatile SingularAttribute<VaultUser, Integer> userId;
	public static volatile SingularAttribute<VaultUser, String> passwordSalt;
	public static volatile SingularAttribute<VaultUser, Date> user_createdTS;
	public static volatile SingularAttribute<VaultUser, String> userEmail;
	public static volatile SingularAttribute<VaultUser, String> user_firstName;
	public static volatile SingularAttribute<VaultUser, String> user_lastName;
	public static volatile SingularAttribute<VaultUser, String> userPassword;
	public static volatile SingularAttribute<VaultUser, BigInteger> userPhone;
	public static volatile SingularAttribute<VaultUser, byte[]> user_publicKey;
	public static volatile SingularAttribute<VaultUser, Date> user_updatedTS;
}
