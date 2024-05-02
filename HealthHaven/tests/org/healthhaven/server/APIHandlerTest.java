package org.healthhaven.server;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class APIHandlerTest {
	// Tests the required fields so a faulty JSON request to the server 
	// without the required fields will not crash the server.
	
	@Test
    public void testRequestNull() {
        JSONObject json = new JSONObject();
        json.put("random", "even more random");

        JSONObject response = APIHandler.handleLogout(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

	@Test
    public void testHandleLogoutFailureResponse() {
        JSONObject json = new JSONObject();
        json.put("request", "LOGOUT");

        JSONObject response = APIHandler.handleLogout(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

    @Test
    public void testHandleDataSharingFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required field: "callerId"
        json.put("request", "UPDATE_DATA_SHARING");
        json.put("data_sharing", true);

        JSONObject response = APIHandler.handleDataSharing(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

    @Test
    public void testCreateRecordFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required fields: "patientID", "doctorID", "timestamp", "height", "weight"
        json.put("request", "CREATE_RECORD");

        JSONObject response = APIHandler.createRecord(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

    @Test
    public void testHandleViewRecordFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required field: "patientID"
        json.put("request", "VIEW_RECORD");

        JSONObject response = APIHandler.handleViewRecord(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

    @Test
    public void testHandleGetMedicalInformationFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required fields: "when", "date"
        json.put("request", "REQUEST_PATIENT_DATA");

        JSONObject response = APIHandler.handleGetMedicalInformation(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

    @Test
    public void testHandleUpdateAccountFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required fields: "updateType"
        json.put("request", "UPDATE_ACCOUNT");

        JSONObject response = APIHandler.handleUpdateAccount(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
        
        json.put("updateType", "ADDRESS");

        response = APIHandler.handleUpdateAccount(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
        
        
    }

    @Test
    public void testHandleCreateAccountFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required fields: "first_name", "last_name", "dob", "address", "email", "password", "accountType"
        json.put("request", "CREATE_ACCOUNT");

        JSONObject response = APIHandler.handleCreateAccount(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

    @Test
    public void testHandleAccountCreationFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required fields: "email", "dob", "userType", "callerId"
        json.put("request", "ALLOW_ACCOUNT_CREATION");

        JSONObject response = APIHandler.handleAccountCreation(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

    @Test
    public void testHandlePasswordResetFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required fields: "type"
        json.put("request", "PASSWORD_RESET");

        JSONObject response = APIHandler.handlePasswordReset(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

    @Test
    public void testHandleSearchAccountFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required field: "userId"
        json.put("request", "SEARCH_ACCOUNT");

        JSONObject response = APIHandler.handleSearchAccount(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }

    @Test
    public void testHandleAccountDeactivationFailureResponse() {
        JSONObject json = new JSONObject();
        // Missing required fields: "type"
        json.put("request", "DEACTIVATE_ACCOUNT");

        JSONObject response = APIHandler.handleAccountDeactivation(json, null);
        
        assertEquals("FAILURE", response.getString("result"));
        assertEquals("Missing one or more of the required fields", response.getString("reason"));
    }
	
	

}
