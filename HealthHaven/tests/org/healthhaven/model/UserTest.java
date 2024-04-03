package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDate;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
    
    @Test
    public void testUpdatePersonalRecordOnDB() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Prepare the mock to return a dummy response
            String expectedResponse = "Server response";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            // Update the address
            String newAddress = "456 Elm St";
            String actualResponse = user.updatePersonalRecordOnDB(newAddress);

            // Capture the JSON payload sent to the server
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            mockedStatic.verify(() -> ServerCommunicator.communicateWithServer(captor.capture()));
            String capturedJson = captor.getValue();

            // Assert the local change
            assertEquals(newAddress, user.getAddress(), "Address should be updated in the User object");

            // Assert the server communication
            assertEquals(expectedResponse, actualResponse, "Should return the server response");

            // Validate the JSON payload
            JSONObject json = new JSONObject(capturedJson);
            assertEquals("UPDATE_ACCOUNT", json.getString("action"));
            assertEquals(newAddress, json.getString("address"));
        }
    }

//    @Override
//    public User createUser() {
//        return new User("userId", "example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
//    }
//
//    @Override
//    protected Account getExpectedAccountType() {
//        // Return the expected Account type for the specific test subclass
//        return Account.NONE; // Or the appropriate Account type for this test
//    }



    
}
