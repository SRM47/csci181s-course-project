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
		String serverResponse = ServerCommunicator.communicateWithAccountServer(message);
		System.out.println("Server response: " + serverResponse);

		return serverResponse;
	}

	/**
	 * Check if the email already exists or not.
	 * @param email
	 * @return
	 */
	private static boolean authenticateNewUser(String email){
		Instant timestamp = Instant.now(); // This captures the current moment in UTC.
		String message = String.format("NEW ACCOUNT %s %s", email, timestamp.toString());
		String serverResponse = ServerCommunicator.communicateWithAccountServer(message);
		System.out.println("Server response: " + serverResponse);

		return (serverResponse.equals("VALID"));
	}

//	private static User communicateWithServer(String message, int serverPort){
//		StringBuilder response = new StringBuilder();
//
//		try (Socket socket = new Socket("localhost", serverPort);
//			 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//			 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//
//			// Send the message to the server
//			writer.write(message);
//			writer.newLine();
//			writer.flush();
//
//			// Read the response
//			String line;
//			while ((line = reader.readLine()) != null) {
//				response.append(line);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Error communicating with server.");
//		}
//
//		System.out.println( response.toString();
//	}
	private static User login(Scanner scanner) {
		System.out.print("Email: ");
		String email = scanner.nextLine();

		System.out.print("Password: ");
		String password = scanner.nextLine();

		String serverResponse = authenticateExistingUser(email, password);
		if (serverResponse.equals("INVALID")){
			System.out.println("Login failed.");
			return null;
		}
		else{
			String [] data = serverResponse.split(",");
			String accountType = data[0];
			double userID = Double.parseDouble(data[1]);
			String legalFirstName = data[4];
			String legalLastName = data[5];
			String address = data[6];
			LocalDate dob = LocalDate.parse(data[7], DateTimeFormatter.ISO_LOCAL_DATE);


            return switch (accountType) {
                case "DOCTOR" -> new Doctor(userID, password, email, legalFirstName, legalLastName, address, dob);
                case "PATIENT" -> new Patient(userID, password, email, legalFirstName, legalLastName, address, dob);
                case "DSA" -> new DataAnalyst(userID, password, email, legalFirstName, legalLastName, address, dob);
                case "DPO" ->
                        new DataProtectionOfficer(userID, password, email, legalFirstName, legalLastName, address, dob);
                case "SA" -> new Superadmin(userID, password, email, legalFirstName, legalLastName, address, dob);
                default -> null;
            };

		}

	}

	private static User createUserInstance(int accountType, String email, String legal_first_name, String legal_last_name, String address,
						   LocalDate dob) {
		String account;
        return switch (accountType) {
            case 1 -> new Patient(email, legal_first_name, legal_last_name, address, dob);
            case 2 -> new Doctor(email, legal_first_name, legal_last_name, address, dob);
            case 3 -> new DataAnalyst(email, legal_first_name, legal_last_name, address, dob);
            case 4 -> new DataProtectionOfficer(email, legal_first_name, legal_last_name, address, dob);
            case 5 -> new Superadmin(email, legal_first_name, legal_last_name, address, dob);
            default -> null;
        };
	}
	private static User createAccount(Scanner scanner){
		int accountType = 0;
		while (accountType < 1 || accountType > 5) {
			System.out.println("Select an account type 1. Patient 2. Doctor 3. Data Science Analyst 4. Data Protection Officer 5. Super Admin: ");
			try {
				accountType = Integer.parseInt(scanner.nextLine());
				if (accountType < 1 || accountType > 5) {
					System.out.println("Invalid account type. Please select a number between 1 and 5.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a number.");
			}
		}
		System.out.print("Enter the email: ");
		String email = scanner.nextLine();
		System.out.print("Enter the first name: ");
		String firstName = scanner.nextLine();
		System.out.print("Enter the last name: ");
		String lastName = scanner.nextLine();
		System.out.print("Enter the address: ");
		String address = scanner.nextLine();
		LocalDate dateOfBirth = null;
		while (dateOfBirth == null) {
			System.out.print("Enter the date of birth (yyyy-mm-dd): ");
			String dobInput = scanner.nextLine();

			try {
				dateOfBirth = LocalDate.parse(dobInput, DateTimeFormatter.ISO_LOCAL_DATE);
			} catch (DateTimeParseException e) {
				System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
			}
		}

		//next, communicate with server to see if the email is new.
		if (authenticateNewUser(email)){
			return createUserInstance(accountType, email, firstName, lastName, address, dateOfBirth);
		} else{
			System.out.println("Email address already exists.");
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
			scanner.nextLine(); // Consume the newline

			switch(option) {
				case 1:
					// Here, call a method to handle account creation
					User newUser = createAccount(scanner);
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
