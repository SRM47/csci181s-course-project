package org.healthhaven.db.models;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.healthhaven.model.AttemptLimit;
import org.healthhaven.model.EmailSender;
import org.healthhaven.model.SaltyHash;
import org.healthhaven.model.TOTP;
import org.healthhaven.model.UserIdGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

public class AccountDAO {
	public static synchronized JSONObject createTemporaryUser(Connection conn, String userId, String email, String password,
			String dob, String accountType, String callerId) {
		int rowsInserted = 0;
		// Set the 'dob' column to the dob to verify later
		// Convert the String dob into a java.sql.Date
		java.util.Date utilDate;
		java.sql.Date sqlDate = java.sql.Date.valueOf(dob);
		try {
			utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
			sqlDate = new java.sql.Date(utilDate.getTime());

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return returnFailureResponse("Unable to parse DOB");
		}
		
		String sql;
		
		try {
			// Insert into users table.
			sql = "INSERT INTO healthhaven.users (userid, dob) VALUES (?, ?)";
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setDate(2, sqlDate);
			
			rowsInserted = stmt.executeUpdate();
			if (rowsInserted <= 0) {
				// Rollback because nothing updated and this is an error.
				return returnFailureResponse(conn, "Unable to create entry in user's table.");
			}
			
			// Insert into accounts
			sql = "INSERT INTO healthhaven.accounts (userid, accounttype) VALUES (?, ?)";
			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, accountType);

			rowsInserted = stmt.executeUpdate();
			if (rowsInserted <= 0) {
				return returnFailureResponse(conn, "Unable to create entry in accounttype table.");
			}
			
			// Insert into authentication table
			sql = "INSERT INTO healthhaven.authentication (userid, email, reset, salt, hashpass) VALUES (?, ?, ?, ?, ?)";
			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, email);
			// stmt.setString(3, password); // TODO: Remove this at some point.
			stmt.setBoolean(3, false);
			String salt = SaltyHash.genSalt();
			stmt.setString(4, salt);
			
			try {
				stmt.setString(5, SaltyHash.pwHash(password, salt));
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				return returnFailureResponse(conn, e.getMessage());
			}
			
			rowsInserted = stmt.executeUpdate();
			if (rowsInserted <= 0) {
				return returnFailureResponse(conn, "Unable to create entry in authentication table.");
			}
			
			// Insert into cookie table.
		    sql = "INSERT INTO healthhaven.cookie (userid, user_cookie, timestamp) VALUES (?, ?, NOW())";
		    
		    stmt = conn.prepareStatement(sql);
		    stmt.setString(1, userId);
	        stmt.setString(2, null);
	        
	        rowsInserted = stmt.executeUpdate();
			if (rowsInserted <= 0) {
				return returnFailureResponse(conn, "Unable to create entry in cookie table.");
			}
			
			// If the user type is a patient, then update the medical_map with the doctor id and patient id
		    sql = "INSERT INTO healthhaven.medical_map (doctorid, patientid) VALUES (?, ?)";
		    
		    stmt = conn.prepareStatement(sql);
		    stmt.setString(1, callerId);
	        stmt.setString(2, userId);
	        
	        rowsInserted = stmt.executeUpdate();
			if (rowsInserted <= 0) {
				return returnFailureResponse(conn, "Unable to update medical mapping database");
			}
			
