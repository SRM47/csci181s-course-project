import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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

    public Doctor(double userID, String email, String password, String legal_first_name, String legal_last_name,
                   String address, LocalDate dob){
        super(userID, email, password, legal_first_name, legal_last_name, address, dob);

    }
	
	@Override
	protected void generateUserID() {
		Random rnd = new Random();
	    long randomNumber = 1_000_000_000_00L + (long)(rnd.nextDouble() * 9_000_000_000_00L);
	    this.setUserID(randomNumber);
	}
	
	
	private void updatePatientRecord(long userID, String height, String weight) {
        Instant timestamp = Instant.now(); // This captures the current moment in UTC.

        // Construct a message to send to the server
        String message = String.format("UPDATE %d %s %s %s", userID, height, weight, timestamp.toString());
        System.out.println("message: "+ message);
        String serverResponse = ServerCommunicator.communicateWithMedicalServer(message);
        System.out.println("Server response: " + serverResponse);
    }
	
	private void viewPatientRecord(long userID) {
        String message = String.format("VIEW %d", userID);
        System.out.println("Message: " + message);
        String serverResponse = ServerCommunicator.communicateWithMedicalServer(message);
        System.out.println("Server response: " + serverResponse);
    }

    @Override
    protected void userInput() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Access my info");
            System.out.println("2. Access patient record");
            System.out.println("3. Create a new client");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt(); // Read the user's choice
            int subChoice;
            switch (choice) {
                case 1:
                    // Access doctor's own info.
                    System.out.println(this);
                    System.out.print("Do you want to update your record? 1 (yes) 2 (no): ");
                    subChoice = scanner.nextInt();
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
                    long userIDView = scanner.nextLong();
                    viewPatientRecord(userIDView);

                    System.out.print("Do you want to update this patient record? 1 (yes) 2 (no): ");
                    subChoice = scanner.nextInt();
                    switch (subChoice){
                        case 1:
                            System.out.println("Updating the patient record");
                            // Prompt for details needed to update a patient record
                            scanner.nextLine();
                            System.out.print("Enter height to update: ");
                            String feature = scanner.nextLine();
                            System.out.print("Enter weight to update: ");
                            String newData = scanner.nextLine();
                            updatePatientRecord(userIDView, feature, newData);
                            break;
                        case 2:
                            System.out.println("Not updating the patient record");
                            break;
                        default:
                            System.out.println("Invalid option. Please try again");
                    }
                    break;
                case 3:
                    //Create a new patient account
                    System.out.println("Creating a new patient's account");
                    Patient newPatient = (Patient) AccountCreationService.createAccount(scanner, Account.PATIENT);
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
        Doctor newDoctor = new Doctor("Sae@pomona.edu", "password", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
        newDoctor.userInput();
    }

}
