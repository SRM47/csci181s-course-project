package org.healthhaven.model;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Random;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.ArgumentMatchers;

public class DoctorTest extends UserTest<Doctor> {
    
    @Override
    public Doctor createUser() {
        return new Doctor("example@example.com", "password123", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }
    
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }
    
    @Override
    protected Account getExpectedAccountType() {
        return Account.DOCTOR;
    }

    @Test
    public void testGenerateUserID() {
        // Ensuring that the generated userID starts with '1', indicating a Doctor's userID
        user.generateUserID();
        long userID = user.getUserID();
        assertTrue(Long.toString(userID).startsWith("1"), "Doctor userID should start with 1.");
    }
    
    @Test
    public void testViewPatientRecord() {
        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            // Mock the static method call
            mockedStatic.when(() -> ServerCommunicator.communicateWithMedicalServer("VIEW 12345"))
                    .thenReturn("Mocked patient record response");

            // Call the method under test
            String response = user.viewPatientRecord(12345);

            // Assert the expected behavior or outcome
            assertEquals("Mocked patient record response", response, "The response should match the mocked one.");
        }
    }
    
    @Test
    public void testUpdatePatientRecordOnDB() {
        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            // Mock the static method to return a specific response for any string that starts with "UPDATE_RECORD"
            mockedStatic.when(() -> ServerCommunicator.communicateWithMedicalServer(ArgumentMatchers.startsWith("UPDATE_RECORD")))
                    .thenReturn("Mocked update record response");

            // Prepare the test inputs
            long testUserID = 12345L;
            String testHeight = "180";
            String testWeight = "75";

            // Call the method under test with the prepared inputs
            String response = user.updatePatientRecordOnDB(testUserID, testHeight, testWeight);

            // Assert that the response is as expected
            assertEquals("Mocked update record response", response, "The response should match the mocked one.");

            // Additionally, you might want to verify that the method was called with an argument that matches the expected format.
            // However, this verification is somewhat more complex due to the inclusion of a timestamp in the message,
            // which we cannot predict exactly in the test. If you had a way to mock the timestamp or if the message format
            // was simpler, you could add a verification step here.
        }
    }
    
}