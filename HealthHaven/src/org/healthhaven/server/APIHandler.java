package org.healthhaven.server;

import java.sql.Connection;
import java.sql.SQLException;

import javax.net.ssl.SSLSocket;

import org.healthhaven.db.models.AccountDAO;
import org.healthhaven.db.models.UserDAO;
import org.healthhaven.model.EmailSender;
import org.healthhaven.model.PasswordGenerator;
import org.healthhaven.model.UserIdGenerator;
import org.json.JSONObject;

public class APIHandler {

	public static JSONObject processAPIRequest(JSONObject json, Connection cnn, SSLSocket clientSocket) {
		if (json.optString("request", null) == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}

		String request = json.getString("request");
		if (!(request.equals("LOGIN") || request.equals("CREATE_ACCOUNT") || request.equals("PASSWORD_RESET"))) {
			// For any request apart from LOGIN and CREATE_ACCOUNT, we must validate that
			// the user is logged in with a cookie.
			JSONObject authentication = AuthenticationService.verifyAuthenticationCookie(cnn, json, clientSocket);
			if (authentication.getString("result").equals("FAILURE")) {
				return authentication;
			}
		}
		json.put("accountType", UserDAO.getUserAccountType(cnn, json.optString("callerId")));

		// Authorize request
		if (!(request.equals("LOGIN") || request.equals("CREATE_ACCOUNT") || request.equals("PASSWORD_RESET"))) {
			boolean isAuthorized = ReferenceMonitor.authorizeRequest(clientSocket, json.getString("accountType"),
					json.optString("callerId"), json, cnn);
			if (!isAuthorized) {
				return returnFailureResponse("User is not authorized to perform this command");
			}
		}

		switch (json.getString("request")) {
		case "UPDATE_DATA_SHARING":
			System.out.println("UPDATE_DATA_SHARING");
			return handleDataSharing(json, cnn);
		case "LOGIN":
			System.out.println("LOGIN"); // any user
			return handleLoginRequest(json, cnn, clientSocket);
		case "PASSWORD_RESET": // any user
			System.out.println("PASSWORD_RESET");
			return handlePasswordReset(json, cnn);
		case "ALLOW_ACCOUNT_CREATION": // doctor or super admin
			System.out.println("ALLOW_ACCOUNT_CREATION");
			return handleAccountCreation(json, cnn);
		case "CREATE_ACCOUNT": // any user
			System.out.println("CREATE_ACCOUNT");
			return handleCreateAccount(json, cnn);
		case "UPDATE_ACCOUNT": // any user
			System.out.println("UPDATE_ACCOUNT");
			return handleUpdateAccount(json, cnn);
		case "REQUEST_PATIENT_DATA": // data analyst
			System.out.println("REQUEST_PATIENT_DATA");
			return handleGetMedicalInformation(json, cnn);
		case "VIEW_RECORD": // doctor or patient
			System.out.println("VIEW_RECORD");
			return handleViewRecord(json, cnn);
		case "CREATE_RECORD": // doctor
			System.out.println("CREATE_RECORD");
			return createRecord(json, cnn);
		case "DEACTIVATE_ACCOUNT": // any user for themselves or admin for everyone
			System.out.println("DEACTIVATE_ACCOUNT");
			return handleAccountDeactivation(json, cnn);
		case "SEARCH_ACCOUNT": // super admin
			System.out.println("SEACH_ACCOUNT");
			return handleSearchAccount(json, cnn);
		case "LOGOUT": // any user
			System.out.println("LOGOUT");
			return handleLogout(json, cnn);
		default:
			return returnFailureResponse("Invalid Request");
		}
	}

	public static JSONObject handleLogout(JSONObject json, Connection cnn) {
		// Use optString to safely handle the absence of "userId"
		String userId = json.optString("userId", null);
		if (userId != null && !userId.isEmpty()) {
			// If userId is not null and not empty, proceed to logout
			return AccountDAO.logoutUser(cnn, userId);
		} else {
			// Return an appropriate JSON response if userId is missing or empty
			return returnFailureResponse("Missing one or more of the required fields");
		}
	}

