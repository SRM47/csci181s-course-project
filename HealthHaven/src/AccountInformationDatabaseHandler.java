import java.time.LocalDate;
import java.util.ArrayList;

/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class AccountInformationDatabaseHandler {
	
	private static String databaseName = "medical_records.csv";

	public static String createAccount(String userId, String email, String password, String legal_first_name,
			String legal_last_name, String address, String dob, String timestamp) {
		if (accountExistsById(userId)) {
			return "ERROR_ACCOUNT_EXISTS";
		}
		
		String[] newData = { userId, email, password,  legal_first_name,
				 legal_last_name,  address,  dob,  timestamp };
		boolean success = CSVHandler.appendToCSV(databaseName, newData);
		return success ? "SUCCESS" : "FAILURE";

	}
	
	public static String updateAccountInformation(String userId, String newEmail, String newPassword, String newAddress) {
		String currentAccountInformation = getUserAccountInformation(userId);
		if (currentAccountInformation == null) {
			return null;
		}
		String[] parts = currentAccountInformation.split(",");
		parts[1] = newEmail;
		parts[2] = newPassword;
		parts[5] = newAddress;
		boolean success = CSVHandler.appendToCSV(databaseName, parts);
		return success ? "SUCCESS" : "FAILURE";
		
	}

	private static boolean accountExistsById(String userId) {
		ArrayList<String> userIds = CSVHandler.readColumnValue(databaseName, 0);
		return userIds.indexOf(userId) >= 0;
	}
	
	public static boolean accountExistsByEmail(String email) {
		ArrayList<String> userIds = CSVHandler.readColumnValue(databaseName, 1);
		return userIds.indexOf(email) >= 0;
	}
	
	public static String authenticateAccount(String email, String password, String timestamp) {
		if (!accountExistsByEmail(email)) {
			return "INVALID";
		}
		return "This is the account information";
	}
	
	public static String getUserAccountInformation(String userId) {
		if (!accountExistsById(userId)) {
			return null;
		}
		ArrayList<String> userIds = CSVHandler.readColumnValue(databaseName, 0);
		int index = userIds.indexOf(userId);
		return CSVHandler.readRowByIndex(databaseName, index);
	}

}
