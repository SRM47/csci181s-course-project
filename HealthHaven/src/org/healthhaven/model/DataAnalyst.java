package org.healthhaven.model;

import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.json.JSONObject;


/**
 * 
 */

/**
 * @author sameermalik and maxbaum
 *
 */
public class DataAnalyst extends User {
	private static Account ACCOUNT_TYPE = Account.DATA_ANALYST;

	/**
	 * New Data Analyst
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
//	public DataAnalyst(String email, String legal_first_name, String legal_last_name, String address,
//			LocalDate dob) {
//		super(email, legal_first_name, legal_last_name, address, dob);
//
//	}

	/**
	 * Existing Data Analyst
	 * @param userID
	 * @param email
	 * @param password
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public DataAnalyst(String userID, String email,  String legal_first_name, String legal_last_name, String address,
					   LocalDate dob, String cookie) {
		super(userID, email, legal_first_name, legal_last_name, address, dob, cookie);
	}
	
	@Override
	public Account getAccountType() {
		return ACCOUNT_TYPE;
	}

	/**
	 * Get and print the data summary
	 * @param scanner
	 */
	public String performDataAnalysis(){
		JSONObject json = new JSONObject();
		json.put("request", "REQUEST_PATIENT_DATA_SUMMARY");
		json.put("callerId", getUserID());
	    json.put("cookie", getCookie());
		return ServerCommunicator.communicateWithServer(json.toString());
	}


}
