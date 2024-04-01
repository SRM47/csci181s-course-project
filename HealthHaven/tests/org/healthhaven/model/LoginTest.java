package org.healthhaven.model;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.MockedStatic;

class LoginTest {

//    @Test
//    void testIdentifyUserSuccess() {
//        String successfulResponse = "123,example@example.com,password123,John,Doe,123 Main St,1990-01-01,Patient";
//        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
//            // Mock the server response for successful authentication
//            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(startsWith("AUTHENTICATE_ACCOUNT")))
//                        .thenReturn(successfulResponse);
//
//            Login login = new Login();
//            User user = login.identifyUser("example@example.com", "password123");
//
//            assertNotNull(user, "User should not be null on successful authentication.");
//            assertTrue(user instanceof Patient, "Identified user should be an instance of Patient.");
//        }
//    }
    
//    @Test
//    void testIdentifyUserFailure() {
//        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
//            // Mock the server response for failed authentication
//            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(startsWith("AUTHENTICATE_ACCOUNT")))
//                        .thenReturn("FAILURE");
//
//            Login login = new Login();
//            User user = login.identifyUser("wrong@example.com", "wrongPassword");
//
//            assertNull(user, "User should be null on failed authentication.");
//        }
//    }

}

