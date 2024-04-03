package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDate;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class DataAnalystTest extends UserTest<DataAnalyst> {

    @Override
    public DataAnalyst createUser() {
        // Assuming the userID is now a String and needs to be passed in the constructor
        return new DataAnalyst("1234567890","example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
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

    @Test
    public void testPerformDataAnalysis() {
        // Assume the server responds with a specific string upon a data analysis request
        String expectedServerResponse = "DATA SUMMARY";

        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Mock the static method call to return a predefined response
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer("REQUEST_PATIENT_DATA_SUMMARY ")).thenReturn(expectedServerResponse);

            // Execute the method under test
            String response = user.performDataAnalysis();

            // Assert that the response is as expected
            assertEquals(expectedServerResponse, response, "The server response should match the expected data summary.");
        }
    }

}
