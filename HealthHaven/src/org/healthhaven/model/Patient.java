package org.healthhaven.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
public class Patient extends User {
	private static Account ACCOUNT_TYPE = Account.PATIENT;

	/**
	 * For new doctor
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
//	public Patient(String email, String legal_first_name, String legal_last_name, String address,
//			LocalDate dob) {
//		super(email, legal_first_name, legal_last_name, address, dob);
//	}

	/**
	 * For existing user
	 * @param userID
	 * @param email
	 * @param password
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public Patient(String userID, String email, String legal_first_name, String legal_last_name,
				   String address, LocalDate dob){
        super(userID, email, legal_first_name, legal_last_name, address, dob);

    }
	
	@Override
	public Account getAccountType() {
		return ACCOUNT_TYPE;
	}

	/**
	 * Display the patient record, return the server response.
	 * @param userID
	 */
	public String viewPatientRecord() {
		// Create a new JSONObject
	    JSONObject json = new JSONObject();
	    
	    // Populate the JSON object with key-value pairs
	    json.put("action", "VIEW_RECORD");
	    json.put("patientID", getUserID());
	   
	    
	    // Send the JSON string to the server
	    return ServerCommunicator.communicateWithServer(json.toString());

	}
	
	
	

}
