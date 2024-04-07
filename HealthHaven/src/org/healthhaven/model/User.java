package org.healthhaven.model;


import java.time.LocalDate;
import java.util.Scanner;

import org.json.JSONObject;
import org.healthhaven.server.ServerCommunicator;


public class User {

	// The format of the userID will correspond to the account type
	private String userID;
	private String email;
	private String legal_first_name;
	private String legal_last_name;
	private String address;
	private LocalDate dob;
	
	
	public static enum Account{
		DOCTOR("Doctor"), 
		PATIENT("Patient"),
		DATA_ANALYST("Data Analyst"),
		SUPERADMIN("Superadmin"),
		DPO("Data Protection Officer"),
		NONE("NONE");
		private String account_name;
		private Account(String account_name) {
			this.account_name = account_name;
		}
		
		public String getAccountName() {
			return this.account_name;
		}
	}
	
	private static Account ACCOUNT_TYPE = Account.NONE;

	/**
	 * New user
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
//	public User(String email, String legal_first_name, String legal_last_name, String address,
//			LocalDate dob) {
//		super();
//		this.email = email;
//		this.legal_first_name = legal_first_name;
//		this.legal_last_name = legal_last_name;
//		this.address = address;
//		this.dob = dob;
//		this.generateUserID();
//	}

	/**
	 * For existing user
	 * @param userID
	 * @param password
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public User(String userID, String email, String legal_first_name, String legal_last_name, String address, LocalDate dob) {
		this.userID = userID;
		this.email = email;
		this.legal_first_name = legal_first_name;
		this.legal_last_name = legal_last_name;
		this.address = address;
		this.dob = dob;
		
	}
	
	/**
	 * @return the account type
	 */
	public Account getAccountType() {
		return ACCOUNT_TYPE;
	}

//	/**
//	 * @return the password
//	 */
//	public String getPassword() {
//		return password;
//	}

//	/**
//	 * @param password the password to set
//	 */
//	public void setPassword(String password) {
//		this.password = password;
//	}

//	protected void generateUserID() {
//		UserIdGenerator g = new UserIdGenerator(16);
//		this.userID = g.generate();
//	}

	
	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	protected void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the legal_first_name
	 */
	public String getLegal_first_name() {
		return legal_first_name;
	}

	/**
	 * @param legal_first_name the legal_first_name to set
	 */
	public void setLegal_first_name(String legal_first_name) {
		this.legal_first_name = legal_first_name;
	}

	/**
	 * @return the legal_last_name
	 */
	public String getLegal_last_name() {
		return legal_last_name;
	}

	/**
	 * @param legal_last_name the legal_last_name to set
	 */
	public void setLegal_last_name(String legal_last_name) {
		this.legal_last_name = legal_last_name;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the dob
	 */
	public LocalDate getDob() {
		return dob;
	}

	/**
	 * @param dob the dob to set
	 */
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	/**
	 * Update the personal record on the database, return the server response.
	 * @param newEmail
	 * @param newPassword
	 * @param newAddress
	 * @return
	 */
	public String updatePersonalRecordOnDB(String newAddress){

		// First set the changes to the object instance.
		setAddress(newAddress);
		JSONObject json = new JSONObject();
	    
	    // Populate the JSON object with key-value pairs
	    json.put("request", "UPDATE_ACCOUNT");
	    json.put("address", newAddress);
		return ServerCommunicator.communicateWithServer(json.toString());
	}

	@Override
	public String toString() {
		return getAccountType().getAccountName() +  ", userID=" + userID + ", email=" + email
				+ ", legal_first_name=" + legal_first_name + ", legal_last_name=" + legal_last_name + ", address="
				+ address + ", dob=" + dob + "]";
	}
	
}
