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
			default:
				JSONObject serverResponse = new JSONObject();
				serverResponse.put("result", "FAILURE");
				serverResponse.put("reason", "Invalid Request");
				return serverResponse;	
		}
	}
	
	private static JSONObject createRecord(JSONObject json, Connection cnn) {
//		return AccountDAO.updateUserInformation(cnn, newAddress, userId);
		return AccountDAO.newMedicalInformation(cnn, json.getString("userID"),
        json.getString("doctorID"), 
        json.getString("height"),
        json.getString("weight"),
        json.getString("timestamp"));
		
	}

	private static JSONObject handleViewRecord(JSONObject json, Connection cnn) {
		return AccountDAO.viewUserInformation(cnn,
		        json.optString("doctorID"), 
		        json.getString("patientID"));
	}

	private static JSONObject handlePatientDataSummary(JSONObject json, Connection cnn) {
		return AccountDAO.getDataAverage(cnn);
	}

	private static JSONObject handleUpdateAccount(JSONObject json, Connection cnn) {
		String newAddress = json.getString("address");
		String userId = json.getString("userId");
		//TODO: needs to be json file
		return AccountDAO.updateUserInformation(cnn, newAddress, userId);
	}

	private static JSONObject handleCreateAccount(JSONObject json, Connection cnn) {
		return AccountDAO.updateTemporaryUserAfterFirstLogin(cnn,
		json.getString("first_name"), json.getString("last_name"),
		json.getString("dob"), json.getString("address"), json.getString("email"),
		json.getString("password"), json.getString("accountType"));

	}

	private static JSONObject handleAccountCreation(JSONObject json, Connection cnn) {
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
		JSONObject serverResponse = new JSONObject();
		switch(json.getString("type")) {
		case "EMAIL_CHECK":
			
			String result = "SUCCESS";
			String reason = "";
			if (!AccountDAO.accountExistsByEmail(cnn, json.getString("email"))) {
				result = "FAILURE";
				reason = "Account doesn't exist";
			} else {
				// Send email with OTP to person only if email is verified.
				try {
					String OTP = TOTP.genSecretKey().toString();
					EmailSender.sendDefaultPasswordEmail(json.getString("email"), OTP, "None");
				} catch (Exception e) {
					result = "FAILURE";
					reason = "Error generating OTP";
				}
				
			}
			serverResponse.put("result", result);
			serverResponse.put("reason", reason);
			return serverResponse;
			
		case "VERIFY_OTP":
			return AccountDAO.authenticateOTP(cnn, json.getString("email"), json.getString("otp"));
				
		case "UPDATE_PASSWORD":
			return AccountDAO.updatePassword(cnn, json.getString("password"),
					json.getString("email"));
		default:
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "incorrect request");
			return serverResponse;	
			
			
		}
		
	}

	private static JSONObject handleLoginRequest(JSONObject json, Connection cnn) {
		switch (json.getString("type")) {
		case "PASSWORD":	
			System.out.println("PASSWORD");
			return AccountDAO.authenticateUser(cnn, json.getString("email"),
					json.getString("password"));
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
}