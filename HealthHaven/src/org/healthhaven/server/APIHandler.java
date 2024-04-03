package org.healthhaven.server;

import java.sql.Connection;

import org.healthhaven.db.models.AccountDAO;
import org.healthhaven.model.EmailSender;
import org.healthhaven.model.PasswordGenerator;
import org.healthhaven.model.UserIdGenerator;
import org.json.JSONObject;

public class APIHandler{
	
	public static JSONObject processAPIRequest(JSONObject json, Connection cnn) {
		switch(json.getString("request")) {
			case "LOGIN":
				return handleLoginRequest(json, cnn);
			case "PASSWORD_RESET":
				return handlePasswordReset(json, cnn);
			case "ALLOW_ACCOUNT_CREATION":
				return handleAccountCreation(json, cnn);
			case "CREATE_ACCOUNT":
				return handleCreateAccount(json, cnn);
			case "UPDATE_ACCOUNT":
				return handleUpdateAccount(json, cnn);
			case "REQUEST_PATIENT_DATA_SUMMARY":
				return handlePatientDataSummary(json, cnn);
			case "VIEW_RECORD":
				return handleViewRecord(json, cnn);
			case "CREATE_RECORD":
				return createRecord(json, cnn);
			default:
				JSONObject serverResponse = new JSONObject();
				serverResponse.put("result", "FAILURE");
				serverResponse.put("reason", "incorrect request");
				return serverResponse;	
		}
	}
	
	private static JSONObject createRecord(JSONObject json, Connection cnn) {
		// TODO Auto-generated method stub
		return null;
	}

	private static JSONObject handleViewRecord(JSONObject json, Connection cnn) {
		// TODO Auto-generated method stub
		return null;
	}

	private static JSONObject handlePatientDataSummary(JSONObject json, Connection cnn) {
		// TODO Auto-generated method stub
		return null;
	}

	private static JSONObject handleUpdateAccount(JSONObject json, Connection cnn) {
		String newAddress = json.getString("address");
		String userId = json.getString("userId");
		//TODO: needs to be json file
		return AccountDAO.updateUserInformation(cnn, newAddress, userId);
	}

	private static JSONObject handleCreateAccount(JSONObject json, Connection cnn) {
		boolean success = AccountDAO.updateTemporaryUserAfterFirstLogin(cnn,
		json.getString("first_name"), json.getString("last_name"),
		json.getString("dob"), json.getString("address"), json.getString("email"),
		json.getString("password"), json.getString("accountType"));
		String response = success ? "SUCCESS" : "FAILURE";
		return null;
	}

	private static JSONObject handleAccountCreation(JSONObject json, Connection cnn) {
		String email = json.getString("email");
		String dob = json.getString("dob");
		String userType = json.getString("userType"); //TODO this is same as accountType but accounttype is better
		if (AccountDAO.accountExistsByEmail(cnn, email)) {
			String response = "FAILURE";
			return null;
		}
		String generatedPassword = PasswordGenerator.generate(16);
		UserIdGenerator g = new UserIdGenerator(16);
		String generatedUserId = g.generate();
		AccountDAO.createTemporaryUser(cnn, generatedUserId, email, generatedPassword, dob, userType);
		EmailSender.sendDefaultPasswordEmail(email, generatedPassword, userType);
//		result:
//		type: NEW or EXISTING
//		if new, usertype: DOCTOR, etc TODO
		String response = "SUCCESS";
		return null;
	}

	private static JSONObject handlePasswordReset(JSONObject json, Connection cnn) {
		switch(json.getString("type")) {
		case "EMAIL_CHECK":
			if (AccountDAO.accountExistsByEmail(cnn, json.getString("email"))) {
				JSONObject serverResponse = new JSONObject();
				serverResponse.put("result", "SUCCESS");
				serverResponse.put("userID", "12345"); //TODO
				return serverResponse;
			} 
		case "VERIFY_OTP":
			String jj = AccountDAO.authenticateOTP(cnn, json.getString("email"), json.getString("otp"));
			return null;
				
		case "UPDATE_PASSWORD":
			return AccountDAO.updatePassword(cnn, json.getString("password"),
					json.getString("userId"));
		default:
			JSONObject serverResponse = new JSONObject();
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "incorrect request");
			return serverResponse;	
			
		}
	}

	private static JSONObject handleLoginRequest(JSONObject json, Connection cnn) {
		switch (json.getString("type")) {
		case "PASSWORD":						
			String response = AccountDAO.authenticateUser(cnn, json.getString("email"),
					json.getString("password"));
			return null;
		case "OTP":
			String response1 = AccountDAO.authenticateOTP(cnn, json.getString("email"),
					json.getString("OTP"));
			return null;
		default:
			JSONObject serverResponse = new JSONObject();
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "incorrect request");
			return serverResponse;	
		} 
	}
}