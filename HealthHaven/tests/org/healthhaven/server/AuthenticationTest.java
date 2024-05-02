/**
 * 
 */
package org.healthhaven.server;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 
 */
class AuthenticationTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
    public void testServerResponseWithInvalidCookieOrId() {
        // Create a JSONObject with callerId and cookie fields
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("callerId", "exampleCallerId");
        jsonObject.put("cookie", "exampleCookie");

        // Simulate sending the JSONObject to the server and getting a response
        JSONObject response = new JSONObject(ServerCommunicator.communicateWithServer(jsonObject.toString()));

        // Check if the response contains "FAILURE" in the "result" key
        assertEquals("FAILURE", response.getString("result"));
    }

}
