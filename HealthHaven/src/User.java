
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
	
	protected static enum Account{
		DOCTOR("Doctor"), 
		PATIENT("Patient"),
		DATA_ANALYST("Data Analyst"),
		SUPERADMIN("Superadmin"),
		DPO("Data Protection Officer"),
		NONE("NONE");
		private String account_name;
		private Account(String account_name) {
			this.account_name = account_name;
		}
		
		private String getAccountName() {
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
	public User(String email, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super();
		this.email = email;
		this.legal_first_name = legal_first_name;
		this.legal_last_name = legal_last_name;
		this.address = address;
		this.dob = dob;
		this.generateUserID();
		this.password = PasswordGenerator.generate(10); // Example: generate a 10-character password
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
	public User(double userID, String password, String email, String legal_first_name, String legal_last_name, String address, LocalDate dob) {
		this.userID = userID;
		this.password = password;
		this.email = email;
		this.legal_first_name = legal_first_name;
		this.legal_last_name = legal_last_name;
		this.address = address;
		this.dob = dob;
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
	public void updatePersonalRecord(Scanner scanner) {
		boolean updating = true;
		while (updating) {
			System.out.println("What do you want to update?");
			System.out.println("1. Email Address");
			System.out.println("2. Address");
			System.out.println("3. Password");
			System.out.println("4. Quit to main menu");
			int updateChoice = scanner.nextInt();
			scanner.nextLine(); // Consume the newline
			switch (updateChoice) {
				case 1:
					System.out.print("Enter new email address: ");
					String newEmail = scanner.nextLine();
					setEmail(newEmail);
					System.out.println("Email address updated.");
					break;
				case 2:
					System.out.print("Enter new address: ");
					String newAddress = scanner.nextLine();
					setAddress(newAddress);
					System.out.println("Address updated.");
					break;
				case 3:
					System.out.print("Enter new password: ");
					String newPassword = scanner.nextLine();
					setPassword(newPassword);
					System.out.println("Password updated.");
					break;
				case 4:
					updating = false; // Quit to main menu
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
