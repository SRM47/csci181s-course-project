package org.healthhaven.model;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Scanner;

class AccountCreationServiceTest {
	
    private final InputStream systemIn = System.in;
    private ByteArrayInputStream testIn;
    
    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
    }

    @Test
    public void testSelectValidAccountType() {
        provideInput("2\n"); // Simulate user input for selecting Doctor
        Scanner scanner = new Scanner(System.in);
        assertEquals(2, AccountCreationService.selectAccountType(scanner), "Should return 2 for Doctor.");
    }

    @Test
    public void testSelectInvalidThenValidAccountType() {
        provideInput("6\n2\n"); // First input is invalid, second input is for Doctor
        Scanner scanner = new Scanner(System.in);
        assertEquals(2, AccountCreationService.selectAccountType(scanner), "Should handle invalid input then return 2 for Doctor.");
    }

    @Test
    public void testSelectNonNumericThenValidAccountType() {
        provideInput("abc\n3\n"); // First input is non-numeric, second input is for Data Science Analyst
        Scanner scanner = new Scanner(System.in);
        assertEquals(3, AccountCreationService.selectAccountType(scanner), "Should handle non-numeric input then return 3 for Data Science Analyst.");
    }

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
    void testCreateDoctorInstanceSuccess() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("SUCCESS");
            
            User newUser = AccountCreationService.createUserInstance(User.Account.DOCTOR, "doctor@example.com", "password123", "Jane", "Doe", "456 Clinic Ave", LocalDate.of(1985, 5, 15));
            
            assertNotNull(newUser, "A new user instance should be created on successful account creation.");
            assertTrue(newUser instanceof Doctor, "The created user instance should be of type Doctor.");
        }
    }

    @Test
    void testCreateDataAnalystInstanceSuccess() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("SUCCESS");
            
            User newUser = AccountCreationService.createUserInstance(User.Account.DATA_ANALYST, "analyst@example.com", "securePass", "Max", "Smith", "789 Data Blvd", LocalDate.of(1980, 10, 20));
            
            assertNotNull(newUser, "A new user instance should be created on successful account creation.");
            assertTrue(newUser instanceof DataAnalyst, "The created user instance should be of type Data Analyst.");
        }
    }

    @Test
    void testCreateDPOInstanceSuccess() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("SUCCESS");
            
            User newUser = AccountCreationService.createUserInstance(User.Account.DPO, "dpo@example.com", "password456", "Sam", "Taylor", "123 Privacy St", LocalDate.of(1975, 3, 5));
            
            assertNotNull(newUser, "A new user instance should be created on successful account creation.");
            assertTrue(newUser instanceof DataProtectionOfficer, "The created user instance should be of type Data Protection Officer.");
        }
    }

    @Test
    void testCreateSuperadminInstanceSuccess() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("SUCCESS");
            
            User newUser = AccountCreationService.createUserInstance(User.Account.SUPERADMIN, "superadmin@example.com", "adminPass123", "Alex", "Johnson", "1 Admin Road", LocalDate.of(1982, 7, 22));
            
            assertNotNull(newUser, "A new user instance should be created on successful account creation.");
            assertTrue(newUser instanceof Superadmin, "The created user instance should be of type Superadmin.");
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
