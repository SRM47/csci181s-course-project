/**
 * 
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DB {
    public static Connection connect() throws SQLException {

        try {
            // Get database credentials from DatabaseConfig class
            String jdbcUrl = DatabaseConfig.getDbUrl();
            String user = DatabaseConfig.getDbUsername();
            String password = DatabaseConfig.getDbPassword();
            
            System.out.println("Connecting to " + jdbcUrl);

            // Open a connection
            return DriverManager.getConnection(jdbcUrl, user, password);

        } catch (SQLException  e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
