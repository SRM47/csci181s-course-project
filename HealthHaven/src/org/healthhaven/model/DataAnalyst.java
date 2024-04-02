package org.healthhaven.model;

import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;


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
	public DataAnalyst(String email, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(email, legal_first_name, legal_last_name, address, dob);

	}

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
	public DataAnalyst(long userID, String email,  String legal_first_name, String legal_last_name, String address,
					   LocalDate dob) {
		super(userID, email, legal_first_name, legal_last_name, address, dob);
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
		String message = "REQUEST_PATIENT_DATA_SUMMARY ";
//		System.out.println("message");
		return(ServerCommunicator.communicateWithServer(message));
	}
	
	@Override
	protected void generateUserID() {
		Random rnd = new Random();
		// Generate a random number where the first digit is 3 and the rest 11 digits are random
		// This is done by starting at 3_000_000_000_000L (the smallest 12-digit number starting with 3)
		// and adding a random number up to 8_999_999_999_999L to ensure the first digit remains 3
		// and we have a total of 12 digits.
		long randomNumber = 3_000_000_000L + (long)(rnd.nextDouble() * 999_999_999L);

		this.setUserID(randomNumber);
	}



}
