package org.healthhaven.model;

import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

import org.healthhaven.model.User.Account;

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
    
	public Doctor(String email, String password, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(email, password, legal_first_name, legal_last_name, address, dob);
	}

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
    public Doctor(long userID, String email, String password, String legal_first_name, String legal_last_name,
                   String address, LocalDate dob){
        super(userID, email, password, legal_first_name, legal_last_name, address, dob);

    }

    /**
     * User ID with left most digit = 1
     */
	@Override
	protected void generateUserID() {
		Random rnd = new Random();
        long randomNumber = 1_000_000_000L + (long)(rnd.nextDouble() * 999_999_999L);
	    this.setUserID(randomNumber);
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
	public String updatePatientRecordOnDB(long userID, String height, String weight) {
        Instant timestamp = Instant.now(); // This captures the current moment in UTC.

        // Construct a message to send to the server
        String message = String.format("UPDATE_RECORD %d %s %s %s", userID, height, weight, timestamp.toString());
        System.out.println("message: "+ message);
        return ServerCommunicator.communicateWithMedicalServer(message);
    }

    /**
     * View patient record, return the server response
     * @param userID
     * @return
     */
	public String viewPatientRecord(long userID) {
        String message = String.format("VIEW %d", userID);
        System.out.println("Message: " + message);
        return ServerCommunicator.communicateWithMedicalServer(message);
    }
	
	public String authorizeAccountCreation(String email) {
		String serverResponse = "";
		serverResponse = doesAccountExist(email);
		if (!serverResponse.equals("VALID")) {
			return serverResponse + ": Cannot create account under this email.";
		}
		
		String password = PasswordGenerator.generate();
		String message = String.format("AUTHORIZE %s %s %s", email, password, "Patient");
		serverResponse = ServerCommunicator.communicateWithAccountServer(message);
		
		if (!serverResponse.equals("SUCCESS")) {
			return serverResponse;
		}
		return sendEmail(email, password);
	}
	
	private String sendEmail(String email, String password) {
		String subject = "Create Account";
		String body = String.format("This is to confirm that you are authorized to make an account as Patient. "
	            + "Your default password is %s. Please go to the login page and use this email and password.", password);
		return EmailSender.sendEmail(email, subject, body);
	}
	
	
	private static String doesAccountExist(String email){
        String message = String.format("EXISTING_ACCOUNT %s", email);
        // System.out.println("Message: " + message);
        return  ServerCommunicator.communicateWithAccountServer(message);
        // System.out.println("Server response: " + serverResponse);
    }


    public static void main(String[] args) {
        Doctor newDoctor = new Doctor("Sae@pomona.edu", "password", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
    }

}
