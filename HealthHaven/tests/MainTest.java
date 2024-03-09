import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

class MainTest {

    /**
     * Testing the valid existing user.
     */
    @Test
    void testAuthenticateValidExistingUser() {
        try (MockedStatic<ServerCommunicator> mocked = Mockito.mockStatic(ServerCommunicator.class)) {
            String expectedResponse = "SUCCESS";
            mocked.when(() -> ServerCommunicator.communicateWithAccountServer(Mockito.anyString())).thenReturn(expectedResponse);

            // Call the method under test directly in Main.java
            String response = Main.authenticateExistingUser("test@example.com", "password");

            // Assert the expected server response is returned
            Assertions.assertEquals(expectedResponse, response, "The server response should match the expected value.");

            // Verify the correct message format is passed to the server communicator
            mocked.verify(() -> ServerCommunicator.communicateWithAccountServer(Mockito.contains("AUTHENTICATE_ACCOUNT test@example.com password")));
        }
    }

    /**
     * Testing the invalid existing user.
     */
    @Test
    void testAuthenticateInvalidExistingUser() {
        try (MockedStatic<ServerCommunicator> mocked = Mockito.mockStatic(ServerCommunicator.class)) {
            String expectedResponse = "FAILURE";
            mocked.when(() -> ServerCommunicator.communicateWithAccountServer(Mockito.anyString())).thenReturn(expectedResponse);

            // Call the method under test directly in Main.java
            String response = Main.authenticateExistingUser("test@example.com", "password");

            // Assert the expected server response is returned
            Assertions.assertEquals(expectedResponse, response, "The server response should match the expected value.");

            // Verify the correct message format is passed to the server communicator
            mocked.verify(() -> ServerCommunicator.communicateWithAccountServer(Mockito.contains("AUTHENTICATE_ACCOUNT test@example.com password")));
        }
    }
}
