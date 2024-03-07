
/**
 * 
 */

/**
 * @author sameermalik
 *
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;

public class User {

	private String password;
	// The format of the userID will correspond to the account type
	private double userID;
	private String email;
	private String legal_first_name;
	private String legal_last_name;
	private String address;
	private LocalDate dob;
	
	public static enum Account{
		DOCTOR("Doctor"), 
		PATIENT( "Patient"),
		DATA_ANALYST("Data Analyst"),
		SUPERADMIN("Superadmin"),
		DPO("Data Protection Officer"),
		NONE("NONE");
		private String account_name;
		private Account(String account_name) {
			this.account_name = account_name;
		}
		
		String getAccountName() {
			return this.account_name;
		}
	}
	
	private static Account ACCOUNT_TYPE = Account.NONE;

	/**
	 * New user
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public User(String email, String password, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super();
		this.email = email;
		this.password = password;
		this.legal_first_name = legal_first_name;
		this.legal_last_name = legal_last_name;
		this.address = address;
		this.dob = dob;
	}

	/**
	 * For existing user
	 * @param userID
	 * @param password
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public User(double userID, String email, String password, String legal_first_name, String legal_last_name, String address, LocalDate dob) {
		this.userID = userID;
		this.password = password;
		this.email = email;
		this.legal_first_name = legal_first_name;
		this.legal_last_name = legal_last_name;
		this.address = address;
		this.dob = dob;
		this.generateUserID();
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	protected void generateUserID() {
	}

	/**
	 * @return the userID
	 */
	protected double getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	protected void setUserID(double userID) {
		this.userID = userID;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the legal_first_name
	 */
	public String getLegal_first_name() {
		return legal_first_name;
	}

	/**
	 * @param legal_first_name the legal_first_name to set
	 */
	public void setLegal_first_name(String legal_first_name) {
		this.legal_first_name = legal_first_name;
	}

	/**
	 * @return the legal_last_name
	 */
	public String getLegal_last_name() {
		return legal_last_name;
	}

	/**
	 * @param legal_last_name the legal_last_name to set
	 */
	public void setLegal_last_name(String legal_last_name) {
		this.legal_last_name = legal_last_name;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the dob
	 */
	public LocalDate getDob() {
		return dob;
	}

	/**
	 * @param dob the dob to set
	 */
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

//	protected String communicateWithServer(String message, int serverPort) {
//		StringBuilder response = new StringBuilder();
//
//		try (Socket socket = new Socket("localhost", serverPort);
//			 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//			 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//
//			// Send the message to the server
//			writer.write(message);
//			writer.newLine();
//			writer.flush();
//
//			// Read the response
//			String line;
//			while ((line = reader.readLine()) != null) {
//				response.append(line);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "Error communicating with server.";
//		}
//
//		return response.toString();
//	}
protected void updatePersonalRecord(Scanner scanner) {
	String newEmail = this.email; // Start with current values
	String newAddress = this.address;
	String newPassword = this.password;

	boolean updating = true;
	while (updating) {
		System.out.println("What do you want to update?");
		System.out.println("1. Email Address");
		System.out.println("2. Address");
		System.out.println("3. Password");
		System.out.println("4. Save & Quit to main menu");
		System.out.println("5. Cancel & Quit to main menu");
		int updateChoice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline
		switch (updateChoice) {
			case 1:
				System.out.print("Enter new email address: ");
				newEmail = scanner.nextLine();
				System.out.println("Email address set for update.");
				break;
			case 2:
				System.out.print("Enter new address: ");
				newAddress = scanner.nextLine();
				System.out.println("Address set for update.");
				break;
			case 3:
				System.out.print("Enter new password: ");
				newPassword = scanner.nextLine();
				System.out.println("Password set for update.");
				break;
			case 4:
				// Apply changes and communicate with the server to update the database
				setEmail(newEmail);
				setAddress(newAddress);
				setPassword(newPassword);

				System.out.println(this);

				// Assuming communicateWithServer is a method that takes the updates and sends them to the server
				String updateMessage = String.format("UPDATE %f %s %s %s", getUserID(), newEmail, newPassword, newAddress);
				String serverResponse = ServerCommunicator.communicateWithAccountServer(updateMessage);
				if (serverResponse.equals("SUCCESS")) {
					System.out.println("Updates saved successfully.");
				} else {
					System.out.println("Failed to save updates.");
				}
				updating = false; // Quit to main menu
				break;
			case 5:
				updating = false; // Quit to main menu without saving
				System.out.println("Updates canceled.");
				break;
			default:
				System.out.println("Invalid option. Please try again.");
		}
	}
}


	protected void userInput(){

	}

	@Override
	public String toString() {
		return "User [password=" + password + ", userID=" + userID + ", email=" + email + ", legal_first_name="
				+ legal_first_name + ", legal_last_name=" + legal_last_name + ", address=" + address + ", dob=" + dob
				+ ", Account Type=" + ACCOUNT_TYPE.getAccountName() + "]";
	}

	
}
