package org.healthhaven.model;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.MockedStatic;

public class LoginTest {

    @Test
    public void testAuthenticateExistingUserSuccess() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Mock the static method to return a successful response
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("SUCCESS");

            String expectedResponse = "SUCCESS";
            String actualResponse = Login.authenticateExistingUser("user@example.com", "correctPassword");
            assertEquals(expectedResponse, actualResponse, "Authentication should return SUCCESS for correct credentials.");
        } // The mocked static method is automatically reset after this block
    }

    @Test
    public void testAuthenticateExistingUserFailure() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Mock the static method to return a failure response
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("FAILURE");

            String expectedResponse = "FAILURE";
            String actualResponse = Login.authenticateExistingUser("user@example.com", "wrongPassword");
            assertEquals(expectedResponse, actualResponse, "Authentication should return FAILURE for incorrect credentials.");
        } // The mocked static method is automatically reset after this block
    }

    @Test
    public void testCreateUserInstanceDoctor() {
        // This test remains unchanged as it doesn't involve mocking static methods
        User doctor = Login.createUserInstance("Doctor", 1L, "doc@example.com", "securePass", "Doc", "Tor", "123 Healing Blvd", LocalDate.of(1980, 1, 1));
        assertNotNull(doctor, "createUserInstance should return a non-null Doctor instance.");
        assertTrue(doctor instanceof Doctor, "createUserInstance should return an instance of Doctor when 'Doctor' account type is specified.");
    }

    // Additional tests can follow the same pattern
}
