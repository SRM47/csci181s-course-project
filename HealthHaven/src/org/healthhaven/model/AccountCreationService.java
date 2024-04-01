package org.healthhaven.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class AccountCreationService {
	
	public static String createUser(String accountType, String email, String password, String firstName, String lastName, String address, LocalDate dob) {
//		if (!doesAccountExist(email).equals("VALID")) {
//			return "Account creation failed!";
//		}
		
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
            case DOCTOR -> new Doctor(email, password, legal_first_name, legal_last_name, address, dob);
            case PATIENT -> new Patient(email, password, legal_first_name, legal_last_name, address, dob);
            case DATA_ANALYST -> new DataAnalyst(email, password, legal_first_name, legal_last_name, address, dob);
            case DPO -> new DataProtectionOfficer(email, password, legal_first_name, legal_last_name, address, dob);
            case SUPERADMIN -> new Superadmin(email, password, legal_first_name, legal_last_name, address, dob);
            default -> null;
        };
        
        String serverResponse = insertNewAccountIntoDB(userType, newUser.getUserID(), email, password, legal_first_name, legal_last_name, address,
        		dob);
        
        return serverResponse;
    }

//    protected static String doesAccountExist(String email){
//        String message = String.format("EXISTING_ACCOUNT %s", email);
//        // System.out.println("Message: " + message);
//        String serverResponse = ServerCommunicator.communicateWithAccountServer(message);
//        // System.out.println("Server response: " + serverResponse);
//
//        return serverResponse;
//    }
    

    protected static String insertNewAccountIntoDB(User.Account userType, long userId, String email, String password, String first_name, String last_name, String address, LocalDate dob){
        Instant timestamp = Instant.now();
        String account = userType.getAccountName();
        String message = String.format(("CREATE_ACCOUNT %d %s %s %s %s %s %s %s %s"), userId, email, password, first_name, last_name, address, dob, timestamp.toString(), account);
        return ServerCommunicator.communicateWithAccountServer(message);
    }
    
    



}
