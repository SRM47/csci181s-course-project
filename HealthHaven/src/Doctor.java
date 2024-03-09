import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;
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
     * New doctor
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
    
	public Doctor(String email, String password, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(email, password, legal_first_name, legal_last_name, address, dob);
		generateUserID();
	}

    /**
     * Existing doctor
     * @param userID
     * @param email
     * @param password
     * @param legal_first_name
     * @param legal_last_name
     * @param address
     * @param dob
     */
    public Doctor(long userID, String email, String password, String legal_first_name, String legal_last_name,
                   String address, LocalDate dob){
        super(userID, email, password, legal_first_name, legal_last_name, address, dob);

    }

    /**
     * User ID with left most digit = 1
     */
	@Override
	protected void generateUserID() {
		Random rnd = new Random();
        long randomNumber = 1_000_000_000L + (long)(rnd.nextDouble() * 999_999_999L);
	    this.setUserID(randomNumber);
	}

    /**
     * Display and update the patient record (include user prompt)
     * @param scanner
     */
	protected void viewAndUpdatePatientRecord(Scanner scanner){
        System.out.print("Enter patient ID: ");
        long userIDView = scanner.nextLong();
        System.out.println(viewPatientRecord(userIDView));

        System.out.print("Do you want to update this patient record? 1 (yes) 2 (no): ");
        int subChoice = scanner.nextInt();
        switch (subChoice){
            case 1:
                System.out.println("Updating the patient record");
                // Prompt for details needed to update a patient record
                scanner.nextLine();
                System.out.print("Enter height to update: ");
                String feature = scanner.nextLine();
                System.out.print("Enter weight to update: ");
                String newData = scanner.nextLine();
                System.out.println( updatePatientRecordOnDB(userIDView, feature, newData));
                break;
            case 2:
                System.out.println("Not updating the patient record");
                break;
            default:
                System.out.println("Invalid option. Please try again");
        }
    }

    /**
     * Update the patient record on DB, communicate with the server, return the server response.
     * @param userID
     * @param height
     * @param weight
     * @return
     */
	protected String updatePatientRecordOnDB(long userID, String height, String weight) {
        Instant timestamp = Instant.now(); // This captures the current moment in UTC.

        // Construct a message to send to the server
        String message = String.format("UPDATE_RECORD %d %s %s %s", userID, height, weight, timestamp.toString());
        System.out.println("message: "+ message);
        return ServerCommunicator.communicateWithMedicalServer(message);
    }

    /**
     * View patient record, return the server response
     * @param userID
     * @return
     */
	protected String viewPatientRecord(long userID) {
        String message = String.format("VIEW %d", userID);
        System.out.println("Message: " + message);
        return ServerCommunicator.communicateWithMedicalServer(message);
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

            String input = scanner.nextLine(); // Use nextLine to read the input as String
            int choice;
            try {
                choice = Integer.parseInt(input); // Parse the input String to an integer
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            switch (choice) {
                case 1:
                    accessPersonalRecord(scanner);
                    break;
                case 2:
                    // Prompt for patient ID to view their record
                    viewAndUpdatePatientRecord(scanner);
                    break;
                case 3:
                    // Create a new patient account
                    System.out.println("Creating a new patient's account");
                    Patient newPatient = (Patient) AccountCreationService.createAccount(scanner, Account.PATIENT);
                    break;
                case 4:
                    // Exit the method
                    System.out.println("Exiting...");
                    // Do not close the scanner if it's System.in as it will close System.in globally.
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
