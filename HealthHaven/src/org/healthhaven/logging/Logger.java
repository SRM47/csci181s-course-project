/**
 * 
 */
package org.healthhaven.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress; 
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

public class Logger {
	

    private final String LOG_FILE_PATH = "FILE_PATH_HERE";
    
    public enum LogLevel {
        INFO,
        WARNING,
        ALERT
    }
    
    public void log(LogLevel loglevel, Socket clientSocket, JSONObject request) {

        try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH, true)) {
            String clientIP = clientSocket.getInetAddress().getHostAddress(); // Get client IP from socket
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String req = request.getString("request");
            // ... (rest of the logging logic remains similar)

        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
