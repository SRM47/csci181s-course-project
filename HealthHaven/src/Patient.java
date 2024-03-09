import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

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
	public Patient(String email, String password, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(email, password, legal_first_name, legal_last_name, address, dob);
	}

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
	public Patient(long userID, String email, String password, String legal_first_name, String legal_last_name,
				   String address, LocalDate dob){
        super(userID, email, password, legal_first_name, legal_last_name, address, dob);

    }

	/**
	 * Generating user id with left most digit = 2
	 */
	@Override
	protected void generateUserID() {
		Random rnd = new Random();
		long randomNumber = 2_000_000_000L + (long)(rnd.nextDouble() * 999_999_999L);
		this.setUserID(randomNumber);
	}

	/**
	 * Display the patient record, return the server response.
	 * @param userID
	 */
	private String viewPatientRecord(long userID) {
		String message = String.format("VIEW %d", userID);
		System.out.println("Message: " + message);
		return ServerCommunicator.communicateWithMedicalServer(message);

	}
	@Override
	protected void userInput() {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("\nPlease choose an option:");
			System.out.println("1. View my personal data");
			System.out.println("2. View patient record");
			System.out.println("3. Exit");
			System.out.print("Enter your choice: ");

			int choice = scanner.nextInt(); // Read the user's choice
			scanner.nextLine();

			switch (choice) {
				case 1:
					// Access doctor's own info.
					accessPersonalRecord(scanner);
					break;
				case 2:
					// Prompt for patient ID to view their record
					System.out.println(viewPatientRecord(getUserID()));
					break;
				case 3:
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
		Patient newPatient = new Patient("Sae@pomona.edu", "password", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
		newPatient.userInput();
	}
	
	
	

}
