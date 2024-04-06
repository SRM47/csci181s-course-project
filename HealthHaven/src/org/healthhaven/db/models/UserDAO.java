/**
 * 
 */
package org.healthhaven.db.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

/**
 * 
 */
public class UserDAO {

	@SuppressWarnings("deprecation")
	public static boolean createUser(Connection conn, String userId, String legalfirstname, String legallastname, int dobYear, int dobMonth, int dobDay, String address, String email, String password, short accountType) {
		int rowsInserted = 0;
		// Insert user data into users table
		String sql = "INSERT INTO healthhaven.users (userid, legalfirstname, legallastname, dob, address) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, userId);

			stmt.setString(2, legalfirstname);

			stmt.setString(3, legallastname);

			stmt.setDate(4, new java.sql.Date(dobYear, dobMonth, dobDay));

			stmt.setString(5, address);

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

			stmt.setString(2, String.valueOf(accountType));

			rowsInserted = stmt.executeUpdate();
			if (rowsInserted == 0) {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Error creating user: " + e.getMessage());
			return false;
		}

		// Insert email and password into authentication table.
		sql = "INSERT INTO healthhaven.authentication (userid, email, password) VALUES (?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, userId);

			stmt.setString(2, email);

			// TODO: Salt and hash this password
			String saltedHashedPassword = password;
			stmt.setString(3, saltedHashedPassword);

			rowsInserted = stmt.executeUpdate();
			if (rowsInserted == 0) {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Error creating user: " + e.getMessage());
			return false;
		}

		return true;
	}

//	@SuppressWarnings("deprecation")
	public static JSONObject getUserInformation(Connection conn, String userId) {
		JSONObject response = new JSONObject();
		String reason = "";
		
		String sql = "SELECT * FROM healthhaven.users WHERE userid = '" + userId + "'";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet data_rs = stmt.executeQuery();
			if (data_rs.next()) {
				response.put("email", getEmailFromId(conn, userId));
				response.put("userID", userId);
				response.put("first_name", data_rs.getString("legalfirstname"));
				response.put("last_name", data_rs.getString("legallastname"));
				response.put("address", data_rs.getString("address"));
				response.put("dob", data_rs.getDate("dob").toString());
				response.put("accountType", getUserAccountType(conn, userId));
				response.put("result", "SUCCESS");
				return response;
			}
			reason = "User does not exist";
		} catch (SQLException e) {
			System.err.println("Error creating user: " + e.getMessage());
			reason = "Error creating user: " + e.getMessage();
		}
		response.put("result", "FAILURE");
		response.put("reason", reason);
		return response;

	}

	private static String getUserAccountType(Connection conn, String userId) {
		String sql = "SELECT * FROM healthhaven.accounts WHERE userid = '" + userId + "'";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet data_rs = stmt.executeQuery();
			if (data_rs.next()) {
				return data_rs.getString("accounttype");
			}
		} catch (SQLException e) {
			System.err.println("Error creating user: " + e.getMessage());
		}
		return "";
	}

	private static String getEmailFromId(Connection conn, String userId) {
		String sql = "SELECT * FROM healthhaven.authentication WHERE userid = '" + userId + "'";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet data_rs = stmt.executeQuery();
			if (data_rs.next()) {
				return data_rs.getString("email");
			}
		} catch (SQLException e) {
			System.err.println("Error creating user: " + e.getMessage());
		}
		return "";
	}
	

}
