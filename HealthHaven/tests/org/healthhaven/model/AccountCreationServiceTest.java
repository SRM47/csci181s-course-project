package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.healthhaven.server.ServerCommunicator;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class AccountCreationServiceTest {

    @Test
    void testCreateUser() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Setup the expected server response and mock the communicateWithServer method
            String expectedResponse = "Success";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            // Test createUser with a sample input
            String result = AccountCreationService.createUser("DOCTOR", "test@example.com", "password123", "John", "Doe", "123 Main St", LocalDate.of(1990, 1, 1));

            // Assert that the result is as expected
            assertEquals(expectedResponse, result, "The response from server should be handled correctly by createUser");
        }
    }

    @Test
    void testInsertNewAccountIntoDB() {
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            String expectedResponse = "Database Entry Created";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            User.Account userType = User.Account.DOCTOR;
            String email = "test@example.com";
            String password = "password123";
            String firstName = "John";
            String lastName = "Doe";
            String address = "123 Main St";
            LocalDate dob = LocalDate.of(1990, 1, 1);

            String result = AccountCreationService.insertNewAccountIntoDB(userType, email, password, firstName, lastName, address, dob);

            // Verify that the JSON sent to the server is as expected
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            mockedStatic.verify(() -> ServerCommunicator.communicateWithServer(captor.capture()));
            JSONObject json = new JSONObject(captor.getValue());

            assertEquals(email, json.getString("email"), "Email should match input");
            assertEquals(password, json.getString("password"), "Password should match input");
            assertEquals(firstName, json.getString("first_name"), "First name should match input");
            assertEquals(lastName, json.getString("last_name"), "Last name should match input");
            assertEquals(address, json.getString("address"), "Address should match input");
            assertEquals(dob.toString(), json.getString("dob"), "DOB should match input");
            assertEquals(userType.getAccountName(), json.getString("accountType"), "Account type should match input");

            assertEquals(expectedResponse, result, "The response from server should be returned by the method");
        }
    }
}