	public static JSONObject handleDataSharing(JSONObject json, Connection cnn) {
		if (json.optString("callerId", null) == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}
		return AccountDAO.updateDataSharingSetting(cnn, json.getString("callerId"), json.getBoolean("data_sharing"));
	}

	public static JSONObject createRecord(JSONObject json, Connection cnn) {
		// Use optString to safely handle the absence of "patientID" and "doctorID", and
		// optString for optional strings
		String patientID = json.optString("patientID", null);
		String doctorID = json.optString("doctorID", null);
		String timestamp = json.optString("timestamp", null);

		// Use optDouble for "height" and "weight", and handle default or incorrect
		// values
		float height = json.optFloat("height", Float.NaN);
		float weight = json.optFloat("weight", Float.NaN);

		// Validate the required fields
		if (patientID == null || patientID.isEmpty() || doctorID == null || doctorID.isEmpty() || Double.isNaN(height)
				|| Double.isNaN(weight) || timestamp == null || timestamp.isEmpty()) {
			return returnFailureResponse("Missing one or more of the required fields");
		}

		// Proceed with creating the record if all inputs are valid
		return AccountDAO.newMedicalInformation(cnn, patientID, doctorID, height, weight, timestamp);
	}

	public static JSONObject handleViewRecord(JSONObject json, Connection cnn) {
		// Using optString to avoid NullPointerException if keys do not exist
		String doctorID = json.optString("doctorID", null);
		String patientID = json.optString("patientID", null);

		if (patientID == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}
		return AccountDAO.viewUserInformation(cnn, doctorID, patientID);
	}

	public static JSONObject handleGetMedicalInformation(JSONObject json, Connection cnn) {
		// Using optString to handle potential missing values
		String when = json.optString("when", null);
		String date = json.optString("date", null);

		if (when == null || date == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}

		return AccountDAO.getMedicalInformationDataByQuery(cnn, when, date);
	}

	public static JSONObject handleUpdateAccount(JSONObject json, Connection cnn) {
		if (json.optString("updateType", null) == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}
		switch (json.getString("updateType")) {
		case "ADDRESS":
			if (json.optString("address", null) == null || json.optString("callerId", null) == null) {
				return returnFailureResponse("Missing one or more of the required fields");
			}
			return AccountDAO.updateUserAddress(cnn, json.getString("address"), json.getString("callerId"));
		case "PASSWORD":
			return handleUpdatePassword(json, cnn);
		default:
			return returnFailureResponse("Invalid Request");

		}

	}

	public static JSONObject handleCreateAccount(JSONObject json, Connection cnn) {
		// Safely retrieving each parameter
		String firstName = json.optString("first_name", null);
		String lastName = json.optString("last_name", null);
		String dob = json.optString("dob", null);
		String address = json.optString("address", null);
		String email = json.optString("email", null);
		String password = json.optString("password", null);
		String accountType = json.optString("accountType", null);

		if (firstName == null || lastName == null || dob == null || address == null || email == null || password == null
				|| accountType == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}

		return AccountDAO.updateTemporaryUserAfterFirstLogin(cnn, firstName, lastName, dob, address, email, password,
				accountType);
	}

	public static JSONObject handleAccountCreation(JSONObject json, Connection cnn) {
		String email = json.optString("email", null);
		String dob = json.optString("dob", null);
		String userType = json.optString("userType", null); // Notice using optString now
		String callerId = json.optString("callerId", null); // Notice using optString now

		if (email == null || dob == null || userType == null || callerId == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}

		if (AccountDAO.accountExistsByEmail(cnn, email)) {
			return returnFailureResponse("Email already exists");
		}

		String generatedPassword = PasswordGenerator.generate(16);
		UserIdGenerator generator = new UserIdGenerator(16);
		String generatedUserId = generator.generate();
		JSONObject serverResponse = AccountDAO.createTemporaryUser(cnn, generatedUserId, email, generatedPassword, dob,
				userType, callerId);

		if ("FAILURE".equals(serverResponse.optString("result"))) {
			return serverResponse;
		}

		String result = EmailSender.sendDefaultPasswordEmail(email, generatedPassword, userType);

		if (result.equals("SUCCESS")) {
			return serverResponse;
		} else {
			try {
				AccountDAO.deleteUserData(cnn, "healthhaven.users", generatedUserId);
				AccountDAO.deleteMedicalMapData(cnn, "healthhaven.medical_map", generatedUserId);
				cnn.commit();
				return returnFailureResponse("Error sending email");
			} catch (SQLException e) {
				return returnFailureResponse("Cannot delete the row");
			}

		}
	}

