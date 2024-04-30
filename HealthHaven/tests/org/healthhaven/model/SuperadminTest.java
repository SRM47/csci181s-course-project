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
//public class SuperadminTest extends UserTest<Superadmin> {
//	
//	private final String cookie = "admin_session_cookie";
//
//    @Override
//    public Superadmin createUser() {
//        // Aligns with the UserTest setup for consistency
//        return new Superadmin("superadminID123", "example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1), cookie);
//    }
//
//    @Override
//    protected Account getExpectedAccountType() {
//        // Specifies the expected account type for instances of Superadmin
//        return Account.SUPERADMIN;
//    }
//
//    @Test
//    public void testViewAccountList() {
//        String expectedResponse = "Account List Details";
//        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
//            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);
//
//            // Execute the method under test
//            String response = user.viewAccountList();
//            assertEquals(expectedResponse, response, "The response should contain the account list details.");
//        }
//    }
//
//    @Test
//    public void testAuthorizeAccountCreation() {
//        String expectedResponse = "Account Creation Authorized";
//        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
//            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);
//
//            // New account details
//            String response = user.authorizeAccountCreation("newaccount@example.com", Account.PATIENT, LocalDate.of(1990, 1, 1));
//            assertEquals(expectedResponse, response, "The response should indicate that the account creation was authorized.");
//        }
//    }
//}
