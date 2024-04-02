package org.healthhaven.model;

import org.json.JSONObject;
import org.json.JSONArray;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Scanner;

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
	    json.put("action", "LOGIN");
	    json.put("email", email);
	    json.put("password", password);
	    json.put("timestamp", timestamp.toString());
	    
		// System.out.println("Message: " + message);
		return(ServerCommunicator.communicateWithAccountServer(json.toString()));
		// System.out.println("Server response: " + serverResponse);

	}
	
//	private static String authenticateNewUser(String email, String password) {
//		Instant timestamp = Instant.now(); // This captures the current moment in UTC.
//		String message = String.format("AUTHENTICATE_NEW %s %s %s", email, password, timestamp.toString());
//		// System.out.println("Message: " + message);
//		return( ServerCommunicator.communicateWithAccountServer(message));
//		// System.out.println("Server response: " + serverResponse);
//
//	}

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
	private static User createUserInstance(String accountType, long userID, String email, String legalFirstName, String legalLastName,
										   String address, LocalDate dob){
		return switch (accountType) {
			case "Doctor" -> new Doctor(userID, email, legalFirstName, legalLastName, address, dob);
			case "Patient" -> new Patient(userID, email, legalFirstName, legalLastName, address, dob);
			case "Data_Analyst" -> new DataAnalyst(userID, email, legalFirstName, legalLastName, address, dob);
			case "Data_Protection_Officer" ->
					new DataProtectionOfficer(userID, email, legalFirstName, legalLastName, address, dob);
			case "Superadmin" -> new Superadmin(userID, email, legalFirstName, legalLastName, address, dob);
			default -> null;
		};
	}
	
//	public String identifyNewUser(String email, String password) {
//		return(authenticateNewUser(email, password));
//	}
	
	public static User existingUserSession(JSONObject jsonOb) {
			
			String email = jsonOb.getString("email");
			long userID = Long.parseLong(jsonOb.getString("userID"));
			String legalFirstName = jsonOb.getString("first_name");
			String legalLastName = jsonOb.getString("last_name");
			String address = jsonOb.getString("address");
			LocalDate dob = LocalDate.parse(jsonOb.getString("dob"), DateTimeFormatter.ISO_LOCAL_DATE);
			String accountType = jsonOb.getString("accountType");

            return createUserInstance(accountType, userID, email, legalFirstName, legalLastName, address, dob);

	}


}
