package org.healthhaven.db.models;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountDAOTest {
	// Not testing private methods or methods that require digging into existing user in db

//    @Test
//    void testCreateTemporaryUser() throws SQLException {
//        // Mock the SQL objects
//        Connection mockConn = mock(Connection.class);
//        PreparedStatement mockStmt = mock(PreparedStatement.class);
//        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
//        when(mockStmt.executeUpdate()).thenReturn(1); // Simulate 1 row inserted
//
//        // Invoke the method under test
//        JSONObject result = AccountDAO.createTemporaryUser(mockConn, "userId123", "email@example.com", "password", "2000-01-01", "USER");
//
//        // Verify and assert the result
//        assertEquals("SUCCESS", result.getString("result"));
//        verify(mockConn, Mockito.times(4)).prepareStatement(anyString());
//        verify(mockStmt, Mockito.times(4)).executeUpdate();
//    }


//    @Test
//    void testAuthenticateUserExisting() throws SQLException {
//        Connection conn = Mockito.mock(Connection.class);
//        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
//        ResultSet rs = Mockito.mock(ResultSet.class);
//
//        // Setting up mocks
//        when(conn.prepareStatement(anyString())).thenReturn(stmt);
//        when(stmt.executeQuery()).thenReturn(rs);
//        when(rs.next()).thenReturn(true); // User is found
//        when(rs.getString("password")).thenReturn("hashedPassword");
//        when(rs.getBoolean("reset")).thenReturn(true); // Reset is required
//        when(rs.getString("totp_key")).thenReturn("dummyTotpKey"); // Needed for TOTP
//
//        JSONObject response = AccountDAO.authenticateUser(conn, "test@example.com", "hashedPassword");
//
//        assertEquals("SUCCESS", response.getString("result"));
//
//        // Safely fetch "reason" with an optional default value to avoid JSONException
//        String reason = response.optString("reason", "This should take an error");
//
//        assertEquals("EXISTING", reason); // Verifying that the reason is "EXISTING"
//
//        verify(conn).prepareStatement(anyString()); // Verify SQL query execution
//        verify(stmt).executeQuery(); // Ensure query execution is verified
//        verify(rs).getString("password"); // Confirm password retrieval
//        verify(rs).getBoolean("reset"); // Check reset status
//    }

    @Test
    void accountExistsByEmailAccountExists() throws SQLException {
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true); // Simulate finding an account
        when(rs.getInt(1)).thenReturn(1); // Simulate count > 0

        boolean exists = AccountDAO.accountExistsByEmail(conn, "test@example.com");

        assertTrue(exists);
        Mockito.verify(stmt).setString(1, "test@example.com");
    }
