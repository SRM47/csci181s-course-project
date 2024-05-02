/**
 * 
 */
package org.healthhaven.server;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * 
 */
class AuthorizationTest {

	@Test
    public void testAuthorizeUpdateDataSharing() {
        JSONObject json = new JSONObject();
        json.put("request", "UPDATE_DATA_SHARING");

        assertTrue(ReferenceMonitor.authorizeRequest(null, "Patient", null, json, null));
        assertFalse(ReferenceMonitor.authorizeRequest(null, "Doctor", null, json, null));
        assertFalse(ReferenceMonitor.authorizeRequest(null, "Superadmin", null, json, null));
    }

    @Test
    public void testAuthorizeAllowAccountCreation() {
        JSONObject jsonSuperadmin = new JSONObject();
        jsonSuperadmin.put("request", "ALLOW_ACCOUNT_CREATION");
        jsonSuperadmin.put("userType", "Data Analyst");

        JSONObject jsonDoctor = new JSONObject();
        jsonDoctor.put("request", "ALLOW_ACCOUNT_CREATION");
        jsonDoctor.put("userType", "Patient");

        assertTrue(ReferenceMonitor.authorizeRequest(null, "Superadmin", null, jsonSuperadmin, null));
        assertTrue(ReferenceMonitor.authorizeRequest(null, "Doctor", null, jsonDoctor, null));
        assertFalse(ReferenceMonitor.authorizeRequest(null, "Patient", null, jsonSuperadmin, null));
    }

    @Test
    public void testAuthorizeRequestPatientData() {
        JSONObject json = new JSONObject();
        json.put("request", "REQUEST_PATIENT_DATA");

        assertTrue(ReferenceMonitor.authorizeRequest(null, "Data Analyst", null, json, null));
        assertFalse(ReferenceMonitor.authorizeRequest(null, "Doctor", null, json, null));
        assertFalse(ReferenceMonitor.authorizeRequest(null, "Superadmin", null, json, null));
    }

    @Test
    public void testAuthorizeSearchAccount() {
        JSONObject json = new JSONObject();
        json.put("request", "SEARCH_ACCOUNT");

        assertTrue(ReferenceMonitor.authorizeRequest(null, "Superadmin", null, json, null));
        assertFalse(ReferenceMonitor.authorizeRequest(null, "Doctor", null, json, null));
        assertFalse(ReferenceMonitor.authorizeRequest(null, "Patient", null, json, null));
    }

    @Test
    public void testAuthorizeDefault() {
        JSONObject json = new JSONObject();
        json.put("request", "INVALID_REQUEST");

        assertFalse(ReferenceMonitor.authorizeRequest(null, "Superadmin", null, json, null));
        assertFalse(ReferenceMonitor.authorizeRequest(null, "Doctor", null, json, null));
        assertFalse(ReferenceMonitor.authorizeRequest(null, "Patient", null, json, null));
    }

}
