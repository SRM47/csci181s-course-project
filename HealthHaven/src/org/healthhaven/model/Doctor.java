package org.healthhaven.model;

import org.json.JSONObject;
import org.json.JSONArray;

import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

import org.healthhaven.server.ServerCommunicator;
import java.time.Instant;


/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class Doctor extends User {
	private static Account ACCOUNT_TYPE = Account.DOCTOR;

	/**
     * New doctor
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
    
//	public Doctor(String email, String legal_first_name, String legal_last_name, String address,
//			LocalDate dob) {
//		super(email, legal_first_name, legal_last_name, address, dob);
//	}

    /**
     * Existing doctor
     * @param userID
     * @param email
     * @param password
     * @param legal_first_name
     * @param legal_last_name
     * @param address
     * @param dob
     */
    public Doctor(String userID, String email, String legal_first_name, String legal_last_name,
                   String address, LocalDate dob){
        super(userID, email, legal_first_name, legal_last_name, address, dob);

    }
	
	@Override
	public Account getAccountType() {
		return ACCOUNT_TYPE;
	}


    /**
     * Update the patient record on DB, communicate with the server, return the server response.
     * @param userID
     * @param height
     * @param weight
     * @return
     */
	public String updatePatientRecordOnDB(String userID, float height, float weight) {
        Instant timestamp = Instant.now(); // This captures the current moment in UTC.
        
        // Create a JSONObject and populate it with data
        JSONObject json = new JSONObject();
        json.put("request", "CREATE_RECORD");
        json.put("patientID", userID);
        json.put("doctorID", getUserID());
        json.put("height", height);
        json.put("weight", weight);
        json.put("timestamp", timestamp.toString());

        /// Send the JSON string to the server
        return ServerCommunicator.communicateWithServer(json.toString());
    }

    /**
     * View patient record, return the server response
     * @param userID
     * @return
     */
	public String viewPatientRecord(String patientID) {
		// Create a new JSONObject
	    JSONObject json = new JSONObject();
	    
	    // Populate the JSON object with key-value pairs
	    json.put("request", "VIEW_RECORD");
	    json.put("patientID", patientID);
	    json.put("doctorID", getUserID());
	   
	    
	    // Send the JSON string to the server
	    return ServerCommunicator.communicateWithServer(json.toString());
	
	}
	
	public String authorizeAccountCreation(String email, LocalDate dob) {
		//Construct API call
		JSONObject json = new JSONObject();
	    json.put("request", "ALLOW_ACCOUNT_CREATION");
	    json.put("email", email);
	    json.put("userType", User.Account.PATIENT.getAccountName());
	    json.put("dob", dob.toString());
	    
	    return ServerCommunicator.communicateWithServer(json.toString());
	}


}
