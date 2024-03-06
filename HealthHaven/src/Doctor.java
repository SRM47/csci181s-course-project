import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
	
	
	public void updatePatientRecord(int userID, String feature, String newData, int serverPort) {
        // Construct a message to send to the server
        String message = String.format("UPDATE %d %s %s", userID, feature, newData);
        String serverResponse = communicateWithServer(message, serverPort);
        System.out.println("Server response: " + serverResponse);
    }
	
	public void viewPatientRecord(int userID, int serverPort) {
        String message = String.format("VIEW %d", userID);
        String serverResponse = communicateWithServer(message, serverPort);
        System.out.println("Server response: " + serverResponse);
    }

    public void createPatient(String password, String email, String legal_first_name, String legal_last_name, String address,
                              LocalDate dob, int serverPort) {
        String message = String.format("CREATE %s %s %s %s %s %s", password, email, legal_first_name, legal_last_name, address, dob);
        String serverResponse = communicateWithServer(message, serverPort);
        System.out.println("Server response: " + serverResponse);
    }
	
    public void userInput() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Update patient record");
            System.out.println("2. View patient record");
            System.out.println("3. Create a new client");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt(); // Read the user's choice

            switch (choice) {
                case 1:
                    // Prompt for details needed to update a patient record
                    System.out.print("Enter patient ID: ");
                    int userIDUpdate = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    System.out.print("Enter feature to update: ");
                    String feature = scanner.nextLine();
                    System.out.print("Enter new data: ");
                    String newData = scanner.nextLine();
                    updatePatientRecord(userIDUpdate, feature, newData, 8888);
                    break;
                case 2:
                    // Prompt for patient ID to view their record
                    System.out.print("Enter patient ID: ");
                    int userIDView = scanner.nextInt();
                    viewPatientRecord(userIDView, 8888);
                    break;
                case 3:
                    // Exit the method
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        
        public static void main() {
        	Doctor newDoctor = new Doctor("password", "Sae@pomona.edu", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
        	newDoctor.userInput();
        }
	
	

}
