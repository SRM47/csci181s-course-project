package org.healthhaven.model;

import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;


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
	public DataAnalyst(String email, String password, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(email, password, legal_first_name, legal_last_name, address, dob);

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
	public DataAnalyst(long userID, String email, String password, String legal_first_name, String legal_last_name, String address,
					   LocalDate dob) {
		super(userID, email, password, legal_first_name, legal_last_name, address, dob);
	}

	/**
	 * Get and print the data summary
	 * @param scanner
	 */
	protected void performDataAnalysis(Scanner scanner){
//		long userID = this.getUserID();
		long userID = 300;
		String message = "REQUEST_PATIENT_DATA_SUMMARY " + userID;
//		System.out.println("message");
		String ServerResponse = ServerCommunicator.communicateWithMedicalServer(message);
		System.out.println(ServerResponse);
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

	@Override
	protected void userInput(Scanner scanner){
		// Scanner scanner = new Scanner(System.in);

		while (true){
			System.out.println("\nPlease choose an option: ");
			System.out.println("1. Access my info");
			System.out.println("2. Get data");
			System.out.println("3. Exit");
			System.out.println("Enter your choice: ");

			int choice = scanner.nextInt();
			scanner.nextLine();
			switch (choice){
				case 1:
					accessPersonalRecord(scanner);
					break;
				case 2:
					System.out.println("Accessing the medical data...");
					performDataAnalysis(scanner);
					break;
				case 3:
					System.out.println("Existing...");
					// scanner.close();
					return;
				default:
					System.out.println("Invalid option. Please try again");

			}
		}


	}

	public static void main(String [] args){
		DataAnalyst newDSA = new DataAnalyst("Sae@pomona.edu", "password", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
		newDSA.userInput(new Scanner(System.in));
	}


}
