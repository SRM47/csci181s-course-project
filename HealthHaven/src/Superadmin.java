import java.time.LocalDate;

/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class Superadmin extends User {
	private static Account ACCOUNT_TYPE = Account.SUPERADMIN;

	/**
	 * @param password
	 * @param email
	 * @param legal_first_name
	 * @param legal_last_name
	 * @param address
	 * @param dob
	 */
	public Superadmin(String password, String email, String legal_first_name, String legal_last_name, String address,
			LocalDate dob) {
		super(password, email, legal_first_name, legal_last_name, address, dob);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void generateUserID() {
		this.setUserID(47);
	}

}
