package org.healthhaven.db.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

import org.healthhaven.model.EmailSender;
import org.healthhaven.model.TOTP;
import org.healthhaven.model.UserIdGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

public class AccountDAO {
	public static JSONObject createTemporaryUser(Connection conn, String userId, String email, String password,
			String dob, String accountType) {
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";
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
		}

		// Insert user data into users table
		String sql = "INSERT INTO healthhaven.users (userid, legalfirstname, legallastname, dob, address) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, userId);

			stmt.setString(2, "NONE");

			stmt.setString(3, "NONE");

			stmt.setDate(4, sqlDate);

			stmt.setString(5, "NONE");

			rowsInserted = stmt.executeUpdate();
			if (rowsInserted <= 0) {
				result = "FAILURE";
				reason = "Database entry error";
			}
		} catch (SQLException e) {
			result = "FAILURE";
			reason = e.getMessage();
		}

		// Insert account type into account information.
		sql = "INSERT INTO healthhaven.accounts (userid, accounttype) VALUES (?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, userId);

			stmt.setString(2, accountType);

			rowsInserted = stmt.executeUpdate();
			if (rowsInserted <= 0) {
				result = "FAILURE";
				reason = "Database entry error";
			}
		} catch (SQLException e) {
			result = "FAILURE";
			reason = e.getMessage();
		}

		sql = "INSERT INTO healthhaven.authentication (userid, email, password, reset, totp_key) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, userId);

			stmt.setString(2, email);

			// TODO: Salt and hash this password
			String saltedHashedPassword = password;
			stmt.setString(3, saltedHashedPassword);

			// Set the 'reset' column to false
			stmt.setBoolean(4, false);
			
			// Set the 'reset' column to false
			stmt.setString(5, "NONE");

			rowsInserted = stmt.executeUpdate();
			if (rowsInserted <= 0) {
				result = "FAILURE";
				reason = "Database entry error";
			}
		} catch (SQLException e) {
			result = "FAILURE";
			reason = e.getMessage();
		}

		serverResponse.put("result", result);
		serverResponse.put("reason", reason);
		return serverResponse;
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
			String authenticationUpdateSql = "UPDATE healthhaven.authentication SET password = ?, totp_key = ?, reset = ? WHERE userid = ?";
			if (!updateUserTable(conn, usersUpdateSql, legalfirstname, legallastname, address, userId)
					|| !updateAuthenticationTable(conn, authenticationUpdateSql, password, true, TOTP.genSecretKey(),
							userId)) {
				result = "FAILURE";
				reason = "Database Entry Error";
			}
		}
		serverResponse.put("result", result);
		serverResponse.put("reason", reason);
		return serverResponse;
	}

	public static JSONObject updateUserInformation(Connection conn, String address, String userId) {
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
			} catch (SQLException e) {
				result = "FAILURE";
				reason = e.getMessage();
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
			}
		} catch (SQLException e) {
			response.put("result", "FAILURE");
			response.put("reason", e.getMessage());
		} catch (IllegalArgumentException e) {
			response.put("result", "FAILURE");
			response.put("reason", "Invalid timestamp format: " + e.getMessage());
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

			int rowsUpdated = stmt.executeUpdate();
			return rowsUpdated > 0;
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
		String authenticationUpdateSql = "UPDATE healthhaven.authentication SET password = ? WHERE userid = ?";
		if (!updateAuthenticationTable(conn, authenticationUpdateSql, newPassword, getUserIdFromEmail(conn, email))) {
			result = "FAILURE";
			reason = "Database Entry Error";

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
			stmt.setString(2, userId);

			int rowsUpdated = stmt.executeUpdate();
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
			stmt.setString(4, userId);

			int rowsUpdated = stmt.executeUpdate();
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

	public static JSONObject authenticateUser(Connection conn, String email, String candidatePassword) {
		JSONObject serverResponse = new JSONObject();
		String result = "SUCCESS";
		String reason = "";
		String userType = "";

		// Returns the userId if user is authenticated correctly
		String sql = "SELECT * FROM healthhaven.authentication WHERE email = '" + email + "'";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet data_rs = stmt.executeQuery();
			System.out.println("Statement Executed");

			if (data_rs.next()) {
				String truePassword = data_rs.getString("password");
				// TODO: Hash this password.
				String hashedCandidatePassword = candidatePassword;
				if (!hashedCandidatePassword.equals(truePassword)) {
					result = "FAILURE";
					reason = "Incorrect Password";
				} else {
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

			} else {
				result = "FAILURE";
				reason = "Account does not exist";
			}

		} catch (SQLException e) {
			result = "FAILURE";
			reason = "Error Authenticating User";
		}

		serverResponse.put("result", result);
		serverResponse.put(result.equals("SUCCESS") ? "type" : "reason", reason);
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
					// Upon success, return all user information
					JSONObject userInformation = UserDAO.getUserInformation(conn, getUserIdFromEmail(conn, email));
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
		serverResponse.put(result.equals("SUCCESS") ? "type" : "reason", reason);
		System.out.println(serverResponse);
		return serverResponse;
		
	}

}
