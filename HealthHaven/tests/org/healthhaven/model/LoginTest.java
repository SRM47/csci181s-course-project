package org.healthhaven.model;

import org.healthhaven.server.ServerCommunicator;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class LoginTest {

    MockedStatic<ServerCommunicator> mockedServerCommunicator;

    @BeforeEach
    public void setUp() {
        mockedServerCommunicator = Mockito.mockStatic(ServerCommunicator.class);
    }
    
    @AfterEach
    public void tearDown() {
        mockedServerCommunicator.close(); // Ensure the mock is closed after each test
    }

    @Test
    public void testAuthenticateUserOnDB() {
        String expectedResponse = "User Authenticated";
        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

        String response = Login.authenticateUserOnDB("user@example.com", "password123");
        assertEquals(expectedResponse, response, "The response should indicate successful authentication.");
    }

    @Test
    public void testExistingUserSession() {
        JSONObject jsonOb = new JSONObject();
        jsonOb.put("email", "user@example.com");
        jsonOb.put("userID", "userID123");
        jsonOb.put("first_name", "John");
        jsonOb.put("last_name", "Doe");
        jsonOb.put("address", "123 Main St");
        jsonOb.put("dob", "1980-01-01");
        jsonOb.put("accountType", "Doctor");

        User user = Login.existingUserSession(jsonOb);

        assertEquals("user@example.com", user.getEmail(), "Email should match the JSON object.");
        // Note: Real assertion might adjust based on actual return type or user instance checks.
    }

    @Test
    public void testAuthenticateOTPLogin() {
        String expectedResponse = "OTP Verified";
        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

        String response = Login.authenticateOTPLogin("user@example.com", "123456");
        assertEquals(expectedResponse, response, "The response should indicate that the OTP was verified successfully.");
    }

    // Tests for createUserInstance functionality
    @Test
    public void testCreateDoctorInstance() {
        String doctorResponse = generateUserJsonResponse("Doctor", "doc123", "doctor@example.com", "Jane", "Doe", "456 Clinic St", "1975-05-15");
        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(doctorResponse);

        User user = Login.existingUserSession(new JSONObject(doctorResponse));
        assertTrue(user instanceof Doctor, "The created user should be an instance of Doctor.");
    }
    
    @Test
    public void testCreatePatientInstance() {
        String patientResponse = generateUserJsonResponse("Patient", "patient123", "patient@example.com", "John", "Patient", "123 Health St", "1985-04-20");
        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(patientResponse);

        User user = Login.existingUserSession(new JSONObject(patientResponse));
        assertTrue(user instanceof Patient, "The created user should be an instance of Patient.");
    }

    @Test
    public void testCreateDataAnalystInstance() {
        String dataAnalystResponse = generateUserJsonResponse("Data_Analyst", "analyst123", "analyst@example.com", "Danny", "Data", "456 Data St", "1982-08-15");
        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(dataAnalystResponse);

        User user = Login.existingUserSession(new JSONObject(dataAnalystResponse));
        assertTrue(user instanceof DataAnalyst, "The created user should be an instance of Data Analyst.");
    }

    @Test
    public void testCreateDPOInstance() {
        String dpoResponse = generateUserJsonResponse("Data_Protection_Officer", "dpo123", "dpo@example.com", "Diana", "Privacy", "789 Privacy Ln", "1978-12-05");
        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(dpoResponse);

        User user = Login.existingUserSession(new JSONObject(dpoResponse));
        assertTrue(user instanceof DataProtectionOfficer, "The created user should be an instance of Data Protection Officer.");
    }

    @Test
    public void testCreateSuperadminInstance() {
        String superadminResponse = generateUserJsonResponse("Superadmin", "admin123", "superadmin@example.com", "Sam", "Admin", "123 Admin Blvd", "1975-02-28");
        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(superadminResponse);

        User user = Login.existingUserSession(new JSONObject(superadminResponse));
        assertTrue(user instanceof Superadmin, "The created user should be an instance of Superadmin.");
    }


    // Add similar tests for Patient, Data_Analyst, Data_Protection_Officer, and Superadmin
    // Implement generateUserJsonResponse and any additional helper methods as needed.

    private String generateUserJsonResponse(String accountType, String userID, String email, String firstName, String lastName, String address, String dob) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("accountType", accountType);
        jsonResponse.put("userID", userID);
        jsonResponse.put("email", email);
        jsonResponse.put("first_name", firstName);
        jsonResponse.put("last_name", lastName);
        jsonResponse.put("address", address);
        jsonResponse.put("dob", dob);
        return jsonResponse.toString();
    }
}
