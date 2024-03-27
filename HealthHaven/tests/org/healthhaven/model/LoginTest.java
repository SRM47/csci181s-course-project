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

    @Test
    public void testCreateUserInstancePatient() {
        User patient = Login.createUserInstance("Patient", 2L, "patient@example.com", "password", "Pat", "Ient", "456 Patient Rd", LocalDate.of(1990, 2, 2));
        assertNotNull(patient, "createUserInstance should return a non-null Patient instance.");
        assertTrue(patient instanceof Patient, "createUserInstance should return an instance of Patient when 'Patient' account type is specified.");
    }

    @Test
    public void testCreateUserInstanceDataAnalyst() {
        User dataAnalyst = Login.createUserInstance("Data_Analyst", 3L, "analyst@example.com", "password", "Data", "Analyst", "789 Data St", LocalDate.of(1985, 3, 3));
        assertNotNull(dataAnalyst, "createUserInstance should return a non-null DataAnalyst instance.");
        assertTrue(dataAnalyst instanceof DataAnalyst, "createUserInstance should return an instance of DataAnalyst when 'Data_Analyst' account type is specified.");
    }

    @Test
    public void testCreateUserInstanceDataProtectionOfficer() {
        User dpo = Login.createUserInstance("Data_Protection_Officer", 4L, "dpo@example.com", "password", "Data", "Protection", "101 Protection Ln", LocalDate.of(1995, 4, 4));
        assertNotNull(dpo, "createUserInstance should return a non-null DataProtectionOfficer instance.");
        assertTrue(dpo instanceof DataProtectionOfficer, "createUserInstance should return an instance of DataProtectionOfficer when 'Data_Protection_Officer' account type is specified.");
    }

    @Test
    public void testCreateUserInstanceSuperadmin() {
        User superadmin = Login.createUserInstance("Superadmin", 5L, "superadmin@example.com", "password", "Super", "Admin", "1 Admin Way", LocalDate.of(2000, 5, 5));
        assertNotNull(superadmin, "createUserInstance should return a non-null Superadmin instance.");
        assertTrue(superadmin instanceof Superadmin, "createUserInstance should return an instance of Superadmin when 'Superadmin' account type is specified.");
    }
}
