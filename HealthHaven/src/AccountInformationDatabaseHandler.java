import java.time.LocalDate;

/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class AccountInformationDatabaseHandler {

	public static String createAccount(String userId, String email, String password, String legal_first_name,
			String legal_last_name, String address, String dob, String timestamp) {
		if (accountExistsById(userId)) {
			return "ERROR ACCOUNT EXISTS";
		}
		return "SUCCESS";

	}
	
	public static String updateAccountInformation(String userId, String newEmail, String newPassword, String newAddress) {
		return "SUCCESS";
	}

	private static boolean accountExistsById(String userId) {
		return false;
	}
	
	public static boolean accountExistsByEmail(String email) {
		return true;
	}
	
	public static String authenticateAccount(String email, String password, String timestamp) {
		if (!accountExistsByEmail(email)) {
			return "INVALID";
		}
		return "This is the account information";
	}

}
