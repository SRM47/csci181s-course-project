
/**
 * 
 */

/**
 * @author sameermalik
 *
 */
import java.time.LocalDate;
import java.util.Scanner;

public class User {

	private String password;
	// The format of the userID will correspond to the account type
	private long userID;
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
	public User(long userID, String email, String password, String legal_first_name, String legal_last_name, String address, LocalDate dob) {
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
	protected long getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	protected void setUserID(long userID) {
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

	/**
	 *  User accessing personal record (include user prompt)
	 * @param scanner
	 */
	protected  void accessPersonalRecord(Scanner scanner){
		// Access doctor's own info.
		System.out.println(this);
		System.out.print("Do you want to update your record? 1 (yes) 2 (no): ");
		String input = scanner.nextLine(); // Use nextLine for subsequent inputs
		try {
			int subChoice = Integer.parseInt(input);
			switch(subChoice){
				case 1:
					updatePersonalRecord(scanner); //update personal record on database
					break;
				case 2:
					System.out.println("Not updating any personal data.");
					break;
				default:
					System.out.println("Invalid option. Please try again");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a number.");
		}
	}

	/**
	 * Update the personal record on the database, return the server response.
	 * @param newEmail
	 * @param newPassword
	 * @param newAddress
	 * @return
	 */
	protected String updatePersonalRecordOnDB(String newEmail, String newPassword, String newAddress){

		// First set the changes to the object instance.
		setEmail(newEmail);
		setAddress(newAddress);
		setPassword(newPassword);

		System.out.println(this);

		// Communicate with the server to update
		String updateMessage = String.format("UPDATE %f %s %s %s", getUserID(), newEmail, newPassword, newAddress);
		System.out.println("Message: " + updateMessage);
		return ServerCommunicator.communicateWithAccountServer(updateMessage);
	}

	/**
	 * Update the personal record (include user prompt)
	 * @param scanner
	 */
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
					//Actual updating
					String serverResponse = updatePersonalRecordOnDB(newEmail, newPassword, newAddress);
					System.out.println(serverResponse);
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
