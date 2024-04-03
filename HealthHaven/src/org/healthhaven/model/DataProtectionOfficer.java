package org.healthhaven.model;


import java.time.LocalDate;
import java.util.Random;

import org.healthhaven.model.User.Account;


/**
 * 
 */

/**
 * @author sameermalik and maxbaum
 *
 */
public class DataProtectionOfficer extends User {
	private static Account ACCOUNT_TYPE = Account.DPO;

	/**
	 * New DPO
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
//	public DataProtectionOfficer(String email, String legal_first_name, String legal_last_name,
//			String address, LocalDate dob) {
//		super(email, legal_first_name, legal_last_name, address, dob);
//
//	}

	/**
	 * Existing DPO
	 * @param userID
	 * @param email
	 * @param password
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public DataProtectionOfficer(String userID, String email, String legal_first_name, String legal_last_name,
								 String address, LocalDate dob) {
		super(userID, email, legal_first_name, legal_last_name, address, dob);
	}

	
	@Override
	public Account getAccountType() {
		return ACCOUNT_TYPE;
	}

}
