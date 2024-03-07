/**
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * @author sameermalik
 *
 */
public class Main {
	private static String authenticateExistingUser(String email, String password){
		Instant timestamp = Instant.now(); // This captures the current moment in UTC.
		String message = String.format("EXISTING ACCOUNT %s %s %s", email, password, timestamp.toString());
		System.out.println("Message: " + message);
		String serverResponse = ServerCommunicator.communicateWithAccountServer(message);
		System.out.println("Server response: " + serverResponse);

		return serverResponse;
	}

	private static User createUserInstance(String accountType, double userID, String email, String password, String legalFirstName, String legalLastName,
										   String address, LocalDate dob){
		return switch (accountType) {
			case "DOCTOR" -> new Doctor(userID, email, password, legalFirstName, legalLastName, address, dob);
			case "PATIENT" -> new Patient(userID, email, password, legalFirstName, legalLastName, address, dob);
			case "DSA" -> new DataAnalyst(userID, email, password, legalFirstName, legalLastName, address, dob);
			case "DPO" ->
					new DataProtectionOfficer(userID, email, password, legalFirstName, legalLastName, address, dob);
			case "SA" -> new Superadmin(userID, email, password, legalFirstName, legalLastName, address, dob);
			default -> null;
		};
	}

	private static User login(Scanner scanner) {
		System.out.print("Email: ");
		String email = scanner.nextLine().trim();

		System.out.print("Password: ");
		String password = scanner.nextLine().trim();

		String serverResponse = authenticateExistingUser(email, password);
		if (serverResponse.equals("INVALID")){
			System.out.println("Login failed.");
			return null;
		}
		try{

			String [] data = serverResponse.split(",");
			String accountType = data[0];
			double userID = Double.parseDouble(data[1]);
			String legalFirstName = data[4];
			String legalLastName = data[5];
			String address = data[6];
			LocalDate dob = LocalDate.parse(data[7], DateTimeFormatter.ISO_LOCAL_DATE);

            return createUserInstance(accountType, userID, email, password, legalFirstName, legalLastName, address, dob);

		} catch (Exception e){
			System.out.println("Error parsing the server response.");
			return null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		boolean running = true;

		while (running) {
			System.out.println("Welcome!");
			System.out.println("1. Create an account");
			System.out.println("2. Login");
			System.out.println("3. Quit");
			System.out.print("Select an option: ");

			int option = scanner.nextInt();

			switch(option) {
				case 1:
					// Here, call a method to handle account creation
					User newUser = AccountCreationService.createAccount(scanner, User.Account.NONE);
					if (newUser != null) {
						newUser.userInput();
					}
					break;
				case 2:
					// Handle login
					User existingUser = login(scanner);
					if (existingUser != null) {
						existingUser.userInput();
					}
					break;
				case 3:
					// Quit
					System.out.println("Quitting program.");
					running = false; // Exit the while loop
					break;
				default:
					System.out.println("Invalid option selected. Please try again.");
					break;
			}
		}

		scanner.close();
	}


}
