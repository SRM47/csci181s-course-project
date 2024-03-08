import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class UserTest<T extends User> {
	
	T user;

	/**
	 * Creates an instance of T extending User for testing.
	 * @return a new instance of T
	 */
	public abstract T createUser();
	
	@BeforeEach
    public void setUp() {
        user = createUser();
    }
	
	@Test
	public void testGetPassword() {
		assertEquals("password123", user.getPassword(), "Password should match the one set in createUser");
	}

    @Test
    public void testGetEmail() {
        assertEquals("example@example.com", user.getEmail(), "Email should match the one set in createUser");
    }

    @Test
    public void testGetLegalFirstName() {
        assertEquals("John", user.getLegal_first_name(), "First name should match the one set in createUser");
    }

    @Test
    public void testGetLegalLastName() {
        assertEquals("Doe", user.getLegal_last_name(), "Last name should match the one set in createUser");
    }

    @Test
    public void testGetAddress() {
        assertEquals("123 Main St", user.getAddress(), "Address should match the one set in createUser");
    }

    @Test
    public void testGetDob() {
        assertEquals(LocalDate.of(1980, 1, 1), user.getDob(), "Date of birth should match the one set in createUser");
    }
    
//    @Test
//    public void testAccessPersonalRecord() {
//        // Mock the server response if possible or simulate the effect of calling this method
//        // Assuming a mock server response setup...
//        String serverResponse = user.updatePersonalRecordOnDB("newemail@gmail.com", "password123", "newAddress");
//        // Assert something about serverResponse or the state change in the user object
//    }
}
