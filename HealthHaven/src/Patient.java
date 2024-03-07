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
	public Patient(String email, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(email, legal_first_name, legal_last_name, address, dob);
		generateUserID();
	}

	public Patient(double userID, String password, String email, String legal_first_name, String legal_last_name,
				   String address, LocalDate dob){
        super(userID, password, email, legal_first_name, legal_last_name, address, dob);

    }

	@Override
	protected void generateUserID() {
		Random rnd = new Random();
		long randomNumber = 2_000_000_000_00L + (long)(rnd.nextDouble() * 9_000_000_000_00L);
		this.setUserID(randomNumber);
	}
	public void viewPatientRecord(int userID, int serverPort) {
		String message = String.format("VIEW %d", userID);
		String serverResponse = ServerCommunicator.communicateWithMedicalServer(message);
		System.out.println("Server response: " + serverResponse);
	}

	protected void userInput() {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("\nPlease choose an option:");
			System.out.println("1. View my personal data");
			System.out.println("2. View patient record");
			System.out.println("3. Exit");
			System.out.print("Enter your choice: ");

			int choice = scanner.nextInt(); // Read the user's choice

			switch (choice) {
				case 1:
					// Access doctor's own info.
					System.out.println(toString());
					System.out.print("Do you want to update your record? 1 (yes) 2 (no): ");
					int subChoice = scanner.nextInt();
					switch(subChoice){
						case 1:
							updatePersonalRecord(scanner);
							break;
						case 2:
							System.out.print("Not updating any personal data.");
							break;
						default:
							System.out.println("Invalid option. Please try again");
					}
					break;
				case 2:
					// Prompt for patient ID to view their record
					System.out.print("Enter patient ID: ");
					int userIDView = scanner.nextInt();
					viewPatientRecord(userIDView, 8889);
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
		Patient newPatient = new Patient("Sae@pomona.edu", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
		newPatient.userInput();
	}
	
	
	

}
