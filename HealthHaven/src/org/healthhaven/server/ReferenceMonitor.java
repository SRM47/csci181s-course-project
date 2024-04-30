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
		switch (json.getString("request")) {
		case "UPDATE_DATA_SHARING":
			if (accountType.equals("Patient")) {
				res = true;
			} else {
				res = false;
			}
			break;
		case "ALLOW_ACCOUNT_CREATION": 
			if (accountType.equals("Superadmin")) {
				res = true;
			} else if (accountType.equals("Doctor")) {
				res = json.getString("userType").equals("Patient");
			} else {
				res = false;
			}
			break;
			
		case "REQUEST_PATIENT_DATA": //data analyst	
			res = accountType.equals("Data Analyst");
			break;
			
		case "VIEW_RECORD": //doctor or patient
			if (accountType.equals("Patient")) {
				res = json.getString("patientID").equals(callerId);
			} else if (accountType.equals("Doctor")) {
				res = AccountDAO.isDoctorAuthorizedToViewPatientData(conn, json.getString("doctorID"), json.getString("patientID")).getString("result").equals("SUCCESS");
			} else {
				res = false;		
			}
			break;
			
		case "CREATE_RECORD": //doctor
			res = accountType.equals("Doctor") && AccountDAO.isDoctorAuthorizedToViewPatientData(conn, json.getString("doctorID"), json.getString("patientID")).getString("result").equals("SUCCESS");
			break;
			
		case "DEACTIVATE_ACCOUNT": //any user for themselves or admin for everyone
			if (json.getString("type").equals("VALIDATE_ACCOUNT")) {
				res = true;
			}
			if (accountType.equals("Superadmin")) {
				res = true;
			} else {
				res = json.getString("callerId").equals(callerId);
			}
			break;
			
		case "SEARCH_ACCOUNT": //super admin
			res = accountType.equals("Superadmin");
			break;
			
		case "LOGIN","PASSWORD_RESET","CREATE_ACCOUNT","UPDATE_ACCOUNT","LOGOUT":
			res = true;
			break;
		default:
			res = false;	
			break;
		}
		
		String logMessage = String.format("request:%s caller_id:%s result:%s reason:%s\n", json.getString("request"),
				callerId, res ? "SUCCESS" : "FAILURE", "");
		log(clientSocket, logMessage);
		return res;
	}
	
	private static void log(SSLSocket clientSocket, String message) {
		Logger.log(AUTHORIZATION_LOG_FILE_PATH, clientSocket.getInetAddress().getHostAddress(), message);
	}

}
