package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDate;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class DataAnalystTest extends UserTest<DataAnalyst> {
	
	private final String cookie = "admin_session_cookie";

    @Override
    public DataAnalyst createUser() {
        // Assuming the userID is now a String and needs to be passed in the constructor
        return new DataAnalyst("1234567890","example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1), cookie);
    }

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp(); // Call the setup method from UserTest
    }

    @Override
    protected Account getExpectedAccountType() {
        return Account.DATA_ANALYST; // Specify the account type for DataAnalyst
    }

//    @Test
//    public void testPerformDataAnalysis() {
//        String expectedServerResponse = "DATA SUMMARY";
//
//        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
//            // Mock the static method call to return a predefined response
//            // Use anyString() to allow any JSON string or refine as needed
//            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedServerResponse);
//
//            // Assuming 'user' is an instance of DataAnalyst
//            DataAnalyst analyst = new DataAnalyst("userID", "email@example.com", "John", "Doe", "123 Main St", LocalDate.now(), cookie);
//
//            // Execute the method under test
//            String response = analyst.performDataAnalysis();
//
//            // Assert that the response is as expected
//            assertEquals(expectedServerResponse, response, "The server response should match the expected data summary.");
//        }
//    }


}
