/**
 * 
 */
package org.healthhaven.server;

import java.sql.Connection;

import javax.net.ssl.SSLSocket;

import org.healthhaven.db.models.AccountDAO;
import org.healthhaven.logging.Logger;
import org.json.JSONObject;

/**
 * 
 */
public class AuthenticationService {

	private static final String AUTH_LOG_FILE_PATH = "auth_log_file.log";

	public static JSONObject verifyAuthenticationCookie(Connection conn, JSONObject request, SSLSocket clientSocket) {
		JSONObject isAuthenticated = AccountDAO.verifyAuthenticationCookieById(conn, request.optString("callerId"),
				request.optString("cookie"));

		String logMessage = String.format("request:%s caller_id:%s result:%s reason:%s\n", request.getString("request"),
				request.optString("callerId"), isAuthenticated.getString("result"),
				isAuthenticated.getString("reason"));
		log(clientSocket, logMessage);

		return isAuthenticated;
	}

	public static JSONObject authenticateUserWithPassword(Connection conn, JSONObject request, SSLSocket clientSocket) {
		JSONObject isAuthenticatedByPassword = AccountDAO.authenticateUser(conn, request.getString("email"),
				request.getString("password"), "LOGIN");

		String logMessage = String.format("request:%s caller_email:%s result:%s reason:%s\n", request.getString("request"),
				request.optString("email"), isAuthenticatedByPassword.getString("result"),
				isAuthenticatedByPassword.getString("reason"));
		log(clientSocket, logMessage);

		return isAuthenticatedByPassword;
	}

	public static JSONObject authenticateUserWithOTP(Connection conn, JSONObject request, SSLSocket clientSocket) {
		JSONObject userInformation = AccountDAO.authenticateOTP(conn, request.getString("email"),
				request.getString("otp"));

		if (userInformation.getString("result").equals("FAILURE")) {
			// Create and add a cookie because they're successfully authenticated into the
			// system
			String userCookie = AccountDAO.generateAndUpdateNewUserCookie(conn, userInformation.getString("userID"));
			if (userCookie == null) {
				return returnFailureResponse("Unable to create cookie");
			}
			userInformation.put("cookie", userCookie);
		}

		String logMessage = String.format("request:%s caller_email:%s result:%s reason:%s\n", request.getString("request"),
				request.optString("email"), userInformation.getString("result"), userInformation.getString("reason"));
		log(clientSocket, logMessage);

		return userInformation;
	}

	private static void log(SSLSocket clientSocket, String message) {
		Logger.log(AUTH_LOG_FILE_PATH, clientSocket.getInetAddress().getHostAddress(), message);
	}

	private static JSONObject returnFailureResponse(String reason) {
		JSONObject serverResponse = new JSONObject();
		serverResponse.put("result", "FAILURE");
		serverResponse.put("reason", reason);
		return serverResponse;
	}

}
