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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountDAOTest {
	// Not testing private methods or methods that require digging into existing user in db

    @Test
    void testCreateTemporaryUser() throws SQLException {
        // Mock the SQL objects
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeUpdate()).thenReturn(1); // Simulate 1 row inserted

        // Invoke the method under test
        JSONObject result = AccountDAO.createTemporaryUser(mockConn, "userId123", "email@example.com", "password", "2000-01-01", "USER");

        // Verify and assert the result
        assertEquals("SUCCESS", result.getString("result"));
        verify(mockConn, Mockito.times(3)).prepareStatement(anyString());
        verify(mockStmt, Mockito.times(3)).executeUpdate();
    }
    
//    @Test
//    void testUpdateUserInformationSuccessfully() throws SQLException {
//        Connection conn = Mockito.mock(Connection.class);
//        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
//        ResultSet rs = Mockito.mock(ResultSet.class); // ResultSet for accountExistsById
//
//        when(conn.prepareStatement(anyString())).thenReturn(stmt);
//        when(stmt.executeQuery()).thenReturn(rs);
//
//        // Simulating an account exists
//        when(rs.next()).thenReturn(true); // Ensure this matches the logic in accountExistsById
//
//        JSONObject response = AccountDAO.updateUserInformation(conn, "New Address", "testUserId");
//
//        System.out.println(response);
//        assertEquals("SUCCESS", response.getString("result"));
//        Mockito.verify(conn).prepareStatement(anyString()); // Verify prepareStatement was called
//        Mockito.verify(stmt, Mockito.times(1)).executeUpdate(); // Verify executeUpdate was called once
//    }




    @Test
    void testAuthenticateUser() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true); // Simulate found user
        when(rs.getString("password")).thenReturn("hashedPassword");
        when(rs.getBoolean("reset")).thenReturn(false);

        JSONObject response = AccountDAO.authenticateUser(conn, "test@example.com", "hashedPassword");

        assertEquals("SUCCESS", response.getString("result"));
        verify(conn).prepareStatement(anyString());
        verify(stmt).executeQuery();
        verify(rs).getString("password");
    }

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

    @Test
    void viewUserInformationSuccess() throws SQLException {
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false); // Return true first time, then false
        when(rs.getString("Timestamp")).thenReturn("2022-01-01 10:00:00");
        when(rs.getFloat("Height")).thenReturn(175.0f);
        when(rs.getFloat("Weight")).thenReturn(70.0f);

        JSONObject response = AccountDAO.viewUserInformation(conn, "docId123", "userId123");

        assertEquals("SUCCESS", response.getString("result"));
        JSONArray records = response.getJSONArray("records");
        assertNotNull(records);
        assertEquals(1, records.length());
    }
    
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
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1); // Simulate successful insert

        JSONObject response = AccountDAO.newMedicalInformation(conn, "userId123", "docId123", "180", "75", "2022-01-01 10:00:00");

        assertEquals("SUCCESS", response.getString("result"));
        Mockito.verify(stmt).setString(2, "userId123"); // Verifies patientId was set correctly
        Mockito.verify(stmt).setString(3, "docId123"); // Verifies doctorId was set correctly
    }
    
//    @Test
//    void updatePasswordAccountDoesNotExist() throws SQLException {
//        Connection conn = Mockito.mock(Connection.class);
//        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
//        ResultSet rs = Mockito.mock(ResultSet.class);
//
//        when(conn.prepareStatement(anyString())).thenReturn(stmt);
//        when(stmt.executeQuery()).thenReturn(rs);
//        when(rs.next()).thenReturn(false); // Simulate account does not exist
//
//        JSONObject response = AccountDAO.updatePassword(conn, "newPassword", "email@example.com");
//        
//        System.out.println(response);
//        assertEquals("FAILURE", response.getString("result"));
//        assertEquals("Account does not exist", response.getString("reason"));
//    }

//    @Test
//    void updatePasswordSuccess() throws SQLException {
//        Connection conn = Mockito.mock(Connection.class);
//        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
//        ResultSet rs = Mockito.mock(ResultSet.class);
//
//        when(conn.prepareStatement(anyString())).thenReturn(stmt);
//        when(stmt.executeQuery()).thenReturn(rs);
//        when(rs.next()).thenReturn(true); // Simulate account exists
//        when(rs.getString("userid")).thenReturn("userId123"); // Return userId for accountExistsByEmail
//        when(stmt.executeUpdate()).thenReturn(1); // Simulate successful password update
//
//        JSONObject response = AccountDAO.updatePassword(conn, "newPassword", "email@example.com");
//
//        System.out.println(response);
//        assertEquals("SUCCESS", response.getString("result"));
//    }

//    @Test
//    void updateTemporaryUserAfterFirstLoginSuccess() throws Exception {
//        Connection mockConn = Mockito.mock(Connection.class);
//        PreparedStatement mockStmt = Mockito.mock(PreparedStatement.class);
//        ResultSet mockRs = Mockito.mock(ResultSet.class);
//
//        // Mock getUserIdFromEmail to simulate finding a user ID
//        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
//        when(mockStmt.executeQuery()).thenReturn(mockRs);
//        when(mockRs.next()).thenReturn(true); // Simulate user found for getUserIdFromEmail and verifyDOB
//        when(mockRs.getString("userid")).thenReturn("testUserId"); // Return value for getUserIdFromEmail
//        when(mockRs.getDate("dob")).thenReturn(java.sql.Date.valueOf("1990-01-01")); // Simulate DOB match for verifyDOB
//
//        // Mock updateUserTable and updateAuthenticationTable to simulate successful updates
//        when(mockStmt.executeUpdate()).thenReturn(1); // Simulate successful update
//
//        // Call the method under test
//        JSONObject response = AccountDAO.updateTemporaryUserAfterFirstLogin(mockConn, "John", "Doe", "1990-01-01", "123 Street", "email@example.com", "newPassword", "USER");
//
//        System.out.println(response);
//        // Assertions
//        assertEquals("SUCCESS", response.getString("result"));
//
//        // Verify that all prepared statements are executed
//        verify(mockStmt, atLeastOnce()).executeUpdate();
//    }


}
