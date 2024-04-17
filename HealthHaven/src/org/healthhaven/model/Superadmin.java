package org.healthhaven.model;

import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

import org.healthhaven.model.User.Account;
import org.json.JSONObject;
import org.healthhaven.server.ServerCommunicator;

/**
 * @author sameermalik
 *
 */
public class Superadmin extends User {
	private static Account ACCOUNT_TYPE = Account.SUPERADMIN;

	/**
	 * New superadmin
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
//	public Superadmin(String email, String legal_first_name, String legal_last_name, String address,
//			LocalDate dob) {
//		super(email, legal_first_name, legal_last_name, address, dob);
//	}

	/**
	 * Existing superadmin
	 * @param userID
	 * @param email
	 * @param password
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public Superadmin(String userID, String email, String legal_first_name, String legal_last_name, String address,
					  LocalDate dob, String cookie) {
		super(userID, email, legal_first_name, legal_last_name, address, dob, cookie);
	}
	
	
	@Override
	public Account getAccountType() {
		return ACCOUNT_TYPE;
	}


	/**
	 * Print out the account list
	 */
	public String viewAccountList(){
		String message = "VIEW ACCOUNT";
		System.out.println("Message: " + message);
		return(ServerCommunicator.communicateWithServer(message));
		
	}
	
	public String authorizeAccountCreation(String email, Account accountType, LocalDate dob) {
		//Construct API call
		JSONObject json = new JSONObject();
	    json.put("request", "ALLOW_ACCOUNT_CREATION");
	    json.put("email", email);
	    json.put("userType", accountType.getAccountName());
	    json.put("dob", dob.toString());
	    json.put("callerId", getUserID());
	    json.put("cookie", getCookie());
	    
	    return ServerCommunicator.communicateWithServer(json.toString());
	}
	
	public String searchByUserId(String userId) {
		JSONObject json = new JSONObject();
	    json.put("request", "SEARCH_ACCOUNT");
	    json.put("userId", userId);
	    json.put("callerId", getUserID());
	    json.put("cookie", getCookie());
	   
	    return ServerCommunicator.communicateWithServer(json.toString());
	}
	
	public String deactivateAccount(String userId) {
		JSONObject json = new JSONObject();
	    json.put("request", "DEACTIVATE_ACCOUNT");
	    json.put("type", "DEACTIVATE_ACCOUNT");
	    json.put("userId", userId);
	    json.put("callerId", getUserID());
	    json.put("cookie", getCookie());
	   
	    return ServerCommunicator.communicateWithServer(json.toString());
	}
	
}
