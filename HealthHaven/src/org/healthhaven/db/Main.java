/**
 * 
 */
package org.healthhaven.db;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args){
        try (Connection connection =  DatabaseConnectionUtil.connect()){
            System.out.println("Connected to the PostgreSQL database.");
////            Patient p = new Patient(1234567890, "test@gmail.com", "testpassword", "testfirst", "testlast",
//// 				   "test street", LocalDate.now());
////            boolean succeeded = UserDAO.createUser(connection, p);
////            System.out.println(succeeded);
//            User u = UserDAO.getUserInformation(connection, "1234567890");
////            System.out.println("Connected to the PasdfostgreSQL database.");
//            System.out.println(u);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
