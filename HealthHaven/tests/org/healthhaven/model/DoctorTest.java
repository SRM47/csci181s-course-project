package org.healthhaven.model;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
    
//
//    @Test
//    public void testUpdatePatientRecordOnDB() {
//        // Assuming the server responds with "UPDATE SUCCESS" upon successful record update
//        String expectedServerResponse = "UPDATE SUCCESS";
//        when(user.mockableCommunicateWithMedicalServer(anyString())).thenReturn(expectedServerResponse);
//
//        String response = user.updatePatientRecordOnDB(12345L, "180", "75"); // Example patient ID, height, and weight
//        assertEquals(expectedServerResponse, response, "The server response should indicate success.");
//    }
//
//    @Test
//    public void testViewAndUpdatePatientRecordInteraction() {
//        // This test simulates the interaction flow for viewing and deciding to update a patient record
//
//        // Mock the server response for viewing a patient record
//        String viewResponse = "Patient Record Details";
//        when(user.mockableCommunicateWithMedicalServer(startsWith("VIEW"))).thenReturn(viewResponse);
//
//        // Mock the server response for updating a patient record
//        String updateResponse = "UPDATE SUCCESS";
//        when(user.mockableCommunicateWithMedicalServer(startsWith("UPDATE"))).thenReturn(updateResponse);
//
//        // Simulate user input for the interaction
//        // For example, entering patient ID 12345, choosing to update, and providing new details
//        String simulatedUserInput = "12345\n1\n180\n75\n";
//        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
//        Scanner scanner = new Scanner(System.in);
//        
//        user.viewAndUpdatePatientRecord(scanner); // Execute the method with mocked user input
//
//        // Verify that the "view" operation was invoked
//        verify(user, times(1)).viewPatientRecord(12345L);
//
//        // Since we're simulating choosing to update the record, verify the "update" operation was also invoked
//        verify(user, times(1)).updatePatientRecordOnDB(eq(12345L), anyString(), anyString());
//
//        // Reset System.in to its original state
//        System.setIn(System.in);
//
//        scanner.close();
//    }
//
//    // Additional setup for mocking the communicateWithMedicalServer method if not using static methods
//    // For demonstration purposes only; in actual implementation, consider design changes for better testability
//    private void setupMockForCommunicateWithMedicalServer() {
//        // Implement mock setup logic here if applicable
//    }
}