package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDate;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

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
    public void testGetAccountType() {
        // The specific subclass tests will provide the expected Account type
        Account expected = getExpectedAccountType();
        assertEquals(expected, user.getAccountType(), "Account type should match the expected type.");
    }
	
	protected abstract Account getExpectedAccountType();
	
//	@Test
//	public void testGetPassword() {
//		assertEquals("password123", user.getPassword(), "Password should match the one set in createUser");
//	}

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

    @Test
    public void testSetEmail(){
        user.setEmail("newEmail@gmail.com");
        assertEquals("newEmail@gmail.com", user.getEmail(), "The user should be able to update the password");
    }
//    @Test
//    public void testSetPassword(){
//        user.setPassword("newpassword");
//        assertEquals("newpassword", user.getPassword(), "The user should be able to update the password");
//    }

    @Test
    public void testSetAddress(){
        user.setAddress("New York, NY");
        assertEquals("New York, NY", user.getAddress(), "The user should be able to update the password");
    }

//    @Test
//    public void testUpdatePersonalRecordOnDB() {
//        User user = createUser();
//        long userID = 12345L; // Set this to match your user's ID setup
//        user.setUserID(userID); // Assuming there's a method to set the user's ID. If not, adjust as needed.
//
//        String newEmail = "newemail@example.com";
//        String newPassword = "newPassword123";
//        String newAddress = "456 New St";
//
//        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
//            // Mock the static method call
//            String expectedServerResponse = "SUCCESS";
//            String expectedUpdateMessage = String.format("UPDATE_ACCOUNT %d %s %s %s", userID, newEmail, newPassword, newAddress);
//            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(expectedUpdateMessage))
//                        .thenReturn(expectedServerResponse);
//
//            // Execute the method under test
//            String actualResponse = user.updatePersonalRecordOnDB(newEmail, newPassword, newAddress);
//
//            // Verify the method's behavior
//            assertEquals(newEmail, user.getEmail(), "Email should be updated in the user object.");
//            assertEquals(newPassword, user.getPassword(), "Password should be updated in the user object.");
//            assertEquals(newAddress, user.getAddress(), "Address should be updated in the user object.");
//            assertEquals(expectedServerResponse, actualResponse, "The server response should match the expected response.");
//        }
//    }

    
//    @Test
//    public void testAccessPersonalRecord() {
//        // Mock the server response if possible or simulate the effect of calling this method
//        // Assuming a mock server response setup...
//        String serverResponse = user.updatePersonalRecordOnDB("newemail@gmail.com", "password123", "newAddress");
//        // Assert something about serverResponse or the state change in the user object
//    }
}
