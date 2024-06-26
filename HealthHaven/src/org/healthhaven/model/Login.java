package org.healthhaven.model;

import org.json.JSONObject;
import org.json.JSONArray;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.healthhaven.server.ServerCommunicator;

/**
 * @author sameermalik
 *
 */
public class Login {
	/**
	 * Check if the existing user's userID and password are in DB
	 * @param email
	 * @param password
	 * @return
	 */
	public static String authenticateUserOnDB(String email, String password){
		Instant timestamp = Instant.now(); // This captures the current moment in UTC.
		// Creating a new JSONObject and populating it with data
	    JSONObject json = new JSONObject();
	    json.put("request", "LOGIN");
	    json.put("type", "PASSWORD");
	    json.put("email", email);
	    json.put("password", password);
	    json.put("timestamp", timestamp.toString());
	    
		// System.out.println("Message: " + message);
		return ServerCommunicator.communicateWithServer(json.toString());
	}
	

	/**
	 * Create a new user instance.
	 * @param accountType
	 * @param userID
	 * @param email
	 * @param password
	 * @param legalFirstName
	 * @param legalLastName
	 * @param address
	 * @param dob
	 * @return
	 */
	private static User createUserInstance(String accountType, String userID, String email, String legalFirstName, String legalLastName,
										   String address, LocalDate dob, String cookie, boolean dataSharing){
		
		System.out.println(accountType);
		
		return switch (accountType) {
			case "Doctor", "DOCTOR" -> new Doctor(userID, email, legalFirstName, legalLastName, address, dob, cookie);
			case "Patient", "PATIENT" -> new Patient(userID, email, legalFirstName, legalLastName, address, dob, cookie, dataSharing);
			case "Data Analyst", "DATA_ANALYST" -> new DataAnalyst(userID, email, legalFirstName, legalLastName, address, dob, cookie);
			case "Superadmin", "SUPERADMIN" -> new Superadmin(userID, email, legalFirstName, legalLastName, address, dob, cookie);
			default -> null;
		};
	}
	
//	public String identifyNewUser(String email, String password) {
//		return(authenticateNewUser(email, password));
//	}
	
	public static User existingUserSession(JSONObject jsonOb) {
			
			String email = jsonOb.getString("email");
			String userID = jsonOb.getString("userID");
			String legalFirstName = jsonOb.getString("first_name");
			String legalLastName = jsonOb.getString("last_name");
			String address = jsonOb.getString("address");
			LocalDate dob = LocalDate.parse(jsonOb.getString("dob"), DateTimeFormatter.ISO_LOCAL_DATE);
			String accountType = jsonOb.getString("accountType");
			String cookie = jsonOb.getString("cookie");
			boolean dataSharing = jsonOb.getBoolean("data_sharing");
				//dataSharing = Boolean.parseBoolean(jsonOb.getString("data_sharing"));

            return createUserInstance(accountType, userID, email, legalFirstName, legalLastName, address, dob, cookie, dataSharing);

	}


	public static String authenticateOTPLogin(String emailAddress, String otpInput) {
		
		JSONObject json = new JSONObject();
		json.put("request", "LOGIN");
	    json.put("type", "OTP");
	    json.put("email", emailAddress);
	    json.put("otp", otpInput);
	    
		// System.out.println("Message: " + message);
		return ServerCommunicator.communicateWithServer(json.toString());
	}



}
