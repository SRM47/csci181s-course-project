package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
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
    private final String cookie = "sample_cookie_string";

    /**
     * Creates an instance of T extending User for testing, with a cookie.
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
            String expectedResponse = "Server response";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            String newAddress = "123 Main St";
            User user = createUser();  // Using the createUser method that now includes a cookie
            String actualResponse = user.updatePersonalRecordOnDB(newAddress, "address");

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            mockedStatic.verify(() -> ServerCommunicator.communicateWithServer(captor.capture()), atLeastOnce());
            String capturedJson = captor.getValue();

            assertEquals(newAddress, user.getAddress(), "Address should be updated in the User object");
            assertEquals(expectedResponse, actualResponse, "Should return the server response");

            JSONObject json = new JSONObject(capturedJson);
            assertEquals("UPDATE_ACCOUNT", json.getString("request"));
            assertEquals(newAddress, json.getString("address"));
        }
    }


    @Test
    public void testDeactivateValidate() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            String expectedResponse = "Success";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);
            
            String password = "secure123";
            String actualResponse = user.deactivate(password, "VALIDATE");

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            mockedStatic.verify(() -> ServerCommunicator.communicateWithServer(captor.capture()), atLeastOnce());
            String capturedJson = captor.getValue();

            JSONObject json = new JSONObject(capturedJson);
            assertEquals("DEACTIVATE_ACCOUNT", json.getString("request"));
            assertEquals("VALIDATE_ACCOUNT", json.getString("type"));
            assertEquals(password, json.getString("password"));
            assertEquals(expectedResponse, actualResponse, "Should return the expected success message from the server");
        }
    }

    @Test
    public void testDeactivateAccount() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            String expectedResponse = "Success";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);
            
            String actualResponse = user.deactivate("", "DEACTIVATE");

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            mockedStatic.verify(() -> ServerCommunicator.communicateWithServer(captor.capture()), atLeastOnce());
            String capturedJson = captor.getValue();

            JSONObject json = new JSONObject(capturedJson);
            assertEquals("DEACTIVATE_ACCOUNT", json.getString("request"));
            assertEquals("DEACTIVATE_ACCOUNT", json.getString("type"));
            assertEquals(user.getUserID(), json.getString("userId"));
            assertEquals(expectedResponse, actualResponse, "Should return the expected success message from the server");
        }
    }



    
}
