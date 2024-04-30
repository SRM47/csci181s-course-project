//package org.healthhaven.model;
//
//import org.healthhaven.model.User.Account;
//import org.healthhaven.server.ServerCommunicator;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.anyString;
//
//public class PatientTest extends UserTest<Patient> {
//	
//	private final String cookie = "admin_session_cookie";
//
//    @Override
//    public Patient createUser() {
//        // Ensure this matches exactly with the UserTest setup for consistency
//        return new Patient("patientID123", "example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1), cookie);
//    }
//
//    @Override
//    protected Account getExpectedAccountType() {
//        // Specifies the expected account type for instances of Patient
//        return Account.PATIENT;
//    }
//
//    @Test
//    public void testViewPatientRecord() {
//        String expectedResponse = "Patient Record Details";
//        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
//            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);
//
//            // Execute the method under test
//            String response = user.viewPatientRecord();
//            assertEquals(expectedResponse, response, "The response should contain the patient record details.");
//        }
//    }
//}
