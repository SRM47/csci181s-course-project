package org.healthhaven.server;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;

import org.healthhaven.db.models.AccountDAO;
import org.healthhaven.model.EmailSender;
import org.healthhaven.model.PasswordGenerator;
import org.healthhaven.model.TOTP;
import org.healthhaven.model.UserIdGenerator;
import org.json.JSONObject;

public class APIHandler{
	
	public static JSONObject processAPIRequest(JSONObject json, Connection cnn) {
		switch(json.getString("request")) {
			case "LOGIN":
				System.out.println("LOGIN");
				return handleLoginRequest(json, cnn);
			case "PASSWORD_RESET":
				System.out.println("PASSWORD_RESET");
				return handlePasswordReset(json, cnn);
			case "ALLOW_ACCOUNT_CREATION":
				System.out.println("ALLOW_ACCOUNT_CREATION");
				return handleAccountCreation(json, cnn);
			case "CREATE_ACCOUNT":
				System.out.println("CREATE_ACCOUNT");
				return handleCreateAccount(json, cnn);
			case "UPDATE_ACCOUNT":
				System.out.println("UPDATE_ACCOUNT");
				return handleUpdateAccount(json, cnn);
			case "REQUEST_PATIENT_DATA_SUMMARY":
				System.out.println("REQUEST_PATIENT_DATA_SUMMARY");
				return handlePatientDataSummary(json, cnn);
			case "VIEW_RECORD":
				System.out.println("VIEW_RECORD");
				return handleViewRecord(json, cnn);
			case "CREATE_RECORD":
				System.out.println("CREATE_RECORD");
				return createRecord(json, cnn);
			case "DEACTIVATE_ACCOUNT":
				System.out.println("DEACTIVATE_ACCOUNT");
				return handleAccountDeactivation(json, cnn);
			case "SEARCH_ACCOUNT":
				System.out.println("SEACH_ACCOUNT");
				return handleSearchAccount(json, cnn);
			case "LOGOUT":
				System.out.println("LOGOUT");
				return handleLogout(json, cnn);
			default:
				JSONObject serverResponse = new JSONObject();
				serverResponse.put("result", "FAILURE");
				serverResponse.put("reason", "Invalid Request");
				return serverResponse;	
		}
	}
	
	private static JSONObject handleLogout(JSONObject json, Connection cnn) {
		JSONObject verifiedCookieObject = AccountDAO.verifyAuthenticationCookieById(cnn, json.optString("callerID"), json.optString("cookie"));
		if (verifiedCookieObject.getString("result").equals("FAILURE")) {
			return verifiedCookieObject;
		}

		return AccountDAO.logoutUser(cnn, json.getString("userId"));
	}
	
	private static JSONObject createRecord(JSONObject json, Connection cnn) {
		// The doctor is calling this so we must make sure the doctor is logged in with a cookie.
		JSONObject verifiedCookieObject = AccountDAO.verifyAuthenticationCookieById(cnn, json.optString("callerID"), json.optString("cookie"));
		if (verifiedCookieObject.getString("result").equals("FAILURE")) {
			return verifiedCookieObject;
		}

		return AccountDAO.newMedicalInformation(cnn, json.getString("patientID"),
        json.getString("doctorID"), 
        json.getFloat("height"),
        json.getFloat("weight"),
        json.getString("timestamp"));
		
	}

	private static JSONObject handleViewRecord(JSONObject json, Connection cnn) {
		// Handle Cookie Logic here.
		return AccountDAO.viewUserInformation(cnn,
		        json.optString("doctorID"), 
		        json.getString("patientID"));
	}

	private static JSONObject handlePatientDataSummary(JSONObject json, Connection cnn) {

		JSONObject verifiedCookieObject = AccountDAO.verifyAuthenticationCookieById(cnn, json.optString("callerId"), json.optString("cookie"));
		if (verifiedCookieObject.getString("result").equals("FAILURE")) {
			return verifiedCookieObject;
		}
		return AccountDAO.getDataAverage(cnn);
	}

