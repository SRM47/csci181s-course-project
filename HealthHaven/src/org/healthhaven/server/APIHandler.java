package org.healthhaven.server;

import java.sql.Connection;

import javax.net.ssl.SSLSocket;

import org.healthhaven.db.models.AccountDAO;
import org.healthhaven.db.models.UserDAO;
import org.healthhaven.model.EmailSender;
import org.healthhaven.model.PasswordGenerator;
import org.healthhaven.model.UserIdGenerator;
import org.json.JSONObject;

public class APIHandler{
	
	public static JSONObject processAPIRequest(JSONObject json, Connection cnn, SSLSocket clientSocket) {
		
		String request = json.getString("request");
		if (!(request.equals("LOGIN") || request.equals("CREATE_ACCOUNT")||request.equals("PASSWORD_RESET"))) {
			// For any request apart from LOGIN and CREATE_ACCOUNT, we must validate that the user is logged in with a cookie.
			JSONObject authentication = AuthenticationService.verifyAuthenticationCookie(cnn, json, clientSocket);
			if (authentication.getString("result").equals("FAILURE")) {
				return authentication;
			}
		}
		json.put("accountType", UserDAO.getUserAccountType(cnn, json.optString("callerId")));
		
		// Authorize request
		if (!(request.equals("LOGIN") || request.equals("CREATE_ACCOUNT")||request.equals("PASSWORD_RESET"))) {
			boolean isAuthorized = ReferenceMonitor.authorizeRequest(clientSocket, json.getString("accountType"), json.optString("callerId"), json);
			if (!isAuthorized) {
				return returnFailureResponse("User is not authorized to perform this command");
			}
		}
		
		
		switch(json.getString("request")) {
			case "UPDATE_DATA_SHARING":
				System.out.println("UPDATE_DATA_SHARING");
				return handleDataSharing(json, cnn);
			case "LOGIN":
				System.out.println("LOGIN"); //any user
				return handleLoginRequest(json, cnn, clientSocket);
			case "PASSWORD_RESET": //any user
				System.out.println("PASSWORD_RESET");
				return handlePasswordReset(json, cnn);
			case "ALLOW_ACCOUNT_CREATION": //doctor or super admin
				System.out.println("ALLOW_ACCOUNT_CREATION");
				return handleAccountCreation(json, cnn);
			case "CREATE_ACCOUNT": // any user
				System.out.println("CREATE_ACCOUNT");
				return handleCreateAccount(json, cnn);
			case "UPDATE_ACCOUNT": //any user
				System.out.println("UPDATE_ACCOUNT");
				return handleUpdateAccount(json, cnn);
			case "REQUEST_PATIENT_DATA": //data analyst
				System.out.println("REQUEST_PATIENT_DATA");
				return handleGetMedicalInformation(json, cnn);
			case "VIEW_RECORD": //doctor or patient
				System.out.println("VIEW_RECORD");
				return handleViewRecord(json, cnn);
			case "CREATE_RECORD": //doctor
				System.out.println("CREATE_RECORD");
				return createRecord(json, cnn);
			case "DEACTIVATE_ACCOUNT": //any user for themselves or admin for everyone
				System.out.println("DEACTIVATE_ACCOUNT");
				return handleAccountDeactivation(json, cnn);
			case "SEARCH_ACCOUNT": //super admin
				System.out.println("SEACH_ACCOUNT");
				return handleSearchAccount(json, cnn);
			case "LOGOUT": //any user
				System.out.println("LOGOUT");
				return handleLogout(json, cnn);
			default:
				return returnFailureResponse("Invalid Request");	
		}
	}
	
	private static JSONObject handleLogout(JSONObject json, Connection cnn) {
		
		return AccountDAO.logoutUser(cnn, json.getString("userId"));
	}
	
	private static JSONObject handleDataSharing(JSONObject json, Connection cnn) {
		return returnFailureResponse("this functionality not yet implemented");
		//return AccountDAO.updateDataSharingSetting(cnn, json.getString("callerId"), json.getString("data_sharing"));
	}
	
	private static JSONObject createRecord(JSONObject json, Connection cnn) {


		return AccountDAO.newMedicalInformation(cnn, json.getString("patientID"),
        json.getString("doctorID"), 
        json.getFloat("height"),
        json.getFloat("weight"),
        json.getString("timestamp"));
		
	}

	private static JSONObject handleViewRecord(JSONObject json, Connection cnn) {

		return AccountDAO.viewUserInformation(cnn,
		        json.optString("doctorID"), 
		        json.getString("patientID"));
	}

	private static JSONObject handleGetMedicalInformation(JSONObject json, Connection cnn) {

		// return AccountDAO.getDataAverage(cnn);
		return AccountDAO.getMedicalInformationDataByQuery(cnn, json.getString("when"), json.getString("date"));
	}

	private static JSONObject handleUpdateAccount(JSONObject json, Connection cnn) {

		switch (json.getString("updateType")) {
		case "ADDRESS":
			return AccountDAO.updateUserAddress(cnn, json.getString("address"), json.getString("callerId"));
		case "PASSWORD":
			return handleUpdatePassword(json, cnn);
		default:
			return returnFailureResponse("Invalid Request");	
			
		} 
		
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
			return returnFailureResponse("Email already exists");
		}
		String generatedPassword = PasswordGenerator.generate(16);
		UserIdGenerator generator = new UserIdGenerator(16);
		String generatedUserId = generator.generate();
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
			return AccountDAO.authenticateUser(cnn, json.getString("email"),
					null, "PASSWORD_RESET");
			
		case "VERIFY_OTP":
			return AccountDAO.authenticateOTP(cnn, json.getString("email"), json.getString("otp"));
				
		case "UPDATE_PASSWORD":
			return handleUpdatePassword(json, cnn);
		default:
			return returnFailureResponse("Invalid Request");	
			
			
		}
		
	}
	private static JSONObject handleUpdatePassword(JSONObject json, Connection cnn){
				
		return AccountDAO.updatePassword(cnn, json.getString("password"), json.getString("email"));
		
	}
	

	private static JSONObject handleLoginRequest(JSONObject json, Connection cnn, SSLSocket clientSocket) {
		
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
	
	private static JSONObject handleSearchAccount(JSONObject json, Connection cnn) {
		return AccountDAO.viewAccountInformation(cnn, json.getString("userId"));
	}
	
	private static JSONObject handleAccountDeactivation(JSONObject json, Connection cnn) {
		JSONObject verifiedCookieObject;
		switch (json.getString("type")) {
		case "VALIDATE_ACCOUNT":
			return AccountDAO.authenticateUser(cnn, json.getString("email"),
					json.getString("password"), "ACCOUNT_DEACTIVATION");
		case "DEACTIVATE_ACCOUNT":
			
			return AccountDAO.deactivateAccount(cnn, json.getString("userId"));
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