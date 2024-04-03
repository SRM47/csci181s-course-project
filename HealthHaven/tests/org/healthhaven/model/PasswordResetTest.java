package org.healthhaven.model;

import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class PasswordResetTest {

    @Test
    public void testVerifyEmail() {
        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            String expectedResponse = "Verification Email Sent";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            String response = PasswordReset.verifyEmail("user@example.com");
            assertEquals(expectedResponse, response, "Should return the expected response from server.");
        }
    }

    @Test
    public void testConfirmOTP() {
        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            String expectedResponse = "OTP Confirmed";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            String response = PasswordReset.confirmOTP("user@example.com", "123456");
            assertEquals(expectedResponse, response, "Should confirm the OTP correctly.");
        }
    }

    @Test
    public void testUpdatePassword() {
        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            String expectedResponse = "Password Updated Successfully";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedResponse);

            String response = PasswordReset.updatePassword("user@example.com", "newPassword");
            assertEquals(expectedResponse, response, "Should update the password successfully.");
        }
    }
}
