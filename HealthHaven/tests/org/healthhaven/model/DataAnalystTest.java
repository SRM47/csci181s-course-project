package org.healthhaven.model;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataAnalystTest extends UserTest<DataAnalyst> {

    @Override
    public DataAnalyst createUser() {
        return new DataAnalyst(1234567890,"example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }
    
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }
    
    @Override
    protected Account getExpectedAccountType() {
        return Account.DATA_ANALYST;
    }
    
    @Test
    public void testGenerateUserID() {
        user.generateUserID(); //use user since setUp
        long userID = user.getUserID();
        assertTrue(String.valueOf(userID).startsWith("3"), "Data Analyst userID should start with .");
    }
    
    @Test
    public void testPerformDataAnalysis() {
        // Assuming the server responds with "DATA SUMMARY" upon successful data request
        String expectedServerResponse = "DATA SUMMARY";
        
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            // Mock the static method call to return a specific response for the expected request
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer("REQUEST_PATIENT_DATA_SUMMARY 300"))
                    .thenReturn(expectedServerResponse);

            // Call the method under test
            String response = user.performDataAnalysis();

            // Assert that the method returns the expected response
            assertEquals(expectedServerResponse, response, "The server response should match the expected data summary.");
        }
    }

}