			// Commit everything at once.
			conn.commit();
			
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				return returnFailureResponse(e1.getMessage());
			}
			return returnFailureResponse(e.getMessage());
		}
		
		// Return success response.
		return returnSuccessResponse("");
	}

	public static synchronized JSONObject updateTemporaryUserAfterFirstLogin(Connection conn, String legalfirstname,
			String legallastname, String dob, String address, String email, String password, String accountType){
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";
		// Step 1: Get userId from authentication table based on email
		String userId = getUserIdFromEmail(conn, email);
		if (userId == null) {
			result = "FAILURE";
			reason = "User does not exist";
		} else if (!verifyDOB(conn, userId, dob)) {
			// Step 1.5: Verify DOB
			result = "FAILURE";
			reason = "Error verifying the DOB";

		} else {

			String sql = "SELECT * FROM healthhaven.authentication WHERE email = ?";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, email);
				ResultSet data_rs = stmt.executeQuery();
				boolean resetValue = false;
				if (data_rs.next()) {
					resetValue = data_rs.getBoolean("reset");
				}
				
				if (resetValue) {
					result = "FAILURE";
					reason = "Account already exist";
				} else {
					// Step 3: Update users table
					// Step 5: Update authentication table (with password update)
					String usersUpdateSql = "UPDATE healthhaven.users SET legalfirstname = ?, legallastname = ?, address = ?, data_sharing = TRUE WHERE userid = ?";
					String authenticationUpdateSql = "UPDATE healthhaven.authentication SET totp_key = ?, reset = ?, salt = ?, hashpass=? WHERE userid = ?";
					if (!updateUserTable(conn, usersUpdateSql, legalfirstname, legallastname, address, userId)
							|| !updateAuthenticationTable(conn, authenticationUpdateSql, password, true, TOTP.genSecretKey(),
									userId)) {
						result = "FAILURE";
						reason = "Database Entry Error";
					} else {
						try {
							conn.commit();
						} catch (SQLException e) {
							try {
								conn.rollback();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							e.printStackTrace();
						}
					}
					
				}
				
			} catch (SQLException e) {
				result = "FAILURE";
				reason = "Error Authenticating User";
				e.printStackTrace();
			}
			
		}
		serverResponse.put("result", result);
		serverResponse.put("reason", reason);
		return serverResponse;
	}

	public static synchronized JSONObject updateUserAddress(Connection conn, String address, String userId) {
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";

		if (!accountExistsById(conn, userId)) {
			result = "FAILURE";
			reason = "Account does not exist";
		} else {
			String usersUpdateSql = "UPDATE healthhaven.users SET address = ? WHERE userid = ?";
			try (PreparedStatement stmt = conn.prepareStatement(usersUpdateSql)) {
				stmt.setString(1, address);
				stmt.setString(2, userId);

				int rowsUpdated = stmt.executeUpdate();
				if (rowsUpdated <= 0) {
					result = "FAILURE";
					reason = "Database entry error"; // TODO: need better reason
				}
				conn.commit();
			} catch (SQLException e) {
				result = "FAILURE";
				reason = e.getMessage();
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		serverResponse.put("result", result);
		serverResponse.put("reason", reason);
		return serverResponse;
	}
	
	public static JSONObject isDoctorAuthorizedToViewPatientData(Connection conn, String doctorId, String patientId) {
		if (doctorId == null || patientId == null) {
			return returnFailureResponse("Must provide both doctor and patient id.");
		}
		String sql = "SELECT COUNT(*) FROM healthhaven.medical_map WHERE doctorid = ? AND patientid = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, doctorId);
			stmt.setString(2, patientId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					if (count > 0) {
						return returnSuccessResponse("Doctor is authorized to view this patient");
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error checking account existence: " + e.getMessage());
			return returnFailureResponse(e.getMessage());
		}

		return returnFailureResponse("Either patient does not exist or doctor is not authorized for this action");
	}

	public static JSONObject viewUserInformation(Connection conn, String doctorId, String userId) {
		JSONObject serverResponse = new JSONObject();
		if (!accountExistsById(conn, userId)) {
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "User does not exist!");
			return serverResponse;
		}
		
		if (!accountCreatedById(conn, userId)) {
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "User exists but has not yet initialized their account!");
			return serverResponse;
		}
		
		String selectSQL = "";
		if (doctorId != null && doctorId != "") {
			selectSQL = "SELECT * FROM healthhaven.medical_information WHERE patientid = ? AND doctorid = ?";
		} else {
			selectSQL = "SELECT * FROM healthhaven.medical_information WHERE patientid = ?";
		}
		

		try (PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
			System.out.println(selectSQL);
			stmt.setString(1, userId);
			if (doctorId != null && doctorId != "") {
				stmt.setString(2, doctorId);
			}
			
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.next()) { // If ResultSet is empty, no records are found for the given IDs.
					serverResponse.put("result", "SUCCESS");
					serverResponse.put("reason", "No records found for the given user ID!");
					return serverResponse;
				}

				// If records are found, process and add them to the response.
				// This example collects all records; adjust according to your needs.
				JSONArray records = new JSONArray();
				do {
					JSONObject record = new JSONObject();
					// TODO check this is the right way to get columns
					record.put("Timestamp", rs.getString("Timestamp"));
					record.put("Height", rs.getFloat("Height"));
					record.put("Weight", rs.getFloat("Weight"));

					records.put(record);
				} while (rs.next());

				serverResponse.put("result", "SUCCESS");
				serverResponse.put("records", records);
			}
		} catch (SQLException e) {
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "SQL error: " + e.getMessage());
		}

		return serverResponse;
	}
	
	//TODO: Used by superadmin
	public static JSONObject viewAccountInformation(Connection conn, String userId) {
		JSONObject serverResponse = new JSONObject();
		if (!accountExistsById(conn, userId)) {
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "User does not exist!");
			return serverResponse;
		}
		//For now, superadmin can see everything
		return UserDAO.getUserInformation(conn, userId);
		
	}

	public static JSONObject getDataAverage(Connection conn) {
		JSONObject serverResponse = new JSONObject();

//		TODO: check query
		String query = "SELECT AVG(height) AS AvgHeight, AVG(weight) AS AvgWeight FROM healthhaven.medical_information";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				float avgHeight = rs.getFloat("AvgHeight");
				float avgWeight = rs.getFloat("AvgWeight");
				serverResponse.put("result", "SUCCESS");
				serverResponse.put("averageHeight", avgHeight);
				serverResponse.put("averageWeight", avgWeight);
			} else {
				// Handle unexpected case where query executed but no row is returned
				serverResponse.put("result", "FAILURE");
				serverResponse.put("reason", "Failed to calculate averages");
			}
		} catch (SQLException e) {
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "SQL error: " + e.getMessage());
		}

		return serverResponse;
	}

	public static synchronized JSONObject newMedicalInformation(Connection conn, String patientId, String doctorId, float height,
			float weight, String timestamp) {
		JSONObject response = new JSONObject();
		if (!accountExistsById(conn, patientId) || !accountExistsById(conn, doctorId)) {
			response.put("result", "FAILURE");
			response.put("reason", "User doe not exist!");
			return response;
		}
		
		boolean data_sharing = UserDAO.getDataSharingSetting(conn, patientId);
//	    TODO: check that PatientUserID is the right way
		String insertSQL = "INSERT INTO healthhaven.medical_information (entryid, patientid, doctorid, timestamp, height, weight, data_sharing) VALUES (?, ?, ?, ?, ?, ?,?)";

		try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
			UserIdGenerator g = new UserIdGenerator(32);
			stmt.setString(1, g.generate());
			stmt.setString(2, patientId);
			stmt.setString(3, doctorId);
			// Not sure of timestamp format so gonna assume string
//	        Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);
			Instant instant = Instant.parse(timestamp);
			stmt.setTimestamp(4, Timestamp.from(instant));
			stmt.setFloat(5, height);
			stmt.setFloat(6, weight);
			stmt.setBoolean(7, data_sharing);

			// Execute the insert operation
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				response.put("result", "SUCCESS");
				// Instead of "reason", you could use a key like "details" for clarity
				response.put("details", String.format("Height: %s, Weight: %s", height, weight));
			} else {
				response.put("result", "FAILURE");
				response.put("reason", "No rows affected");
				conn.rollback();
			}
			
			conn.commit();
		} catch (SQLException e) {
			response.put("result", "FAILURE");
			response.put("reason", e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IllegalArgumentException e) {
			response.put("result", "FAILURE");
			response.put("reason", "Invalid timestamp format: " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return response;
	}
	
	
	public static synchronized JSONObject updateDataSharingSetting(Connection conn, String callerId, boolean data_sharing) {
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";

		if (!accountExistsById(conn, callerId)) {
			result = "FAILURE";
			reason = "Account does not exist";
		} else {
			String usersUpdateSql = "UPDATE healthhaven.users SET data_sharing = ? WHERE userid = ?";
			String medicalInfoUpdateSql = "UPDATE healthhaven.medical_information SET data_sharing = ? WHERE patientid = ?";
			if (!updateDataSharingOnUserTable(conn, usersUpdateSql, callerId, data_sharing)
					|| !udpateDataSharingOnMedicalTable(conn, medicalInfoUpdateSql, callerId, data_sharing)) {
				result = "FAILURE";
				reason = "Database Entry Error";
			} else {
				try {
					conn.commit();
				} catch (SQLException e) {
					try {
						conn.rollback();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}

		serverResponse.put("result", result);
		serverResponse.put("reason", reason);
		return serverResponse;

	}

	private static boolean updateUserTable(Connection conn, String sql, String legalfirstname, String legallastname,
			String address, String userId) {
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, legalfirstname);
			stmt.setString(2, legallastname);
			stmt.setString(3, address);
			stmt.setString(4, userId);

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected <= 0) {
				conn.rollback();
			}
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.err.println("Error during table update: " + e.getMessage());
			return false;
		}
	}
	
	private static boolean updateDataSharingOnUserTable(Connection conn, String sql, String userId, boolean data_sharing) {
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setBoolean(1, data_sharing);
			stmt.setString(2, userId);
			

			int rowsAffected = stmt.executeUpdate();
			System.out.println("Update made to the number of user row: " + rowsAffected);
			if (rowsAffected <= 0) {
				conn.rollback();
			}
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.err.println("Error during table update: " + e.getMessage());
			return false;
		}
	}
	
	private static boolean udpateDataSharingOnMedicalTable(Connection conn, String sql, String userId, boolean data_sharing) {
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setBoolean(1, data_sharing);
			stmt.setString(2, userId);
			

			int rowsAffected = stmt.executeUpdate();
			System.out.println("Update made to the number of medical table row: " + rowsAffected);
			if (rowsAffected < 0) {
				conn.rollback();
			}
			return rowsAffected >= 0;
		} catch (SQLException e) {
			System.err.println("Error during table update: " + e.getMessage());
			return false;
		}
	}

	public static synchronized JSONObject updatePassword(Connection conn, String newPassword, String email) {
		JSONObject serverResponse = new JSONObject(); 
		String result = "SUCCESS";
		String reason = "";
		if (!accountExistsByEmail(conn, email)) {
			result = "FAILURE";
			reason = "Account does not exist";
		}
		String authenticationUpdateSql = "UPDATE healthhaven.authentication SET salt = ?, hashpass = ? WHERE userid = ?";
		if (!updateAuthenticationTable(conn, authenticationUpdateSql, newPassword, getUserIdFromEmail(conn, email))) {
			result = "FAILURE";
			reason = "Database Entry Error";
		} else {
			try {
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		serverResponse.put("result", result);
		serverResponse.put("reason", reason);
		return serverResponse;

	}

	private static boolean updateAuthenticationTable(Connection conn, String sql, String password, String userId) {
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			String salt = SaltyHash.genSalt();
			stmt.setString(1, salt);
			
			try {
				String hashpass = SaltyHash.pwHash(password, salt);
				stmt.setString(2, hashpass);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			stmt.setString(3, userId);

			int rowsUpdated = stmt.executeUpdate();
			if (rowsUpdated <= 0) {
				conn.rollback();
			}
			return rowsUpdated > 0;
		} catch (SQLException e) {
			System.err.println("Error updating authentication: " + e.getMessage());
			return false;
		}
	}

	private static boolean updateAuthenticationTable(Connection conn, String sql, String password, boolean reset,
			String totp_key, String userId) {
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			// IMPORTANT: Replace with your password hashing mechanism
			String hashedPassword = password;
			stmt.setString(1, totp_key);
			stmt.setBoolean(2, reset);
			String salt = SaltyHash.genSalt();
			stmt.setString(3, salt);

			String hashpass;
			try {
				hashpass = SaltyHash.pwHash(password, salt);
				stmt.setString(4,hashpass);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stmt.setString(5, userId);
			

			int rowsUpdated = stmt.executeUpdate();
			if (rowsUpdated <= 0) {
				conn.rollback();
			}
			return rowsUpdated > 0;
		} catch (SQLException e) {
			System.err.println("Error updating authentication: " + e.getMessage());
			return false;
		}
	}

	private static boolean verifyDOB(Connection conn, String userId, String providedDob) {

		// Get existing DOB from database
		String sql = "SELECT dob FROM healthhaven.users WHERE userId = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					// Step 2: Prepare date of birth (DOB)
					System.out.println(providedDob);
					java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(providedDob);
					java.util.Date actualDob = rs.getDate("dob");
					System.out.println(date);
					System.out.println(actualDob);
					return actualDob.compareTo(date) == 0;
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching existing DOB: " + e.getMessage());
		} catch (ParseException e) {
			System.err.println("Error parsing provided DOB");
		}
		return false;
	}
	
	public static JSONObject verifyUserIdfromEmail(Connection conn, String userId, String email) {
		if (userId == null || email == null) {
			return returnFailureResponse("Please provide a userId and email");
		}
		if (userId.equals(getUserIdFromEmail(conn, email))) {
			return returnSuccessResponse("");
		}
		return returnFailureResponse("Unable to match email and userId");
	}

	private static String getUserIdFromEmail(Connection conn, String email) {
		String sql = "SELECT userid FROM healthhaven.authentication WHERE email = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("userid");
				} else {
					return null; // Email not found
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching userId: " + e.getMessage());
			return null;
		}
	}

	public static boolean accountExistsById(Connection conn, String userId) {
		String sql = "SELECT COUNT(*) FROM healthhaven.authentication WHERE userid = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					return count > 0; // True if there's at least one row with this userid
				}
			}
		} catch (SQLException e) {
			System.err.println("Error checking account existence: " + e.getMessage());
			return false; // Or potentially throw an exception instead
		}

		return false; // Default to account not existing if an error occurs or no match is found
	}
	
	public static boolean accountCreatedById(Connection conn, String userId) {
		String sql = "SELECT COUNT(*) FROM healthhaven.authentication WHERE userid = ? AND reset = true";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					return count > 0; // True if there's at least one row with this userid
				}
			}
		} catch (SQLException e) {
			System.err.println("Error checking account existence: " + e.getMessage());
			return false; // Or potentially throw an exception instead
		}

		return false; // Default to account not existing if an error occurs or no match is found
	}

	public static boolean accountExistsByEmail(Connection conn, String email) {
		// TODO: Need to give back userID if successful
		String sql = "SELECT COUNT(*) FROM healthhaven.authentication WHERE email = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					return count > 0; // True if there's at least one row with this email
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Ideally, you'd want to handle this exception more gracefully
		}

		return false; // Default to account not existing if an error occurs or no match is found
	}

	public static JSONObject authenticateUser(Connection conn, String email, String candidatePassword, String type) {
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";
		String userType = "";
		
		//TODO: SQL Injection Attack risk.
		
		// Returns the userId if user is authenticated correctly
		String sql = "SELECT * FROM healthhaven.authentication WHERE email = '" + email + "'";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet data_rs = stmt.executeQuery();
			System.out.println("Statement Executed");

			if (data_rs.next()) {
				if (type.equals("PASSWORD_RESET")) {
					sendTOTPEmail(email, TOTP.genTOTP2(data_rs.getString("totp_key")));
				} else {
					
					String trueHash = data_rs.getString("hashpass");
					String trueSalt = data_rs.getString("salt");
					
					Boolean hashCheck = SaltyHash.checkPassword(candidatePassword, trueSalt, trueHash);

					// TODO: Hash this password.
					String hashedCandidatePassword = candidatePassword;
					
					JSONObject Attempts = limitProcess(conn, email, 2);
					
					String allowAttempt = Attempts.getString("result");
					System.out.println(allowAttempt);
					
					if(allowAttempt.equals("FAILURE")) {
						result = "FAILURE";
						reason = "Max Attempts";
					} else if (!hashCheck) {
						result = "FAILURE";
						reason = "Incorrect Password";
						limitProcess(conn, email, 1);
						
					} else { //password matches
						if  (type.equals("ACCOUNT_DEACTIVATION")){
							serverResponse.put("result", result);
							return serverResponse;
						}
						boolean resetValue = data_rs.getBoolean("reset");
						if (!resetValue) {
							result = "SUCCESS";
							reason = "NEW";
							userType = UserDAO.getUserAccountType(conn, getUserIdFromEmail(conn, email));
							
						} else {
							result = "SUCCESS";
							reason = "EXISTING";
							limitProcess(conn, email, 0);
							sendTOTPEmail(email, TOTP.genTOTP2(data_rs.getString("totp_key")));
						}

					}
				}
			} else {
				result = "FAILURE";
				reason = "Account does not exist";
			}

		} catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			result = "FAILURE";
			reason = "Error Authenticating User";
		}

		serverResponse.put("result", result);
		serverResponse.put("reason", reason);
		serverResponse.put("type" , reason);
		serverResponse.put("userType", userType);
		System.out.println(serverResponse);
		return serverResponse;
	}
	
	private static JSONObject limitProcess(Connection conn, String email, Integer loginFail) {
		System.out.println("hereagain");
		
		JSONObject serverResponse = new JSONObject();

		if(!accountExistsByEmail(conn, email)) {
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "Account Does Not Exist");
			return serverResponse;
		} else {
			
			String selectSQL = "SELECT attempt_track FROM healthhaven.authentication WHERE email = ?";
			
			try (PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
				System.out.println(selectSQL);
				try {
					stmt.setString(1, email);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try (ResultSet rs = stmt.executeQuery()) {
					if (!rs.next()) { // If ResultSet is empty, no records are found for the given IDs.
						serverResponse.put("result", "FAILURE");
						serverResponse.put("reason", "No records found for the given email.");
						return serverResponse;
					}
					
					String timeString = rs.getString("attempt_track");
//					System.out.println(timeString);
					
					if(timeString==null) {
						timeString = "";
					}
					
					List<Long> timeList = AttemptLimit.StringtotList(timeString);
					
					List<List<Long>> rList = AttemptLimit.withinAttemptLimit(timeList);
					List<Long> uploadList = rList.get(1);
					
					if(loginFail == 1) {
						uploadList = AttemptLimit.addTime(uploadList);
					}
					
					
					
					System.out.print(uploadList);
					String uploadString = AttemptLimit.tListtoString(uploadList);
					
					System.out.print(uploadString);
					Long allowAttempt = rList.get(0).get(0);
//					System.out.println(allowAttempt);
					
					if(loginFail == 0 && allowAttempt==1) {
						uploadString = "";
					}
					
					String usersUpdateSql = "UPDATE healthhaven.authentication SET attempt_track = ? WHERE email = ?";
					
					try (PreparedStatement stmt2 = conn.prepareStatement(usersUpdateSql)) {
						stmt2.setString(1, uploadString);
						stmt2.setString(2, email);
					
						int rowsUpdated = stmt2.executeUpdate();
						if (rowsUpdated <= 0) {
							serverResponse.put("result", "FAILURE");
							serverResponse.put("reason", "Database entry error");
							return serverResponse;
						}
						conn.commit();
						
					} catch (SQLException e) {
						serverResponse.put("result", "FAILURE");
						serverResponse.put("reason", e.getMessage());					
						try {
							conn.rollback();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						return serverResponse;
					}
						
					if(allowAttempt == 1) {
						serverResponse.put("result", "SUCCESS");
						serverResponse.put("reason", "Within Attempts");
						return serverResponse;
					
					} else if (allowAttempt == 0) {
						serverResponse.put("result", "FAILURE");
						serverResponse.put("reason", "Max Attempts");
						return serverResponse;
					} else {
						serverResponse.put("result", "FAILURE");
						serverResponse.put("reason", "Something Happened");
						return serverResponse;
					}
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					serverResponse.put("result", "FAILURE");
					serverResponse.put("reason", "Server Error");
					return serverResponse;

				}
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				serverResponse.put("result", "FAILURE");
				serverResponse.put("reason", "Server Error");
				return serverResponse;
			}
		}
	}
	
	private static void sendTOTPEmail(String email, String key) {
		EmailSender.sendTOTPEmail(email, key);
	}

	public static JSONObject authenticateOTP(Connection conn, String email, String otp) {
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";

		// Returns the userId if user is authenticated correctly
		String sql = "SELECT * FROM healthhaven.authentication WHERE email = '" + email + "'";
		
		JSONObject Attempts = limitProcess(conn, email, 2);
		String allowAttempt = Attempts.getString("result");
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet data_rs = stmt.executeQuery();

			if(allowAttempt.equals("FAILURE")) {
				result = "FAILURE";
				reason = "Max Attempts"; 
			} else if (data_rs.next()) {
				String totp_key = data_rs.getString("totp_key");
				System.out.println(totp_key);
				if (TOTP.verTOTP(totp_key, otp)) {
					// Upon success, return all user information and create cookie to store login information
					String userId = data_rs.getString("userid");
					
					JSONObject userInformation = UserDAO.getUserInformation(conn, userId);
					limitProcess(conn, email, 0);
//					userInformation.put("cookie", userCookie);
					return userInformation;
				} else {
					result = "FAILURE";
					reason = "Incorrect OTP";
					limitProcess(conn, email, 1);
				}

			} else {
				result = "FAILURE";
				reason = "Account does not exist";
			}

		} catch (SQLException e) {
			result = "FAILURE";
			reason = "Error Authenticating User";
		}

		serverResponse.put("result", result);
		serverResponse.put("type" , reason);
		serverResponse.put("reason", reason);
		System.out.println(serverResponse);
		return serverResponse;
		
	}
	
	public static synchronized String generateAndUpdateNewUserCookie(Connection conn, String userId) {
		// Cookie Generation
	    SecureRandom random = new SecureRandom();
	    byte[] cookieBytes = new byte[32]; // Example: 32-byte cookie value
	    random.nextBytes(cookieBytes);
	    String cookieValue = Base64.getUrlEncoder().encodeToString(cookieBytes); 

	    // Database Interaction (Assuming a PreparedStatement)
	    String sql = "UPDATE healthhaven.cookie SET user_cookie = ?, timestamp = NOW() WHERE userid = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, cookieValue);
	        pstmt.setString(2, userId);
	        pstmt.executeUpdate();
	        conn.commit();
	    } catch (SQLException e) {
	    	try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
	        return null;
	    }

	    return cookieValue;
		
	}
	
	private static String generateNewUserCookie() {
		// Cookie Generation
	    SecureRandom random = new SecureRandom();
	    byte[] cookieBytes = new byte[32]; // Example: 32-byte cookie value
	    random.nextBytes(cookieBytes);
	    String cookieValue = Base64.getUrlEncoder().encodeToString(cookieBytes); 
	    return cookieValue;
		
	}
	
	public static JSONObject verifyAuthenticationCookieByEmail(Connection conn, String email, String candidateCookie) {
		JSONObject serverResponse = new JSONObject();
		if (email == null || email == "" || candidateCookie == null || candidateCookie == "") {
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "email is null or no cookie");
			System.out.println(serverResponse);
			return serverResponse;
		}
		String userId = getUserIdFromEmail(conn, email);
		if (userId != null) {
			return verifyAuthenticationCookieById(conn, userId, candidateCookie);
		} 
		
		serverResponse.put("result", "FAILURE");
		serverResponse.put("reason", "Email does not exist");
		System.out.println(serverResponse);
		return serverResponse;
		
	}
	
	public static JSONObject verifyAuthenticationCookieById(Connection conn, String userId, String candidateCookie) {
		System.out.println(userId);
		if (userId == null || userId == "" || candidateCookie == null || candidateCookie == "") {
			return returnFailureResponse("Must provide both identification and cookie");
		}
		
		String sql = "SELECT user_cookie, timestamp FROM healthhaven.cookie WHERE userid = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        
	        ResultSet data_rs = pstmt.executeQuery();
	        if (data_rs.next()) {
				String cookie = data_rs.getString("user_cookie");
				
				if (cookie == null || !candidateCookie.equals(cookie)) {
					return returnFailureResponse("Incorrect Authentication Cookie");
				}
				Timestamp timestamp = data_rs.getTimestamp("timestamp");
				
				if ((System.currentTimeMillis() - timestamp.getTime()) >= 600000) {
					return returnFailureResponse("Cookie expired, must log in again.");
                }
				
				
			} else {
				return returnFailureResponse("Account does not exist");
			}

	    } catch (SQLException e) {
			return returnFailureResponse(e.getMessage());
	    }
	    
		return returnSuccessResponse("Successfully authentiated");	
	}
	
