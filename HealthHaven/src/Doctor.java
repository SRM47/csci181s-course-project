import java.time.LocalDate;
import java.util.Random;
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
	private String serverAddress = "your.server.address"; // Replace with your actual server address
    private int serverPort = 12345; // Replace with your actual server port

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
	
	private String communicateWithServer(String message) {
        StringBuilder response = new StringBuilder();

        try (Socket socket = new Socket(serverAddress, serverPort);
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
	
	
	public void updatePatientRecord(int userID, String feature, String newData) {
        // Construct a message to send to the server
        String message = String.format("UPDATE %d %s %s", userID, feature, newData);
        String serverResponse = communicateWithServer(message);
        System.out.println("Server response: " + serverResponse);
    }
	
	public void viewPatientRecord(int userID) {
        String message = String.format("VIEW %d", userID);
        String serverResponse = communicateWithServer(message);
        System.out.println("Server response: " + serverResponse);
    }

    public void createPatient(String password, String email, String legal_first_name, String legal_last_name, String address,
                              LocalDate dob) {
        String message = String.format("CREATE %s %s %s %s %s %s", password, email, legal_first_name, legal_last_name, address, dob);
        String serverResponse = communicateWithServer(message);
        System.out.println("Server response: " + serverResponse);
    }
	
	
	

}
