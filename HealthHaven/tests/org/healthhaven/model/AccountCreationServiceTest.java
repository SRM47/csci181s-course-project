package org.healthhaven.model;

import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

public class AccountCreationServiceTest {
    private MockedStatic<ServerCommunicator> mockedServerCommunicator;

    @BeforeEach
    public void setUp() {
        // Initialize the mocked static method before each test
        mockedServerCommunicator = Mockito.mockStatic(ServerCommunicator.class);
        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn("Mock Response");
    }

    @Test
    public void testCreateUserForAllUserTypes() {
        // An array of all user types to test, including an undefined type to trigger the default case
        String[] userTypes = {"PATIENT", "DOCTOR", "DATA_ANALYST", "DPO", "SUPERADMIN", "UNDEFINED"};
        for (String userType : userTypes) {
            String result = AccountCreationService.createUser(userType, "test@example.com", "password", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
            assertEquals("Mock Response", result, "The response for userType " + userType + " should be 'Mock Response'");
        }
    }

    @Test
    public void testCreateUserWithInvalidUserType() {
        // Testing with an explicitly invalid user type to ensure the method handles it gracefully
        String result = AccountCreationService.createUser("INVALID_TYPE", "test@example.com", "password", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
        assertEquals("Mock Response", result, "The response for an invalid userType should be 'Mock Response'");
    }

    @Test
    public void testDetailsForPatientAccountCreation() {
        // Using ArgumentCaptor to capture the JSON string sent to ServerCommunicator
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        
        // Perform the operation to capture the argument
        AccountCreationService.createUser("PATIENT", "jane.doe@example.com", "securepassword", "Jane", "Doe", "456 Elm Street", LocalDate.of(1990, 5, 15));
        
        // Capture the argument passed to communicateWithServer
        mockedServerCommunicator.verify(() -> ServerCommunicator.communicateWithServer(argumentCaptor.capture()));
        String capturedArgument = argumentCaptor.getValue();

        // Assertions can be expanded to check for specific JSON fields
        assertEquals(true, capturedArgument.contains("\"accountType\":\"Patient\""), "The JSON must contain the patient account type.");
        assertEquals(true, capturedArgument.contains("\"email\":\"jane.doe@example.com\""), "The JSON must contain the correct email.");
    }


    @AfterEach
    public void tearDown() {
        // Close the mocked static method after each test
        mockedServerCommunicator.close();
    }
}