//	public static boolean isCookieValid(Connection conn, String userId, String cookie) {
//	    String sql = "SELECT user_cookie, timestamp FROM healthhaven.cookie WHERE userid = ?";
//	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//	        pstmt.setString(1, userId);
//	        ResultSet rs = pstmt.executeQuery();
//	        if (rs.next()) {
//	            String storedCookie = rs.getString("user_cookie");
//	            Timestamp timestamp = rs.getTimestamp("timestamp");
//	            
//	            // Check if the cookie matches and verify its expiry
//	            if (storedCookie != null && storedCookie.equals(cookie) && timestamp != null) {
//	                long currentTimeMillis = System.currentTimeMillis();
//	                long cookieTimeMillis = timestamp.getTime(); 
//	                
//	                if ((currentTimeMillis - cookieTimeMillis) < 600000) {
//	                    return true;
//	                }
//	            }
//	        }
//	    } catch (SQLException e) {
//	        System.err.println("Error checking cookie validity: " + e.getMessage());
//	    }
//	    return false;
//	}
	
	public static synchronized boolean updateCookieTimestamp(Connection conn, String userId) {
	    String sql = "UPDATE healthhaven.cookie SET timestamp = NOW() WHERE userid = ?";
	    System.out.println(sql);
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        int affectedRows = pstmt.executeUpdate();
	        if (affectedRows <= 0) {
	        	conn.rollback();
	        }
	        conn.commit();
	    } catch (SQLException e) {
	    	try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        System.err.println("Error updating cookie timestamp: " + e.getMessage());
	        return false;
	    }
	    return true;
	}


	//TODO: Write SQL query that wipes out data associated with given userId, no need to validate whether userID exists.
	public static synchronized JSONObject deactivateAccount(Connection conn, String userId) {
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";

        
        try {
            deleteUserData(conn, "healthhaven.users", userId);
            deleteMedicalData(conn, userId);
            conn.commit();
            serverResponse.put("result", result);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                serverResponse.put("result", "FAILURE");
                serverResponse.put("reason", "Error rolling back transaction: " + ex.getMessage());
                return serverResponse;
            }
            serverResponse.put("result", "FAILURE");
            serverResponse.put("reason", "SQL error during deletion: " + e.getMessage());
        }
		System.out.println(serverResponse);
		return serverResponse;
	}
	
	private static boolean deleteUserData(Connection conn, String tableName, String userId) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE userid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            int affectedRows = stmt.executeUpdate();
            return (affectedRows > 0);
        }
    }
	
	private static boolean deleteMedicalData(Connection conn, String patientId) throws SQLException {
        String sql = "DELETE FROM healthhaven.medical_information WHERE patientid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patientId);
            int affectedRows = stmt.executeUpdate();
            return (affectedRows > 0);
        }
    }
	
	private static double generateLaplaceNoise(double epsilon, double sensitivity) {
		Random random = new Random();
        double scale = sensitivity / epsilon;
        double u = 0.5 - random.nextDouble();  
        return -scale * Math.signum(u) * Math.log(1 - 2 * Math.abs(u));
    }
	
	public static JSONObject getMedicalInformationDataByQuery(Connection conn, String when, String date) {
		double epsilon = 0.1;  // Privacy parameter
        double sensitivity = 1.0;  // Sensitivity for height and weight
        
        HashMap<String, String> patientIdToRandomizedId = new HashMap<>();
        int counter = 0;
        
	    JSONObject serverResponse = new JSONObject();
	    String result = "SUCCESS";
	    String reason = "";
	    
	    String sql = "SELECT * FROM healthhaven.medical_information WHERE CAST(timestamp AS DATE) " + when + " DATE '" + date + "' AND data_sharing = TRUE";
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        System.out.println(pstmt.toString());
	        ResultSet data_rs = pstmt.executeQuery();

	        // Create an array to store the entries
	        JSONArray entriesArray = new JSONArray();
	        
	        // Create a unique key to hash with to create randomized user identifiers

	        while (data_rs.next()) {
	            float height = data_rs.getFloat("height");
	            float weight = data_rs.getFloat("weight");
	            java.util.Date entryDate = data_rs.getDate("timestamp");
				String patientId = data_rs.getString("patientid");
				
				// Generate noisy height and weight data. Double to float can lose precision but that is OKAY for now.
	            float noisyHeight = height + (float) generateLaplaceNoise(epsilon, sensitivity);
	            float noisyWeight = weight + (float) generateLaplaceNoise(epsilon, sensitivity);
	            
	            // Check if patientID already has a pseudonym; if not, generate one
	            String patientPseudonym = patientIdToRandomizedId.get(patientId);
	            if (patientPseudonym == null) {
	            	patientPseudonym = "P" + String.valueOf(counter++);
	            	patientIdToRandomizedId.put(patientId, patientPseudonym);
	            }

	            // Create a JSON object for each entry
	            JSONObject entry = new JSONObject();
	            entry.put("height", noisyHeight);
	            entry.put("weight", noisyWeight);
	            entry.put("entryDate", entryDate.toString()); // Or format the date as needed
				entry.put("identifier", patientPseudonym);
	            // Add the entry to the array
	            entriesArray.put(entry);
	        }

	        // Add the array to the response
	        serverResponse.put("entries", entriesArray);

	    } catch (SQLException e) {
	        result = "FAILURE";
	        reason = e.getMessage();
	    } 

	    serverResponse.put("result", result);
	    serverResponse.put("reason", reason);

	    return serverResponse;
	}
	

	public static synchronized JSONObject logoutUser(Connection conn, String userId) {
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";
		String sql = "UPDATE healthhaven.cookie SET user_cookie = NULL, timestamp = NOW() WHERE userId = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        pstmt.executeUpdate();
	        conn.commit();
	    } catch (SQLException e) {
	    	try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        result = "FAILURE";
	        reason = "SQL error failed to logout";
	    }
		serverResponse.put("result", result);
        serverResponse.put("reason", reason);
        return serverResponse;
	}
	 
	private static JSONObject returnFailureResponse(String reason) {
		JSONObject serverResponse = new JSONObject();
		serverResponse.put("result", "FAILURE");
		serverResponse.put("reason", reason);
		return serverResponse;	
	}
	
	private static JSONObject returnFailureResponse(Connection conn, String reason) throws SQLException {
		conn.rollback();
		return returnFailureResponse(reason);
	}
	
	private static JSONObject returnSuccessResponse(String reason) {
		JSONObject serverResponse = new JSONObject();
		serverResponse.put("result", "SUCCESS");
		serverResponse.put("reason", reason);
		return serverResponse;	
	}
	

}
