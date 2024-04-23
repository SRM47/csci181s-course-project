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

import org.healthhaven.model.EmailSender;
import org.healthhaven.model.SaltyHash;
import org.healthhaven.model.TOTP;
import org.healthhaven.model.UserIdGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

public class AccountDAO {
	public static JSONObject createTemporaryUser(Connection conn, String userId, String email, String password,
			String dob, String accountType) {
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
			sql = "INSERT INTO healthhaven.authentication (userid, email, password, reset, salt, hashpass) VALUES (?, ?, ?, ?, ?, ?)";
			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, email);
			stmt.setString(3, password); // TODO: Remove this at some point.
			stmt.setBoolean(4, false);
			String salt = SaltyHash.genSalt();
			stmt.setString(5, salt);
			
			try {
				stmt.setString(6, SaltyHash.pwHash(password, salt));
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

	public static JSONObject updateTemporaryUserAfterFirstLogin(Connection conn, String legalfirstname,
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
			// Step 3: Update users table
			// Step 5: Update authentication table (with password update)
			String usersUpdateSql = "UPDATE healthhaven.users SET legalfirstname = ?, legallastname = ?, address = ? WHERE userid = ?";
			String authenticationUpdateSql = "UPDATE healthhaven.authentication SET password = ?, totp_key = ?, reset = ?, salt = ?, hashpass=? WHERE userid = ?";
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
		serverResponse.put("result", result);
		serverResponse.put("reason", reason);
		return serverResponse;
	}

	public static JSONObject updateUserAddress(Connection conn, String address, String userId) {
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

	public static JSONObject viewUserInformation(Connection conn, String doctorId, String userId) {
		JSONObject serverResponse = new JSONObject();
		if (!accountExistsById(conn, userId)) {
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "User does not exist!");
			return serverResponse;
		}

//		TODO: check that PatientUserID is the right way
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
					serverResponse.put("result", "FAILURE");
					serverResponse.put("reason", "No records found for the given user ID.");
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

	public static JSONObject newMedicalInformation(Connection conn, String patientId, String doctorId, float height,
			float weight, String timestamp) {
		JSONObject response = new JSONObject();
		if (!accountExistsById(conn, patientId) || !accountExistsById(conn, doctorId)) {
			response.put("result", "FAILURE");
			response.put("reason", "User doe not exist!");
			return response;
		}

//	    TODO: check that PatientUserID is the right way
		String insertSQL = "INSERT INTO healthhaven.medical_information (entryid, patientid, doctorid, timestamp, height, weight) VALUES (?, ?, ?, ?, ?, ?)";

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

	public static JSONObject updatePassword(Connection conn, String newPassword, String email) {
		JSONObject serverResponse = new JSONObject(); 
		String result = "SUCCESS";
		String reason = "";
		if (!accountExistsByEmail(conn, email)) {
			result = "FAILURE";
			reason = "Account does not exist";
		}
		String authenticationUpdateSql = "UPDATE healthhaven.authentication SET password = ?, salt = ?, hashpass = ? WHERE userid = ?";
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
			// IMPORTANT: Replace with your password hashing mechanism
			String hashedPassword = password;
			
			stmt.setString(1, hashedPassword);
			
			String salt = SaltyHash.genSalt();
			stmt.setString(2, salt);
			
			try {
				String hashpass = SaltyHash.pwHash(password, salt);
				stmt.setString(3, hashpass);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			stmt.setString(4, userId);

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
			stmt.setString(1, hashedPassword);
			stmt.setString(2, totp_key);
			stmt.setBoolean(3, reset);
			String salt = SaltyHash.genSalt();
			stmt.setString(4, salt);

			String hashpass;
			try {
				hashpass = SaltyHash.pwHash(password, salt);
				stmt.setString(5,hashpass);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stmt.setString(6, userId);
			

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
					String truePassword = data_rs.getString("password");
					
					String trueHash = data_rs.getString("hashpass");
					String trueSalt = data_rs.getString("salt");
					
					Boolean hashCheck = SaltyHash.checkPassword(candidatePassword, trueSalt, trueHash);

					// TODO: Hash this password.
					String hashedCandidatePassword = candidatePassword;
					if (!hashCheck) {
						result = "FAILURE";
						reason = "Incorrect Password";
					} else { //password matches
						if (type.equals("ACCOUNT_DEACTIVATION")){
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

	private static void sendTOTPEmail(String email, String key) {
		EmailSender.sendTOTPEmail(email, key);
	}

	public static JSONObject authenticateOTP(Connection conn, String email, String otp) {
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";

		// Returns the userId if user is authenticated correctly
		String sql = "SELECT * FROM healthhaven.authentication WHERE email = '" + email + "'";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet data_rs = stmt.executeQuery();

			if (data_rs.next()) {
				String totp_key = data_rs.getString("totp_key");
				System.out.println(totp_key);
				if (TOTP.verTOTP(totp_key, otp)) {
					// Upon success, return all user information and create cookie to store login information
					String userId = data_rs.getString("userid");
					
					JSONObject userInformation = UserDAO.getUserInformation(conn, userId);
//					userInformation.put("cookie", userCookie);
					return userInformation;
				} else {
					result = "FAILURE";
					reason = "Incorrect OTP";
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
	
	public static String generateAndUpdateNewUserCookie(Connection conn, String userId) {
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
		
		String sql = "SELECT user_cookie FROM healthhaven.cookie WHERE userid = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        
	        ResultSet data_rs = pstmt.executeQuery();
	        if (data_rs.next()) {
				String cookie = data_rs.getString("user_cookie");
				if (cookie == null || !candidateCookie.equals(cookie)) {
					return returnFailureResponse("Incorrect Authentication Cookie");
				}
			} else {
				return returnFailureResponse("Account does not exist");
			}

	    } catch (SQLException e) {
			return returnFailureResponse(e.getMessage());
	    }
	    
		return returnSuccessResponse("Successfully authentiated");	
	}
	
	public static boolean isCookieValid(Connection conn, String userId, String cookie) {
	    String sql = "SELECT user_cookie, timestamp FROM healthhaven.cookie WHERE userid = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            String storedCookie = rs.getString("user_cookie");
	            Timestamp timestamp = rs.getTimestamp("timestamp");
	            
	            // Check if the cookie matches and verify its expiry
	            if (storedCookie != null && storedCookie.equals(cookie) && timestamp != null) {
	                long currentTimeMillis = System.currentTimeMillis();
	                long cookieTimeMillis = timestamp.getTime();
	                long expiryTimeMillis = 10 * 60 * 1000; // 10 min in milliseconds
	                
	                if ((currentTimeMillis - cookieTimeMillis) < expiryTimeMillis) {
	                    return true;
	                }
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error checking cookie validity: " + e.getMessage());
	    }
	    return false;
	}
	
	public static boolean updateCookieTimestamp(Connection conn, String userId) {
	    String sql = "UPDATE healthhaven.cookie SET timestamp = NOW() WHERE userid = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        int affectedRows = pstmt.executeUpdate();
	        if (affectedRows > 0) {
	            return true;
	        }
	    } catch (SQLException e) {
	        System.err.println("Error updating cookie timestamp: " + e.getMessage());
	        return false;
	    }
	    return false;
	}


	//TODO: Write SQL query that wipes out data associated with given userId, no need to validate whether userID exists.
	public static JSONObject deactivateAccount(Connection conn, String userId) {
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
	
	public static JSONObject getMedicalInformationDataByQuery(Connection conn, String when, String date) {
	    JSONObject serverResponse = new JSONObject();
	    String result = "SUCCESS";
	    String reason = "";
	    
	    String sql = "SELECT * FROM healthhaven.medical_information WHERE CAST(timestamp AS DATE) " + when + " DATE '" + date + "'";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        System.out.println(pstmt.toString());
	        ResultSet data_rs = pstmt.executeQuery();

	        // Create an array to store the entries
	        JSONArray entriesArray = new JSONArray();

	        while (data_rs.next()) {
	            float height = data_rs.getFloat("height");
	            float weight = data_rs.getFloat("weight");
	            java.util.Date entryDate = data_rs.getDate("timestamp");

	            // Create a JSON object for each entry
	            JSONObject entry = new JSONObject();
	            entry.put("height", height);
	            entry.put("weight", weight);
	            entry.put("entryDate", entryDate.toString()); // Or format the date as needed

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
	

	public static JSONObject logoutUser(Connection conn, String userId) {
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
