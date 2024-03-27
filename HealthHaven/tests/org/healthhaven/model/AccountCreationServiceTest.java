package org.healthhaven.model;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

class AccountCreationServiceTest {

    @Test
    void testDoesAccountExist() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Mocking the server response for an existing account
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer("EXISTING_ACCOUNT user@example.com")).thenReturn("EXISTS");

            // Testing for an existing account
            String response = AccountCreationService.doesAccountExist("user@example.com");
            assertEquals("EXISTS", response);
        }
    }

    @Test
    void testCreateUserInstanceSuccess() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Mocking the server response for a successful account creation
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("SUCCESS");

            // Attempt to create a new user instance
            User newUser = AccountCreationService.createUserInstance(User.Account.PATIENT, "user@example.com", "password123", "John", "Doe", "123 Main St", LocalDate.of(1990, 1, 1));

            // Verify that a new user instance is created successfully
            assertNotNull(newUser, "A new user instance should be created on successful account creation.");
            assertTrue(newUser instanceof Patient, "The created user instance should be of type Patient.");
        }
    }

    @Test
    void testCreateUserInstanceFailure() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Mocking the server response for a failed account creation
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("FAILURE");

            // Attempt to create a new user instance
            User newUser = AccountCreationService.createUserInstance(User.Account.PATIENT, "user@example.com", "password123", "John", "Doe", "123 Main St", LocalDate.of(1990, 1, 1));

            // Verify that no user instance is created on failure
            assertNull(newUser, "No user instance should be created on failed account creation.");
        }
    }

    // Additional tests can follow the same pattern for different scenarios
}
