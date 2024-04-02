package org.healthhaven.db.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.healthhaven.model.User;

public class AccountDAO {
	public static boolean createTemporaryUser(Connection conn, String userId, String email, String password, String dob, String accountType) {
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
			if (rowsInserted == 0) {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Error creating user: " + e.getMessage());
			return false;
		}

		// Insert account type into account information.
		sql = "INSERT INTO healthhaven.accounts (userid, accounttype) VALUES (?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, userId);

			stmt.setString(2, accountType);

			rowsInserted = stmt.executeUpdate();
			if (rowsInserted == 0) {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Error creating user: " + e.getMessage());
			return false;
		}
		
		sql = "INSERT INTO healthhaven.authentication (userid, email, password, reset) VALUES (?, ?, ?, ?)";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	    	 stmt.setString(1, userId);

			 stmt.setString(2, email);

			 // TODO: Salt and hash this password
			 String saltedHashedPassword = password;
			 stmt.setString(3, saltedHashedPassword);

	         // Set the 'reset' column to false
	         stmt.setBoolean(4, false);

	         rowsInserted = stmt.executeUpdate();
				if (rowsInserted == 0) {
					return false;
				}
	    } catch (SQLException e) {
	    	e.printStackTrace();
	        return false;
	    }
	    
	    return true;
	}
	
	public static boolean updateTemporaryUserAfterFirstLogin(Connection conn, String legalfirstname, String legallastname, String dob, String address, String email, String password, String accountType) {

	    // Step 1: Get userId from authentication table based on email
	    String userId = getUserIdFromEmail(conn, email);
	    if (userId == null) {
	        System.err.println("Error: User not found with email: " + email);
	        return false;
	    }
	    
	    // Step 1.5: Verify DOB
	    if (!verifyDOB(conn, userId, dob)) {
	        System.err.println("Error: Provided DOB does not match existing DOB.");
	        return false; 
	    }

	    // Step 3: Update users table
	    String usersUpdateSql = "UPDATE healthhaven.users SET legalfirstname = ?, legallastname = ?, address = ? WHERE userid = ?";
	    if (!updateUserTable(conn, usersUpdateSql, legalfirstname, legallastname, address, userId)) {
	        return false;
	    } 

	    // Step 5: Update authentication table (with password update)
	    String authenticationUpdateSql = "UPDATE healthhaven.authentication SET password = ? WHERE userid = ?";
	    if (!updateAuthenticationTable(conn, authenticationUpdateSql, password, userId)) { 
	        return false; 
	    } 

	    return true; 
	}
	
	private static boolean updateUserTable(Connection conn, String sql, String legalfirstname, String legallastname, String address, String userId) {
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
	
	
	private static boolean verifyDOB(Connection conn, String userId, String providedDob) {
		// Step 2: Prepare date of birth (DOB)
	    java.util.Date utilDate;
        java.sql.Date sqlDate = java.sql.Date.valueOf(providedDob);

	    // Get existing DOB from database
	    String sql = "SELECT dob FROM healthhaven.users WHERE userId = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, userId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                java.sql.Date existingDob = rs.getDate("dob");
	                return existingDob.equals(providedDob);
	            } else {
	                // Handle case where user might not be found (unlikely, but good to handle)
	                return false; 
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching existing DOB: " + e.getMessage());
	        return false;
	    }
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
	
	
	public static boolean doesAccountExist(Connection conn, String email) {
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
	
	public static String authenticateUser(Connection conn, String email, String candidatePassword) {
		// Returns the userId if user is authenticated correctly
		String sql = "SELECT * FROM healthhaven.authentication WHERE email = '" + email + "'";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet data_rs = stmt.executeQuery();

			if (data_rs.next()) {
				String truePassword = data_rs.getString("password");
				// TODO: Hash this password.
				String hashedCandidatePassword = candidatePassword;
				if (!hashedCandidatePassword.equals(truePassword)) {
					return "INCORRECT_PASSWORD";
				}
				boolean resetValue = data_rs.getBoolean("reset");
				if (!resetValue) {
					return "MUST_CREATE_ACCOUNT";
				} else {
					return "AUTHENTICATED";
				}
			} else {
				return "EMAIL DOES NOT EXIST";
			}

		} catch (SQLException e) {
			System.err.println("Error creating user: " + e.getMessage());
			return null;
		}
	}



}
