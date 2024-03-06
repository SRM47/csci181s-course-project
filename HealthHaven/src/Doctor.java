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
	 * @param password
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
    
	public Doctor(String password, String email, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(password, email, legal_first_name, legal_last_name, address, dob);
		generateUserID();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void generateUserID() {
		Random rnd = new Random();
	    long randomNumber = 1_000_000_000_00L + (long)(rnd.nextDouble() * 9_000_000_000_00L);
	    this.setUserID(randomNumber);
	}
	
	private String communicateWithServer(String message, int serverPort) {
        StringBuilder response = new StringBuilder();

        try (Socket socket = new Socket("localhost", serverPort);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the message to the server
            writer.write(message);
            writer.newLine();
            writer.flush();

            // Read the response
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error communicating with server.";
        }

        return response.toString();
    }
	
	
	public void updatePatientRecord(long userID, String height, String weight, int serverPort) {
        Instant timestamp = Instant.now(); // This captures the current moment in UTC.

        // Construct a message to send to the server
        String message = String.format("UPDATE %d %s %s %s", userID, height, weight, timestamp.toString());
        System.out.println("message: "+ message);
        String serverResponse = communicateWithServer(message, serverPort);
        System.out.println("Server response: " + serverResponse);

        // Example: Use the timestamp
        System.out.println("Record updated at: " + timestamp);
    }
	
	public void viewPatientRecord(long userID, int serverPort) {
        String message = String.format("VIEW %d", userID);
        String serverResponse = communicateWithServer(message, serverPort);
        System.out.println("Server response: " + serverResponse);
    }

    public void createPatient(String email, String legal_first_name, String legal_last_name, String address,
                              LocalDate dob, int serverPort) {
        Instant timestamp = Instant.now(); // This captures the current moment in UTC.
        String message = String.format("CREATE %s %s %s %s %s %s", email, legal_first_name, legal_last_name, address, dob, timestamp.toString());
        String serverResponse = communicateWithServer(message, serverPort);
        System.out.println("Server response: " + serverResponse);
    }
	
    public void userInput() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Access my info:");
            System.out.println("2. Access patient record");
            System.out.println("3. Create a new client");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt(); // Read the user's choice
            int subChoice;
            switch (choice) {
                case 1:
                    // Access doctor's own info.
                    System.out.println(toString());
                    System.out.print("Do you want to update your record? 1 (yes) 2 (no): ");
                    subChoice = scanner.nextInt();
                    switch(subChoice){
                        case 1:
                            updatePersonalRecord(scanner);
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
                    viewPatientRecord(userIDView, 8889);
                    System.out.print("Do you want to update this patient record? 1 (yes) 2 (no): ");
                    subChoice = scanner.nextInt();
                    switch (subChoice){
                        case 1:
                            System.out.println("Updating the patient record");
                            // Prompt for details needed to update a patient record
                            System.out.print("Enter patient ID: ");
                            long userIDUpdate = scanner.nextLong();
                            scanner.nextLine(); // Consume newline left-over
                            System.out.print("Enter feature to update: ");
                            String feature = scanner.nextLine();
                            System.out.print("Enter new data: ");
                            String newData = scanner.nextLine();
                            updatePatientRecord(userIDUpdate, feature, newData, 8889);
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
                    System.out.print("Enter the patient's email: ");
                    String patientEmail = scanner.nextLine();
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
                    createPatient(patientEmail, firstName, lastName, address, dateOfBirth, 8889);

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
        Doctor newDoctor = new Doctor("password", "Sae@pomona.edu", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
        newDoctor.userInput();
    }

}