	public static JSONObject handlePasswordReset(JSONObject json, Connection cnn) {
		if (json.optString("type", null) == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}
		JSONObject serverResponse = new JSONObject();
		switch (json.getString("type")) {
		case "EMAIL_CHECK":
			if (json.optString("email", null) == null) {
				return returnFailureResponse("Missing one or more of the required fields");
			}
			return AccountDAO.authenticateUser(cnn, json.getString("email"), null, "PASSWORD_RESET");

		case "VERIFY_OTP":
			if (json.optString("email", null) == null || json.optString("otp", null) == null) {
				return returnFailureResponse("Missing one or more of the required fields");
			}
			return AccountDAO.authenticateOTP(cnn, json.getString("email"), json.getString("otp"));

		case "UPDATE_PASSWORD":
			return handleUpdatePassword(json, cnn);
		default:
			return returnFailureResponse("Invalid Request");

		}

	}

	public static JSONObject handleUpdatePassword(JSONObject json, Connection cnn) {
		String password = json.optString("password", null);
		String email = json.optString("email", null);

		if (password == null || email == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}

		return AccountDAO.updatePassword(cnn, password, email);
	}

	public static JSONObject handleLoginRequest(JSONObject json, Connection cnn, SSLSocket clientSocket) {
		if (json.optString("type", null) == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}
		switch (json.getString("type")) {
		case "PASSWORD":
			System.out.println("PASSWORD");
//			return AccountDAO.authenticateUser(cnn, json.getString("email"),
//					json.getString("password"), "LOGIN");
			return AuthenticationService.authenticateUserWithPassword(cnn, json, clientSocket);
		case "OTP":
			System.out.println("OTP");
//			JSONObject userInformation = AccountDAO.authenticateOTP(cnn, json.getString("email"), json.getString("otp"));
//			if (!userInformation.getString("result").equals("SUCCESS")) {
//				return returnFailureResponse(userInformation.getString("reason"));
//			}
//			// Create and add a cookie because they're successfully authenticated into the system
//			String userCookie = AccountDAO.generateAndUpdateNewUserCookie(cnn, userInformation.getString("userID"));
//			if (userCookie == null) {
//				return returnFailureResponse("Unable to create cookie");
//			}
//			userInformation.put("cookie", userCookie);
//			return userInformation;
			return AuthenticationService.authenticateUserWithOTP(cnn, json, clientSocket);

		default:
			return returnFailureResponse("Invalid Request");
		}
	}

	public static JSONObject handleSearchAccount(JSONObject json, Connection cnn) {
		String userId = json.optString("userId", null);

		if (userId == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}

		return AccountDAO.viewAccountInformation(cnn, userId);
	}

	public static JSONObject handleAccountDeactivation(JSONObject json, Connection cnn) {
		if (json.optString("type", null) == null) {
			return returnFailureResponse("Missing one or more of the required fields");
		}
		JSONObject verifiedCookieObject;
		switch (json.getString("type")) {
		case "VALIDATE_ACCOUNT":
			if (json.optString("email", null) == null || (json.optString("password", null) == null)) {
				return returnFailureResponse("Missing one or more of the required fields");
			}
			return AccountDAO.authenticateUser(cnn, json.optString("email", null), json.optString("password", null),
					"ACCOUNT_DEACTIVATION");
		case "DEACTIVATE_ACCOUNT":
			if (json.optString("userId", null) == null) {
				return returnFailureResponse("Missing one or more of the required fields");
			}

			return AccountDAO.deactivateAccount(cnn, json.optString("userId", null));
		default:
			return returnFailureResponse("Invalid Request");
		}

	}

	private static JSONObject returnFailureResponse(String reason) {
		JSONObject serverResponse = new JSONObject();
		serverResponse.put("result", "FAILURE");
		serverResponse.put("reason", reason);
		return serverResponse;
	}
}