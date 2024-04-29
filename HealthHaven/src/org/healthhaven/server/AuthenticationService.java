/**
 * 
 */
package org.healthhaven.server;

import java.sql.Connection;
import java.sql.SQLException;

import javax.net.ssl.SSLSocket;

import org.healthhaven.db.models.AccountDAO;
import org.healthhaven.logging.Logger;
import org.json.JSONObject;

/**
 * 
 */
public class AuthenticationService {

	private static final String AUTHENTICATION_LOG_FILE_PATH = "authentication.log";

	public static JSONObject verifyAuthenticationCookie(Connection conn, JSONObject request, SSLSocket clientSocket) {
	    String userId = request.optString("callerId", null);
	    String cookie = request.optString("cookie", null);

		if (userId == null || cookie == null) {
			return returnFailureResponse("Missing user ID or cookie.");
		}
	    
		JSONObject isAuthenticated = AccountDAO.verifyAuthenticationCookieById(conn, request.optString("callerId"),
				request.optString("cookie"));
		
		if (isAuthenticated.getString("result").equals("SUCCESS")) {
			if (!AccountDAO.updateCookieTimestamp(conn, userId)) {
		        return returnFailureResponse("Failed to update session timestamp.");
		    }
		}

		String logMessage = String.format("request:%s caller_id:%s result:%s reason:%s\n", request.getString("request"),
				request.optString("callerId"), isAuthenticated.getString("result"),
				isAuthenticated.getString("reason"));
		log(clientSocket, logMessage);

		return isAuthenticated;
	}

	public static JSONObject authenticateUserWithPassword(Connection conn, JSONObject request, SSLSocket clientSocket) {
		String email = request.optString("email", null);
		String password = request.optString("password", null);
	
		if (email == null || password == null) {
			return returnFailureResponse("Missing email or password.");
		}
	
		JSONObject isAuthenticatedByPassword = AccountDAO.authenticateUser(conn, email, password, "LOGIN");
		String logMessage = String.format("request:%s caller_email:%s result:%s reason:%s\n", request.optString("request"),
				email, isAuthenticatedByPassword.optString("result"), isAuthenticatedByPassword.optString("reason", "No reason provided"));
		log(clientSocket, logMessage);
	
		return isAuthenticatedByPassword;
	}
	

	public static JSONObject authenticateUserWithOTP(Connection conn, JSONObject request, SSLSocket clientSocket) {
		String email = request.optString("email", null);
		String otp = request.optString("otp", null);
	
		if (email == null || otp == null) {
			return returnFailureResponse("Missing email or OTP.");
		}
	
		JSONObject userInformation = AccountDAO.authenticateOTP(conn, email, otp);
		if ("SUCCESS".equals(userInformation.optString("result"))) {
			String userId = userInformation.optString("userID");
			if (userId == null) {
				return returnFailureResponse("User ID missing from authentication response.");
			}
			String userCookie = AccountDAO.generateAndUpdateNewUserCookie(conn, userId);
			if (userCookie == null) {
				return returnFailureResponse("Unable to create cookie");
			}
			userInformation.put("cookie", userCookie);
		}
	
		String logMessage = String.format("request:%s caller_email:%s result:%s reason:%s\n", request.optString("request"),
				email, userInformation.optString("result"), userInformation.optString("reason", "No reason provided"));
		log(clientSocket, logMessage);
	
		return userInformation;
	}
	

	private static void log(SSLSocket clientSocket, String message) {
		Logger.log(AUTHENTICATION_LOG_FILE_PATH, clientSocket.getInetAddress().getHostAddress(), message);
	}

	private static JSONObject returnFailureResponse(String reason) {
		JSONObject serverResponse = new JSONObject();
		serverResponse.put("result", "FAILURE");
		serverResponse.put("reason", reason);
		return serverResponse;
	}

}