//
//    @Test
//    void viewUserInformationSuccess() throws SQLException {
//        Connection conn = Mockito.mock(Connection.class);
//        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
//        ResultSet rs = Mockito.mock(ResultSet.class);
//
//        when(conn.prepareStatement(anyString())).thenReturn(stmt);
//        when(stmt.executeQuery()).thenReturn(rs);
//        when(rs.next()).thenReturn(true, false); // Return true first time, then false
//        when(rs.getString("Timestamp")).thenReturn("2022-01-01 10:00:00");
//        when(rs.getFloat("Height")).thenReturn(175.0f);
//        when(rs.getFloat("Weight")).thenReturn(70.0f);
//
//        JSONObject response = AccountDAO.viewUserInformation(conn, "docId123", "userId123");
//
//        assertEquals("SUCCESS", response.getString("result"));
//        JSONArray records = response.getJSONArray("records");
//        assertNotNull(records);
//        assertEquals(1, records.length());
//        JSONObject record = records.getJSONObject(0);
//        assertEquals("2022-01-01 10:00:00", record.getString("Timestamp"));
//        assertEquals(175.0f, record.getFloat("Height"), 0.001);
//        assertEquals(70.0f, record.getFloat("Weight"), 0.001);
//    }
//    
    @Test
    void getDataAverageSuccess() throws SQLException {
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true); // Simulate successful query execution
        when(rs.getFloat("AvgHeight")).thenReturn(175.0f);
        when(rs.getFloat("AvgWeight")).thenReturn(70.0f);

        JSONObject response = AccountDAO.getDataAverage(conn);

        assertEquals("SUCCESS", response.getString("result"));
        assertEquals(175.0f, response.getFloat("averageHeight"));
        assertEquals(70.0f, response.getFloat("averageWeight"));
    }
    @Test
    void newMedicalInformationSuccess() throws SQLException {
        // Mocking necessary JDBC components
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        // Setup for the connection and statement
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        // ResultSet setup for checking existence of both patient and doctor
        when(rs.next()).thenReturn(true, true, false); // Returns true twice for patient and doctor, then false to simulate end of result set
        when(rs.getInt(1)).thenReturn(1);  // Assume accountExistsById method checks the count, and that the users exist

        // Simulate successful insert operation
        when(stmt.executeUpdate()).thenReturn(1);

        // Actual call to the method under test
        JSONObject response = AccountDAO.newMedicalInformation(conn, "userId123", "docId123", 180, 75, "2022-01-01T10:00:00Z");

        // Assertions to verify the correct responses and interactions
        assertEquals("SUCCESS", response.getString("result"), "The result should indicate success.");
        Mockito.verify(stmt).setString(2, "userId123"); // Verifies patientId was set correctly
        Mockito.verify(stmt).setString(3, "docId123"); // Verifies doctorId was set correctly
        Mockito.verify(stmt, atLeastOnce()).executeQuery(); // Verify that a query was executed
    }
    
    @Test
    public void testGenerateAndUpdateNewUserCookie() throws SQLException {
        // Mock the connection and prepared statement
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPstmt = mock(PreparedStatement.class);

        // Configure the mock to return our prepared statement
        when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPstmt);

        // Mock the executeUpdate and commit behaviors
        when(mockPstmt.executeUpdate()).thenReturn(1);
        doNothing().when(mockConn).commit();

        // Generate the cookie and update the database
        String userId = "user123";
        String cookie = AccountDAO.generateAndUpdateNewUserCookie(mockConn, userId);

        // Verify that methods are called correctly
        verify(mockConn).prepareStatement(anyString());
        verify(mockPstmt).setString(1, cookie);
        verify(mockPstmt).setString(2, userId);
        verify(mockPstmt).executeUpdate();
        verify(mockConn).commit();

        // Assert that a cookie was returned
        assertNotNull(cookie);

        // Check cookie format (optional)
        assertNotEquals("", cookie); // Simple check to ensure cookie is not empty
    }

    @Test
    public void testGenerateAndUpdateNewUserCookie_WithSQLException() throws SQLException {
        // Mock the connection and prepared statement
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPstmt = mock(PreparedStatement.class);

        // Configure the mock to throw an SQLException
        when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenThrow(new SQLException("Database error"));

        // Attempt to generate the cookie and handle the exception
        String userId = "user123";
        String cookie = AccountDAO.generateAndUpdateNewUserCookie(mockConn, userId);

        // Verify that rollback is called
        verify(mockConn).rollback();

        // Assert that null is returned due to the exception
        assertNull(cookie);
    }
    
    @Test
    public void testLogoutUser_Successful() throws SQLException {
        // Mock the connection and prepared statement
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPstmt = mock(PreparedStatement.class);

        // Setup mock behavior
        when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenReturn(1);
        doNothing().when(mockConn).commit();

        // Perform the logout
        String userId = "user456";
        JSONObject response = AccountDAO.logoutUser(mockConn, userId);

        // Verify interactions
        verify(mockConn).prepareStatement(anyString());
        verify(mockPstmt).setString(1, userId);
        verify(mockPstmt).executeUpdate();
        verify(mockConn).commit();

        // Check the response
        assertEquals("SUCCESS", response.getString("result"));
        assertEquals("", response.getString("reason")); // Assuming no reason needed on success
    }

    @Test
    public void testLogoutUser_FailureDueToSQLException() throws SQLException {
        // Mock the connection and prepared statement
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPstmt = mock(PreparedStatement.class);

        // Setup mock to throw an SQLException
        when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenThrow(new SQLException("Failed to execute update"));

        // Perform the logout
        String userId = "user456";
        JSONObject response = AccountDAO.logoutUser(mockConn, userId);

        // Verify rollback is called
        verify(mockConn).rollback();

        // Check the response
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("SQL error failed to logout", response.getString("reason"));
    }

    


}
