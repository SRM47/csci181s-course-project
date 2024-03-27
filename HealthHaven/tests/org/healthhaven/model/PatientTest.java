package org.healthhaven.model;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import org.healthhaven.model.User.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;

public class PatientTest extends UserTest<Patient> {

    @Override
    public Patient createUser() {
        return new Patient(1234567890,"example@example.com","password123", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }
    
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }
    
    @Override
    protected Account getExpectedAccountType() {
        return Account.PATIENT; // Return the expected Account type for Patient
    }
    
    @Test
    public void testGenerateUserID() {
        user.generateUserID(); //use user since setUp
        long userID = user.getUserID();
        assertTrue(String.valueOf(userID).startsWith("2"), "Patient userID should start with 2.");
    }

    @Test
    public void testViewPatientRecord() {
        Patient patient = createUser();
        // Set a specific userID for the patient, if your user class allows it. Otherwise, ensure it's set in the constructor.
        // patient.setUserID(12345L); // Only if your design allows setting the userID outside the constructor

        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Mock the static method to return a specific response for the expected request
            // Assuming getUserID returns a fixed value or is mocked to return a fixed value
            String expectedMessage = "VIEW " + patient.getUserID();
            String expectedResponse = "Mocked patient record content";
            mockedStatic.when(() -> ServerCommunicator.communicateWithMedicalServer(expectedMessage))
                        .thenReturn(expectedResponse);
            

            // Call the method under test
            String actualResponse = patient.viewPatientRecord();

            // Verify the response matches the expected mock response
            assertEquals(expectedResponse, actualResponse);
        }
    }
}