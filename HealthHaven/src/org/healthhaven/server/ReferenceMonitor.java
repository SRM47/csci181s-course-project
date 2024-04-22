/**
 * 
 */
package org.healthhaven.server;

import org.json.JSONObject;

/**
 * 
 */
public class ReferenceMonitor {
	
	protected static boolean authorizeRequest(String accountType, String callerId, JSONObject json) {
		switch (json.getString("request")) {
		case "ALLOW_ACCOUNT_CREATION": 
			if (accountType.equals("Superadmin")) {
				return true;
			} else if (accountType.equals("Doctor")) {
				return json.getString("userType").equals("Patient");
			} else {
				return false;
			}
			
		case "REQUEST_PATIENT_DATA": //data analyst	
			return accountType.equals("Data Analyst");
			
		case "VIEW_RECORD": //doctor or patient
			if (accountType.equals("Patient")) {
				return json.getString("patientID").equals(callerId);
			} else if (accountType.equals("Doctor")) {
				return true;
			} else {
				return false;		
			}
			
		case "CREATE_RECORD": //doctor
			return accountType.equals("Doctor");
			
		case "DEACTIVATE_ACCOUNT": //any user for themselves or admin for everyone
			if (json.getString("type").equals("VALIDATE_ACCOUNT")) {
				return true;
			}
			if (accountType.equals("Superadmin")) {
				return true;
			} else {
				return json.getString("userId").equals(callerId);
			}
			
		case "SEARCH_ACCOUNT": //super admin
			return accountType.equals("Superadmin");
		default:
			return false;	
		}
	}

}
