import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class Patient extends User {
	private static Account ACCOUNT_TYPE = Account.PATIENT;

	/**
	 * @param password
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public Patient(String password, String email, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(password, email, legal_first_name, legal_last_name, address, dob);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void generateUserID() {
		Random rnd = new Random();
		long randomNumber = 2_000_000_000_00L + (long)(rnd.nextDouble() * 9_000_000_000_00L);
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

	public void viewPatientRecord(int userID, int serverPort) {
		String message = String.format("VIEW %d", userID);
		String serverResponse = communicateWithServer(message, serverPort);
		System.out.println("Server response: " + serverResponse);
	}

	public void userInput() {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("\nPlease choose an option:");
			System.out.println("1. View patient record");
			System.out.println("2. Exit");
			System.out.print("Enter your choice: ");

			int choice = scanner.nextInt(); // Read the user's choice

			switch (choice) {
				case 1:
					// Prompt for patient ID to view their record
					System.out.print("Enter patient ID: ");
					int userIDView = scanner.nextInt();
					viewPatientRecord(userIDView, 8888);
					break;
				case 2:
					// Exit the method
					System.out.println("Exiting...");
					return;
				default:
					System.out.println("Invalid option. Please try again.");
			}
		}
	}

	public static void main(String[] args) {
		Patient newPatient = new Patient("password", "Sae@pomona.edu", "Sae", "Furukawa", "Claremont", LocalDate.of(2002, 10, 05));
		newPatient.userInput();
	}
	
	
	

}