	private static JSONObject handleUpdateAccount(JSONObject json, Connection cnn) {
		JSONObject verifiedCookieObject = AccountDAO.verifyAuthenticationCookieById(cnn, json.optString("callerId"), json.optString("cookie"));
		if (verifiedCookieObject.getString("result").equals("FAILURE")) {
			return verifiedCookieObject;
		}
		switch (json.getString("updateType")) {
		case "ADDRESS":
			return AccountDAO.updateUserAddress(cnn, json.getString("userInput"), json.getString("userId"));
		case "PASSWORD":
			return handleUpdatePassword(json, cnn);
		default:
			JSONObject serverResponse = new JSONObject();
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "incorrect request");
			return serverResponse;	
			
		} 
		
	}

	private static JSONObject handleCreateAccount(JSONObject json, Connection cnn) {
		JSONObject verifiedCookieObject = AccountDAO.verifyAuthenticationCookieByEmail(cnn, json.optString("callerEmail"), json.optString("cookie"));
		if (verifiedCookieObject.getString("result").equals("FAILURE")) {
			return verifiedCookieObject;
		}
		return AccountDAO.updateTemporaryUserAfterFirstLogin(cnn,
		json.getString("first_name"), json.getString("last_name"),
		json.getString("dob"), json.getString("address"), json.getString("email"),
		json.getString("password"), json.getString("accountType"));

	}

	private static JSONObject handleAccountCreation(JSONObject json, Connection cnn) {
		JSONObject verifiedCookieObject = AccountDAO.verifyAuthenticationCookieById(cnn, json.optString("callerId"), json.optString("cookie"));
		if (verifiedCookieObject.getString("result").equals("FAILURE")) {
			return verifiedCookieObject;
		}
		JSONObject serverResponse = new JSONObject();
		String email = json.getString("email");
		String dob = json.getString("dob");
		String userType = json.getString("userType"); //TODO this is same as accountType but accounttype is better
		if (AccountDAO.accountExistsByEmail(cnn, email)) {
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "Email already exists");
			return serverResponse;
		}
		String generatedPassword = PasswordGenerator.generate(16);
		UserIdGenerator g = new UserIdGenerator(16);
		String generatedUserId = g.generate();
		serverResponse = AccountDAO.createTemporaryUser(cnn, generatedUserId, email, generatedPassword, dob, userType);
		if (serverResponse.get("result").equals("FAILURE")) {
			// If there was an error in updating the database, do not send email and return failure right away.
			return serverResponse;
		}
		EmailSender.sendDefaultPasswordEmail(email, generatedPassword, userType);
		return serverResponse;
	}

	private static JSONObject handlePasswordReset(JSONObject json, Connection cnn) {
		JSONObject verifiedCookieObject = AccountDAO.verifyAuthenticationCookieByEmail(cnn, json.optString("callerEmail"), json.optString("cookie"));
		if (verifiedCookieObject.getString("result").equals("FAILURE")) {
			return verifiedCookieObject;
		}
		JSONObject serverResponse = new JSONObject();
		switch(json.getString("type")) {
		case "EMAIL_CHECK":
			return AccountDAO.authenticateUser(cnn, json.getString("email"),
					null, "PASSWORD_RESET");
			
		case "VERIFY_OTP":
			return AccountDAO.authenticateOTP(cnn, json.getString("email"), json.getString("otp"));
				
		case "UPDATE_PASSWORD":
			return handleUpdatePassword(json, cnn);
		default:
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "incorrect request");
			return serverResponse;	
			
			
		}
		
	}
	private static JSONObject handleUpdatePassword(JSONObject json, Connection cnn){ 
		return AccountDAO.updatePassword(cnn, json.getString("password"), json.getString("email"));
		
	}
	

	private static JSONObject handleLoginRequest(JSONObject json, Connection cnn) {
		
		switch (json.getString("type")) {
		case "PASSWORD":	
			System.out.println("PASSWORD");
			return AccountDAO.authenticateUser(cnn, json.getString("email"),
					json.getString("password"), "LOGIN");
		case "OTP":
			System.out.println("OTP");
			return AccountDAO.authenticateOTP(cnn, json.getString("email"), json.getString("otp"));
		default:
			JSONObject serverResponse = new JSONObject();
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "Invalid Request");
			return serverResponse;	
		} 
	}
	
	private static JSONObject handleSearchAccount(JSONObject json, Connection cnn) {
		return AccountDAO.viewAccountInformation(cnn, json.getString("userId"));
	}
	
	private static JSONObject handleAccountDeactivation(JSONObject json, Connection cnn) {
		JSONObject verifiedCookieObject;
		switch (json.getString("type")) {
		case "VALIDATE_ACCOUNT":
			verifiedCookieObject = AccountDAO.verifyAuthenticationCookieByEmail(cnn, json.optString("callerEmail"), json.optString("cookie"));
			if (verifiedCookieObject.getString("result").equals("FAILURE")) {
				return verifiedCookieObject;
			}
			return AccountDAO.authenticateUser(cnn, json.getString("email"),
					json.getString("password"), "ACCOUNT_DEACTIVATION");
		case "DEACTIVATE_ACCOUNT":
			verifiedCookieObject = AccountDAO.verifyAuthenticationCookieById(cnn, json.optString("callerId"), json.optString("cookie"));
			if (verifiedCookieObject.getString("result").equals("FAILURE")) {
				return verifiedCookieObject;
			}
			return AccountDAO.deactivateAccount(cnn, json.getString("userId"));
		default:
			JSONObject serverResponse = new JSONObject();
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "Invalid Request");
			return serverResponse;	
		}
		
	}
}