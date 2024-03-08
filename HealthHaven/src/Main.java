/**
 * 
 */
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * @author sameermalik
 *
 */
public class Main {
	/**
	 * Check if the existing user's userID and password are in DB
	 * @param email
	 * @param password
	 * @return
	 */
	private static String authenticateExistingUser(String email, String password){
		Instant timestamp = Instant.now(); // This captures the current moment in UTC.
		String message = String.format("AUTHENTICATE_ACCOUNT %s %s %s", email, password, timestamp.toString());
		System.out.println("Message: " + message);
		String serverResponse = ServerCommunicator.communicateWithAccountServer(message);
		System.out.println("Server response: " + serverResponse);

		return serverResponse;
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
	private static User createUserInstance(String accountType, long userID, String email, String password, String legalFirstName, String legalLastName,
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

	/**
	 * Select an account type, return the type. (Include the user prompt)
	 * @param scanner
	 * @return
	 */
	private static User.Account selectAccountType(Scanner scanner) {
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
        return switch (accountType) {
            case 1 -> User.Account.DOCTOR;
            case 2 -> User.Account.PATIENT;
            case 3 -> User.Account.DATA_ANALYST;
            case 4 -> User.Account.DPO;
            case 5 -> User.Account.SUPERADMIN;
            default -> User.Account.NONE;
        };
	}

	/**
	 * User log in, if valid, then it uses the server response to parse the data and create an existing user instance (include user prompt)
	 * @param scanner
	 * @return
	 */
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
			long userID = Long.parseLong(data[1]);
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
		int option = 0;

		boolean running = true;

		while (running) {
			System.out.println("Welcome!");
			System.out.println("1. Create an account");
			System.out.println("2. Login");
			System.out.println("3. Quit");
			System.out.print("Select an option: ");

			try{
				String input = scanner.nextLine().trim(); // Read the input as String and trim whitespace
				option = Integer.parseInt(input);
				switch(option) {
					case 1:
						// Here, call a method to handle account creation
						User.Account userType = selectAccountType(scanner);

						User newUser = AccountCreationService.createAccount(scanner, userType);
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
			} catch (NumberFormatException e){
				System.out.println("Please enter a valid number.");
			}
		}

		scanner.close();
	}


}
