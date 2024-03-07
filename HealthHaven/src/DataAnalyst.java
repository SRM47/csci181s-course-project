import java.time.LocalDate;
import java.util.Random;


/**
 * 
 */

/**
 * @author sameermalik and maxbaum
 *
 */
public class DataAnalyst extends User {
	private static Account ACCOUNT_TYPE = Account.DATA_ANALYST;

	/**
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public DataAnalyst(String email, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(email, legal_first_name, legal_last_name, address, dob);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void generateUserID() {
		Random random = new Random();
		
		// UserID created with random six digit number base. User class is appended as highest order digit. 
		
		int userIDrand = (random.nextInt(999999-100000)+100000) + 1000000;
		
		this.setUserID(userIDrand);
	}

}
