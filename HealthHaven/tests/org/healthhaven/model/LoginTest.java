//package org.healthhaven.model;
//
//import org.healthhaven.server.ServerCommunicator;
//import org.json.JSONObject;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//
//public class LoginTest {
//
//    MockedStatic<ServerCommunicator> mockedServerCommunicator;
//
//    @BeforeEach
//    public void setUp() {
//        mockedServerCommunicator = Mockito.mockStatic(ServerCommunicator.class);
//    }
//    
//    @AfterEach
//    public void tearDown() {
//        mockedServerCommunicator.close(); // Ensure the mock is closed after each test
//    }
//
//    @Test
//    public void testAuthenticateUserOnDB() {
//        String expectedResponse = "User Authenticated";
//        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);
//
//        String response = Login.authenticateUserOnDB("user@example.com", "password123");
//        assertEquals(expectedResponse, response, "The response should indicate successful authentication.");
//    }
//
//    @Test
//    public void testExistingUserSession() {
//        JSONObject jsonOb = new JSONObject();
//        jsonOb.put("email", "user@example.com");
//        jsonOb.put("userID", "userID123");
//        jsonOb.put("first_name", "John");
//        jsonOb.put("last_name", "Doe");
//        jsonOb.put("address", "123 Main St");
//        jsonOb.put("dob", "1980-01-01");
//        jsonOb.put("accountType", "Doctor"); // Assume the method handles this correctly
//        jsonOb.put("cookie", "cookie");
//
//        User user = Login.existingUserSession(jsonOb);
//
//        assertNotNull(user, "User should not be null.");
//        assertTrue(user instanceof Doctor, "The created user should be an instance of Doctor.");
//        assertEquals("user@example.com", user.getEmail(), "Email should match the JSON object.");
//        assertEquals("John", user.getLegal_first_name(), "First name should match the JSON object.");
//        assertEquals("Doe", user.getLegal_last_name(), "Last name should match the JSON object.");
//        assertEquals("123 Main St", user.getAddress(), "Address should match the JSON object.");
//        assertEquals(LocalDate.parse("1980-01-01"), user.getDob(), "DOB should match the JSON object.");
//    }
//
//    @Test
//    public void testAuthenticateOTPLogin() {
//        String expectedResponse = "OTP Verified";
//        mockedServerCommunicator.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);
//
//        String response = Login.authenticateOTPLogin("user@example.com", "123456");
//        assertEquals(expectedResponse, response, "The response should indicate that the OTP was verified successfully.");
//    }
//
//    // Tests for createUserInstance functionality
//    @Test
//    public void testCreateDoctorInstance() {
//        JSONObject doctorJson = generateUserJson("123456789", "doctor@example.com", "Jane", "Doe", "456 Clinic St", "1975-05-15", "Doctor", "cookie");
//        User user = Login.existingUserSession(doctorJson);
//        assertTrue(user instanceof Doctor, "The created user should be an instance of Doctor.");
//    }
//
//    
//    @Test
//    public void testCreatePatientInstance() {
//        JSONObject patientJson = generateUserJson("987654321", "patient@example.com", "John", "Patient", "123 Health St", "1985-04-20", "Patient", "cookie");
//        User user = Login.existingUserSession(patientJson);
//        assertTrue(user instanceof Patient, "The created user should be an instance of Patient.");
//    }
//
//
//    @Test
//    public void testCreateDataAnalystInstance() {
//        JSONObject dataAnalystJson = generateUserJson("234567890", "analyst@example.com", "Danny", "Data", "456 Data St", "1982-08-15", "Data Analyst", "cookie");
//        User user = Login.existingUserSession(dataAnalystJson);
//        assertTrue(user instanceof DataAnalyst, "The created user should be an instance of Data Analyst.");
//    }
//
//
//    @Test
//    public void testCreateSuperadminInstance() {
//        JSONObject superadminJson = generateUserJson("456789012", "superadmin@example.com", "Sam", "Admin", "123 Admin Blvd", "1975-02-28", "Superadmin", "cookie");
//        User user = Login.existingUserSession(superadminJson);
//        assertTrue(user instanceof Superadmin, "The created user should be an instance of Superadmin.");
//    }
//
//
//
//    // Add similar tests for Patient, Data_Analyst, Data_Protection_Officer, and Superadmin
//    // Implement generateUserJsonResponse and any additional helper methods as needed.
//
//    private JSONObject generateUserJson(String userID, String email, String firstName, String lastName, String address, String dob, String accountType, String cookie) {
//        return new JSONObject()
//                .put("userID", userID)
//                .put("email", email)
//                .put("first_name", firstName)
//                .put("last_name", lastName)
//                .put("address", address)
//                .put("dob", dob)
//                .put("accountType", accountType)
//        		.put("cookie", cookie);
//    }
//
//}
