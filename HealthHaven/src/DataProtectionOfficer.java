import java.time.LocalDate;
import java.util.Random;



/**
 * 
 */

/**
 * @author sameermalik and maxbaum
 *
 */
public class DataProtectionOfficer extends User {
	private static Account ACCOUNT_TYPE = Account.DPO;

	/**
	 * New DPO
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public DataProtectionOfficer(String email, String password, String legal_first_name, String legal_last_name,
			String address, LocalDate dob) {
		super(email, password, legal_first_name, legal_last_name, address, dob);

	}

	/**
	 * Existing DPO
	 * @param userID
	 * @param email
	 * @param password
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public DataProtectionOfficer(long userID, String email, String password, String legal_first_name, String legal_last_name,
								 String address, LocalDate dob) {
		super(userID, email, password, legal_first_name, legal_last_name, address, dob);
	}
	
	@Override
	protected void generateUserID() {
		Random random = new Random();
		
		// UserID created with random six digit number base. User class is appended as highest order digit.
		long randomNumber = 4_000_000_000L + (long)(random.nextDouble() * 999_999_999L);
		this.setUserID(randomNumber);
	}

}
