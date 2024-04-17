package org.healthhaven.server;

import java.sql.Connection;

import org.healthhaven.db.models.AccountDAO;
import org.healthhaven.db.models.UserDAO;
import org.healthhaven.model.EmailSender;
import org.healthhaven.model.PasswordGenerator;
import org.healthhaven.model.UserIdGenerator;
import org.json.JSONObject;

public class APIHandler{
	
	public static JSONObject processAPIRequest(JSONObject json, Connection cnn) {
		
		String request = json.getString("request");
		if (!(request.equals("LOGIN") || request.equals("CREATE_ACCOUNT"))) {
			// For any request apart from LOGIN and CREATE_ACCOUNT, we must validate that the user is logged in with a cookie.
			JSONObject verifiedCookieObject = AccountDAO.verifyAuthenticationCookieById(cnn, json.optString("callerId"), json.optString("cookie"));
			if (verifiedCookieObject.getString("result").equals("FAILURE")) {
				return verifiedCookieObject;
			}
		}
		
		json.put("accountType", UserDAO.getUserAccountType(cnn, json.optString("callerId")));
		
		switch(json.getString("request")) {
			case "LOGIN":
				System.out.println("LOGIN"); //any user
				return handleLoginRequest(json, cnn);
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
			case "REQUEST_PATIENT_DATA_SUMMARY": //data analyst
				System.out.println("REQUEST_PATIENT_DATA_SUMMARY");
				return handlePatientDataSummary(json, cnn);
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
	
	private static JSONObject createRecord(JSONObject json, Connection cnn) {
		
		//Role based authorization
		if(!checkAuthorization(json.getString("accountType"), json.getString("userId"), json)) {
			return returnFailureResponse("Invalid Request");
		};

		return AccountDAO.newMedicalInformation(cnn, json.getString("patientID"),
        json.getString("doctorID"), 
        json.getFloat("height"),
        json.getFloat("weight"),
        json.getString("timestamp"));
		
	}

	private static JSONObject handleViewRecord(JSONObject json, Connection cnn) {
		
		//Role based authorization
		if(!checkAuthorization(json.getString("accountType"), json.getString("userId"), json)) {
			return returnFailureResponse("Invalid Request");
		};

		return AccountDAO.viewUserInformation(cnn,
		        json.optString("doctorID"), 
		        json.getString("patientID"));
	}

	private static JSONObject handlePatientDataSummary(JSONObject json, Connection cnn) {
		
		//Role based authorization
		if(!checkAuthorization(json.getString("accountType"), json.getString("userId"), json)) {
			return returnFailureResponse("Invalid Request");
		};

		return AccountDAO.getDataAverage(cnn);
	}

	private static JSONObject handleUpdateAccount(JSONObject json, Connection cnn) {

		switch (json.getString("updateType")) {
		case "ADDRESS":
			return AccountDAO.updateUserAddress(cnn, json.getString("userInput"), json.getString("userId"));
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

		
		//Role based authorization
		if(!checkAuthorization(json.getString("accountType"), json.getString("userId"), json)) {
			return returnFailureResponse("Invalid Request");
		};
				
		JSONObject serverResponse = new JSONObject();
		String email = json.getString("email");
		String dob = json.getString("dob");
		String userType = json.getString("userType"); //TODO this is same as accountType but accounttype is better
		if (AccountDAO.accountExistsByEmail(cnn, email)) {
			return returnFailureResponse("Email already exists");
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
	

	private static JSONObject handleLoginRequest(JSONObject json, Connection cnn) {
		
		switch (json.getString("type")) {
		case "PASSWORD":	
			System.out.println("PASSWORD");
			return AccountDAO.authenticateUser(cnn, json.getString("email"),
					json.getString("password"), "LOGIN");
		case "OTP":
			System.out.println("OTP");
			JSONObject userInformation = AccountDAO.authenticateOTP(cnn, json.getString("email"), json.getString("otp"));
			// Create and add a cookie because they're successfully authenticated into the system
			String userCookie = AccountDAO.generateAndUpdateNewUserCookie(cnn, userInformation.getString("userID"));
			if (userCookie == null) {
				return returnFailureResponse("Unable to create cookie");
			}
			userInformation.put("cookie", userCookie);
			return userInformation;
			
		default:
			return returnFailureResponse("Invalid Request");	
		} 
	}
	
	private static JSONObject handleSearchAccount(JSONObject json, Connection cnn) {
		
		//Role based authorization
		if(!checkAuthorization(json.getString("accountType"), json.getString("userId"), json)) {
			return returnFailureResponse("Invalid Request");
		};
		return AccountDAO.viewAccountInformation(cnn, json.getString("userId"));
	}
	
	private static JSONObject handleAccountDeactivation(JSONObject json, Connection cnn) {
		JSONObject verifiedCookieObject;
		switch (json.getString("type")) {
		case "VALIDATE_ACCOUNT":
			//Role based authorization
			if(!checkAuthorization(json.getString("accountType"), json.getString("userId"), json)) {
				return returnFailureResponse("Invalid Request");
			};
			return AccountDAO.authenticateUser(cnn, json.getString("email"),
					json.getString("password"), "ACCOUNT_DEACTIVATION");
		case "DEACTIVATE_ACCOUNT":
			
			//Role based authorization
			if(!checkAuthorization(json.getString("accountType"), json.getString("userId"), json)) {
				return returnFailureResponse("Invalid Request");
			};
			
			return AccountDAO.deactivateAccount(cnn, json.getString("userId"));
		default:
			return returnFailureResponse("Invalid Request");
		}
		
	}
	
	private static boolean checkAuthorization(String accountType, String callerId, JSONObject json) {
		switch (json.getString("request")) {
		case "ALLOW_ACCOUNT_CREATION": 
			if (accountType.toUpperCase().equals("SUPERADMIN")) {
				return true;
			} else if (accountType.toUpperCase().equals("DOCTOR")) {
				return json.getString("userType").toUpperCase().equals("PATIENT");
			} else {
				return false;
				
			}
			
		case "REQUEST_PATIENT_DATA_SUMMARY": //data analyst	
			return accountType.toUpperCase().equals("DATA_ANALYST");
			
		case "VIEW_RECORD": //doctor or patient
			if (accountType.toUpperCase().equals("PATIENT")) {
				return json.getString("patientID").equals(callerId);
			} else if (accountType.toUpperCase().equals("DOCTOR")) {
				return true;
			} else {
				return false;		
			}
			
		case "CREATE_RECORD": //doctor
			return accountType.toUpperCase().equals("DOCTOR");
			
		case "DEACTIVATE_ACCOUNT": //any user for themselves or admin for everyone
			System.out.println("DEACTIVATE_ACCOUNT");
			
		case "SEARCH_ACCOUNT": //super admin
			return accountType.toUpperCase().equals("SUPERADMIN");
		default:
			return false;	
		}
	}
	
	private static JSONObject returnFailureResponse(String reason) {
		JSONObject serverResponse = new JSONObject();
		serverResponse.put("result", "FAILURE");
		serverResponse.put("reason", reason);
		return serverResponse;	
	}
}