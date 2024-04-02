package org.healthhaven.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import org.json.JSONObject;

import org.healthhaven.server.ServerCommunicator;

public class AccountCreationService {
	
	public static String createUser(String accountType, String email, String password, String firstName, String lastName, String address, LocalDate dob) {
		
		User.Account userType = getUserType(accountType);
		
		return(createUserInstance(userType, email, password, firstName, lastName, address, dob));
	}
	
	private static User.Account getUserType(String accountType){
		return switch (accountType.toUpperCase()) {
		case "PATIENT" -> User.Account.PATIENT;
		case "DOCTOR" -> User.Account.DOCTOR;
		case "DATA_ANALYST" -> User.Account.DATA_ANALYST;
		case "DPO" -> User.Account.DPO;
		case "SUPERADMIN" -> User.Account.SUPERADMIN;
		default -> User.Account.NONE;
		};
	}

    private static String createUserInstance(User.Account userType, String email, String password, String legal_first_name, String legal_last_name, String address,
                                           LocalDate dob) {


        User newUser = switch (userType) {
            case DOCTOR -> new Doctor(email, legal_first_name, legal_last_name, address, dob);
            case PATIENT -> new Patient(email, legal_first_name, legal_last_name, address, dob);
            case DATA_ANALYST -> new DataAnalyst(email, legal_first_name, legal_last_name, address, dob);
            case DPO -> new DataProtectionOfficer(email, legal_first_name, legal_last_name, address, dob);
            case SUPERADMIN -> new Superadmin(email, legal_first_name, legal_last_name, address, dob);
            default -> null;
        };
        
        return(insertNewAccountIntoDB(userType, newUser.getUserID(), email, password, legal_first_name, legal_last_name, address,
        		dob));
    }

    

    protected static String insertNewAccountIntoDB(User.Account userType, long userId, String email, String password, String first_name, String last_name, String address, LocalDate dob){
        Instant timestamp = Instant.now();
        String account = userType.getAccountName();
 
     // Create a JSONObject and populate it with account data
        JSONObject json = new JSONObject();
        json.put("request", "CREATE_ACCOUNT");
        json.put("userId", userId);
        json.put("email", email);
        json.put("password", password);
        json.put("first_name", first_name);
        json.put("last_name", last_name);
        json.put("address", address);
        json.put("dob", dob.toString()); // Converting LocalDate to String
        json.put("timestamp", timestamp.toString()); // Converting Instant to String
        json.put("accountType", userType.getAccountName());
       
        return ServerCommunicator.communicateWithServer(json.toString());

    }
    
    



}
