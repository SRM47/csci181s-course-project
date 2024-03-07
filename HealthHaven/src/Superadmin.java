import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;
import java.util.Scanner;

/**
 * @author sameermalik
 *
 */
public class Superadmin extends User {
	private static Account ACCOUNT_TYPE = Account.SUPERADMIN;

	/**
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

	public Superadmin(double userID, String email, String password, String legal_first_name, String legal_last_name, String address,
					  LocalDate dob) {
		super(userID, email, password, legal_first_name, legal_last_name, address, dob);
	}
	
	@Override
	protected void generateUserID() {
		Random rnd = new Random();
		long randomNumber = 3_000_000_000_00L + (long)(rnd.nextDouble() * 9_000_000_000_00L);
		this.setUserID(randomNumber);
	}
	protected static User.Account selectAccountType(Scanner scanner) {
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
	public void viewAccountList(){
		String message = "VIEW ACCOUNT";
		System.out.println("Message: " + message);
		String serverResponse = ServerCommunicator.communicateWithAccountServer(message);
		System.out.println("Server response: " + serverResponse);
	}
	@Override
	public void userInput(){
		Scanner scanner = new Scanner(System.in);

		while(true){
			System.out.println("\nPlease choose an option:");
			System.out.println("1. Access my info");
			System.out.println("2. Access account list");
			System.out.println("3. Create a new user");
			System.out.println("4. Exit");
			System.out.print("Enter your choice: ");

			int choice = scanner.nextInt();
			int subChoice;
			switch(choice){
				case 1:
					System.out.println(this);
					System.out.print("Do you want to update your record? 1(yes) 2(no)");
					subChoice = scanner.nextInt();
					switch (subChoice){
						case 1:
							updatePersonalRecord(scanner);
							break;
						case 2:
							System.out.println("Not updating any personal data.");
							break;
						default:
							System.out.println("Invalid option. Please try again");
					}
					break;
				case 2:
					System.out.println("Accessing the account list");
					viewAccountList();
					break;
				case 3:
					System.out.println("Creating a new user");
					// Here, call a method to handle account creation
					User.Account userType = selectAccountType(scanner);
					User newUser = AccountCreationService.createAccount(scanner, userType);
					break;
				case 4:
					// Exit the method
					System.out.println("Exiting...");
					scanner.close();
					return;
				default:
					System.out.println("Invalid option. Please try again.");

			}
		}
	}

	public static void main(String[] args) {
		Superadmin newSuperadmin = new Superadmin("Sae@pomona.edu", "password", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
		newSuperadmin.userInput();
	}


}
