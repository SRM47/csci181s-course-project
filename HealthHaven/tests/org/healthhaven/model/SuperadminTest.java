package org.healthhaven.model;
import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class SuperadminTest extends UserTest<Superadmin> {

    @Override
    public Superadmin createUser() {
        // Create a Superadmin instance with known attributes for testing.
        return new Superadmin("example@example.com", "password123", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        // Additional setup if needed.
    }
    
    @Override
    protected Account getExpectedAccountType() {
        return Account.SUPERADMIN;
    }

    @Test
    void testGenerateUserID() {
        user.generateUserID();
        long userID = user.getUserID();
        assertTrue(String.valueOf(userID).startsWith("5"), "Superadmin userID should start with 5.");
    }
    
    @Test
    public void testViewAccountList() {
        Superadmin superadmin = createUser();

        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Mock the static method call to return a specific response for the "VIEW ACCOUNT" message
            String expectedResponse = "Mocked list of accounts";
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer("VIEW ACCOUNT"))
                        .thenReturn(expectedResponse);

            // Call the method under test
            String actualResponse = superadmin.viewAccountList();

            // Verify the response matches the expected mock response
            assertEquals(expectedResponse, actualResponse, "The response from viewAccountList should match the mocked response.");
        }
    }
}
