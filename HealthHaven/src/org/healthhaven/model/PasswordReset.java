package org.healthhaven.model;

import java.time.Instant;

import org.healthhaven.server.ServerCommunicator;
import org.json.JSONObject;

public class PasswordReset{
	public static String verifyEmail(String email) {
		Instant timestamp = Instant.now();
		JSONObject json = new JSONObject();
	    json.put("request", "PASSWORD_RESET");
	    json.put("email", email);
	    json.put("timestamp", timestamp.toString());
	    
	 	return(ServerCommunicator.communicateWithServer(json.toString()));

		
	} 
	
	public static String confirmOTP(String email, String otpInput) {
		Instant timestamp = Instant.now();
		JSONObject json = new JSONObject();
	    json.put("request", "PASSWORD_RESET");
	    json.put("type", "OTP");
	    json.put("email", email);
	    json.put("timestamp", timestamp.toString());

	 	return(ServerCommunicator.communicateWithServer(json.toString()));

	}
	
	public static String updatePassword(String email, String password) {
		Instant timestamp = Instant.now();
		JSONObject json = new JSONObject();
		
		json.put("request", "PASSWORD_UPDATE");
		json.put("email", email);
		json.put("timestamp", timestamp.toString());
		
		return(ServerCommunicator.communicateWithServer(json.toString()));
	}
	
}