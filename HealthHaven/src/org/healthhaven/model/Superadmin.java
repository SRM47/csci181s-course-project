package org.healthhaven.model;

import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

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
	public Superadmin(String email, String password, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(email, password, legal_first_name, legal_last_name, address, dob);
	}

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
	public Superadmin(long userID, String email, String password, String legal_first_name, String legal_last_name, String address,
					  LocalDate dob) {
		super(userID, email, password, legal_first_name, legal_last_name, address, dob);
	}
	
	@Override
	protected void generateUserID() {
		Random rnd = new Random();
		long randomNumber = 5_000_000_000L + (long)(rnd.nextDouble() * 999_999_999L);
		this.setUserID(randomNumber);
	}

//	/**
//	 * Choose account type to create (include user prompt)
//	 * @param scanner
//	 * @return
//	 */
//	protected static User.Account selectAccountType(Scanner scanner) {
//		int accountType = 0;
//		while (accountType < 1 || accountType > 5) {
//			System.out.println("Select an account type 1. Patient 2. Doctor 3. Data Science Analyst 4. Data Protection Officer 5. Super Admin: ");
//			try {
//				accountType = Integer.parseInt(scanner.nextLine());
//				if (accountType < 1 || accountType > 5) {
//					System.out.println("Invalid account type. Please select a number between 1 and 5.");
//				}
//			} catch (NumberFormatException e) {
//				System.out.println("Invalid input. Please enter a number.");
//			}
//		}
//		return switch (accountType) {
//			case 1 -> User.Account.DOCTOR;
//			case 2 -> User.Account.PATIENT;
//			case 3 -> User.Account.DATA_ANALYST;
//			case 4 -> User.Account.DPO;
//			case 5 -> User.Account.SUPERADMIN;
//			default -> User.Account.NONE;
//		};
//	}

	/**
	 * Print out the account list
	 */
	public void viewAccountList(){
		String message = "VIEW ACCOUNT";
		System.out.println("Message: " + message);
		String serverResponse = ServerCommunicator.communicateWithAccountServer(message);
		System.out.println("Server response: " + serverResponse);
	}

	public static void main(String[] args) {
		Superadmin newSuperadmin = new Superadmin("Sae@pomona.edu", "password", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
	}


}
