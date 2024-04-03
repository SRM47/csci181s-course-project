/**
 * 
 */
package org.healthhaven.db.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

import org.healthhaven.model.User;

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
//	public static User getUserInformation(Connection conn, String userId) {
//		String sql = "SELECT * FROM healthhaven.users WHERE userid = '" + userId + "'";
//
//		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//			ResultSet data_rs = stmt.executeQuery();
//			User user = null;
//			if (data_rs.next()) {
//				Date dob = data_rs.getDate("dob");
//				user = new User(Long.parseLong(data_rs.getString("userid")), "email",
//						data_rs.getString("legalfirstname"), data_rs.getString("legallastname"),
//						data_rs.getString("address"), LocalDate.of(dob.getYear(), dob.getMonth(), dob.getDay()));
//			}
//			return user;
//		} catch (SQLException e) {
//			System.err.println("Error creating user: " + e.getMessage());
//			return null;
//		}
//
//	}
	

}
