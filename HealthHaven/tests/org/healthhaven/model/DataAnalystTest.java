package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

public class DataAnalystTest extends UserTest<DataAnalyst> {

    private final String cookie = "admin_session_cookie";

    @Override
    public DataAnalyst createUser() {
        return new DataAnalyst("1234567890", "example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1), cookie);
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
        String expectedServerResponse = "{\"data\": \"Processed data summary\"}";
        LocalDate testDate = LocalDate.of(2023, 4, 15);
        
        try (MockedStatic<ServerCommunicator> mockedStatic = mockStatic(ServerCommunicator.class)) {
            mockedStatic.when(() -> ServerCommunicator.communicateWithServer(anyString())).thenReturn(expectedServerResponse);

            // Test various combinations of the boolean flags and assert the expected behavior
            checkAnalysis(true, false, ">=", testDate);
            checkAnalysis(false, true, "<=", testDate);
            checkAnalysis(false, false, "<=", testDate);
        }
    }

    private void checkAnalysis(boolean after, boolean before, String expectedWhen, LocalDate date) {
        JSONObject response = user.performDataAnalysis(after, before, date);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
//        verify(ServerCommunicator).communicateWithServer(captor.capture());
        String capturedJson = captor.getValue();

        JSONObject jsonRequest = new JSONObject(capturedJson);
        assertEquals("REQUEST_PATIENT_DATA", jsonRequest.getString("request"));
        assertEquals(user.getUserID(), jsonRequest.getString("callerId"));
        assertEquals(user.getCookie(), jsonRequest.getString("cookie"));
        assertEquals(expectedWhen, jsonRequest.getString("when"));
        assertEquals(date.toString(), jsonRequest.getString("date"));

        assertEquals("Processed data summary", response.getJSONObject("data").getString("data"), "The server response should match the expected data summary.");
    }
}
