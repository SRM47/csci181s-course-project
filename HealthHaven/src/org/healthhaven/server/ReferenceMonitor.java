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
public class ReferenceMonitor {
	
	private static final String AUTHORIZATION_LOG_FILE_PATH = "authorization.log";
	
	protected static boolean authorizeRequest(SSLSocket clientSocket, String accountType, String callerId, JSONObject json, Connection conn) {
		boolean res = false;
		String reason = "Unauthorized access";
		switch (json.getString("request")) {
		case "UPDATE_DATA_SHARING":
			if (accountType.equals("Patient")) {
				res = true;
				reason = "";
			} else {
				res = false;
			}
			break;
		case "ALLOW_ACCOUNT_CREATION": 
			if (accountType.equals("Superadmin")) {
				res = true;
				reason = "";
			} else if (accountType.equals("Doctor")) {
				res = "Patient".equals(json.optString("userType"));
				reason = res ? "" : "User type trying to be accessed is not a patient";
			} else {
				res = false;
			}
			break;
			
		case "REQUEST_PATIENT_DATA": //data analyst	
			res = accountType.equals("Data Analyst");
			reason = res ? "" : "Account type is not Data Analyst";
			break;
			
		case "VIEW_RECORD": //doctor or patient
			if (accountType.equals("Patient")) {
				res = callerId.equals(json.optString("patientID"));
				reason = res ? "" : "Caller is not the patient whoes data is being accessed";
			} else if (accountType.equals("Doctor")) {
				res = AccountDAO.isDoctorAuthorizedToViewPatientData(conn, json.optString("doctorID"), json.optString("patientID")).getString("result").equals("SUCCESS");
				reason = res ? "" : "Doctor is unauthorized to view this patient's data";
			} else {
				res = false;		
			}
			break;
			
		case "CREATE_RECORD": //doctor
			res = accountType.equals("Doctor") && AccountDAO.isDoctorAuthorizedToViewPatientData(conn, json.optString("doctorID"), json.optString("patientID")).getString("result").equals("SUCCESS");
			reason = res ? "" : "Doctor is unauthorized to create data for this patient";
			break;
			
		case "DEACTIVATE_ACCOUNT": //any user for themselves or admin for everyone
			if (accountType.equals("Superadmin")) {
				res = true;
				reason = "";
			} else {
				if ("VALIDATE_ACCOUNT".equals(json.optString("type"))) {
					// Check if the userID associated with the email is equal to the caller's ID
					JSONObject userIdFromEmail = AccountDAO.verifyUserIdfromEmail(conn, callerId, json.optString("email"));
					res = userIdFromEmail.getString("result").equals("SUCCESS");
					reason = userIdFromEmail.getString("reason");
				} else if ("DEACTIVATE_ACCOUNT".equals(json.optString("type"))) {
					res = callerId.equals(json.optString("userId"));
					reason = res ? "" : "Caller Id does not match the user id being deactivated";
				}
			}
			break;
			
		case "SEARCH_ACCOUNT": //super admin
			res = accountType.equals("Superadmin");
			reason = res ? "" : "Must be a superadmin";
			break;
			
		case "LOGIN","PASSWORD_RESET","CREATE_ACCOUNT","UPDATE_ACCOUNT","LOGOUT":
			res = true;
			reason = "";
			break;
		default:
			res = false;	
			break;
		}
		
		String logMessage = String.format("request:%s caller_id:%s result:%s reason:%s\n", json.optString("request"),
				callerId, res ? "SUCCESS" : "FAILURE", reason);
		log(clientSocket, logMessage);
		return res;
	}
	
	private static void log(SSLSocket clientSocket, String message) {
		Logger.log(AUTHORIZATION_LOG_FILE_PATH, clientSocket.getInetAddress().getHostAddress(), message);
	}

}
