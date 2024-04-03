package org.healthhaven.model;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

public class DoctorTest extends UserTest<Doctor> {

    @Override
    public Doctor createUser() {
        // Ensure this matches exactly with the UserTest setup for consistency
        return new Doctor("userId123", "example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }

    @Override
    protected Account getExpectedAccountType() {
        // Specifies the expected account type for instances of Doctor
        return Account.DOCTOR;
    }

    @Test
    public void testUpdatePatientRecordOnDB() {
        String expectedResponse = "Record Updated";
        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            // Example patient ID, height, and weight
            String response = user.updatePatientRecordOnDB(12345L, "180cm", "75kg");
            assertEquals(expectedResponse, response, "The response should indicate that the record was updated.");
        }
    }

    @Test
    public void testViewPatientRecord() {
        String expectedResponse = "Patient Record Details";
        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            // Example patient ID
            String response = user.viewPatientRecord(12345L);
            assertEquals(expectedResponse, response, "The response should contain the patient record details.");
        }
    }

    @Test
    public void testAuthorizeAccountCreation() {
        String expectedResponse = "Account Creation Authorized";
        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            // New patient email and dob
            String response = user.authorizeAccountCreation("newpatient@example.com", LocalDate.of(1990, 1, 1));
            assertEquals(expectedResponse, response, "The response should indicate that the account creation was authorized.");
        }
    }
}
