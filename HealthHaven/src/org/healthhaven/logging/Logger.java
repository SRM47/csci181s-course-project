/**
 * 
 */
package org.healthhaven.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	

    public static synchronized void log(String filepath, String clientIP, String message) {
        try (FileWriter fileWriter = new FileWriter(filepath, true)) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String logEntry = String.format("[%s] - %s - %s\n", timestamp, clientIP, message);
            fileWriter.write(logEntry);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
    

}
