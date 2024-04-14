package org.healthhaven.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;


public class ServerCommunicator {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8886; // Example port for account data server
    
    private static final String protocol = "TLSv1.3";
    private static final String[] cipherSuites = new String[]{"TLS_AES_128_GCM_SHA256"};


    /**
     * Generic method to handle communication with any server.
     *
     * @param serverAddress The server's address.
     * @param serverPort The server's port.
     * @param message The message to send to the server.
     * @return The server's response as a String.
     */
    public static String communicateWithServer1(String message) {
    	
        
        try {
        	// Certificate Trust Configuration (if using a self-signed certificate)
        	// Currently using the server's Key-store, which in reality wouldn't be possible because server and 
        	// client machines would be different.
        	// We are doing this for the sake of convenience and demonstration. 
            String truststorePath = "src/org/healthhaven/server/cert.jks"; // Contains the self-signed cert or CA
            String truststorePassword = "healthhaven"; 

            // Load Truststore (contains trusted certificates)
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(truststorePath), truststorePassword.toCharArray());
            
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            
        	// Create SSLContext
        	SSLContext sslContext = SSLContext.getInstance(protocol);
        	sslContext.init(null, tmf.getTrustManagers(), null);
        	
        	SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        	
        	// Setup server socket.
        	SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(SERVER_ADDRESS, SERVER_PORT);
        	
        	// Enable the cipher suite
        	socket.setEnabledCipherSuites(cipherSuites);
        	
        	// Begin handshake.
        	socket.startHandshake();
        	
        	// Initialize readers and writer.
        	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        	
            
            // Write the message.
            try {
                // Write the message.
                writer.write(message);
                writer.newLine();
                writer.flush();
                String response = reader.readLine();
                return response; 
            } catch (IOException e) {
                // Handle IO exceptions (e.g., log the error)
            	e.printStackTrace();
            } finally {
                // Close resources to prevent leaks
                try { writer.close(); } catch (Exception ignored) {} 
                try { reader.close(); } catch (Exception ignored) {}
                try { socket.close(); } catch (Exception ignored) {} 
            } 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    
    //This is to test client side code by mimicking the server response.
    public static String communicateWithServer(String message) {
    	System.out.println(message);
    	JSONObject json = new JSONObject(message);
    	JSONObject serverResponse = new JSONObject();
    	switch(json.getString("request")) {
		case "LOGIN":
			switch (json.getString("type")) {
			case "PASSWORD":
				serverResponse.put("result", "SUCCESS");
				serverResponse.put("type" , "EXISTING"); 
				//or this for new
				serverResponse.put("type" , "EXISTING"); 
				serverResponse.put("userType" , "DOCTOR"); //feel free to change here for testing
				return serverResponse.toString();
			case "OTP":
				serverResponse.put("result", "SUCCESS");
				serverResponse.put("email", "example@email.com");
				serverResponse.put("userID", "abcdefg"); //might not need this
				serverResponse.put("first_name", "Sae");
				serverResponse.put("last_name", "Furukawa");
				serverResponse.put("address", "Claremont, CA");
				serverResponse.put("accountType", "SUPERADMIN"); //change the role for testing
				serverResponse.put("dob", LocalDate.of(2000, 10, 10).toString());
				return serverResponse.toString();

			default:	
				serverResponse.put("result", "FAILURE");
				serverResponse.put("reason", "Invalid Request");
				return serverResponse.toString();	
			}
		case "PASSWORD_RESET":
			switch (json.getString("type")){
			case "EMAIL_CHECK":
				serverResponse.put("result", "SUCCESS");
				serverResponse.put("email", "example@example.com"); //don't need this
				return serverResponse.toString();
			case "VERIFY_OTP":
				serverResponse.put("result", "SUCCESS");
				serverResponse.put("email", "example@example.com");
				serverResponse.put("first_name", "Sae");
				serverResponse.put("last_name", "Furukawa");
				serverResponse.put("dob", LocalDate.of(2000, 10, 10).toString());
				return serverResponse.toString();
			case "UPDATE_PASSWORD":
				serverResponse.put("result", "SUCCESS");
				return serverResponse.toString();
			default:
				serverResponse.put("result", "FAILURE");
				serverResponse.put("reason", "incorrect request");
				return serverResponse.toString();	
			}
		case "ALLOW_ACCOUNT_CREATION":
			serverResponse.put("result", "SUCCESS");
			return serverResponse.toString();
		case "CREATE_ACCOUNT":
			serverResponse.put("result", "SUCCESS");
			return serverResponse.toString();
		case "UPDATE_ACCOUNT":
			serverResponse.put("result", "SUCCESS");
			serverResponse.put("type" , "EXISTING");
			return serverResponse.toString();
		case "REQUEST_PATIENT_DATA_SUMMARY":
			serverResponse.put("average height", "180");
			serverResponse.put("average weight", "180");
			return serverResponse.toString();
		case "VIEW_RECORD":
			serverResponse.put("height", "180");
			serverResponse.put("weight", "180");
			return serverResponse.toString();
		case "CREATE_RECORD":
			serverResponse.put("result", "SUCCESS");
			serverResponse.put("height", "180");
			serverResponse.put("weight", "190");
			return serverResponse.toString();
		case "DEACTIVATE_ACCOUNT":
			switch (json.getString("type")) {
			case  "VALIDATE_ACCOUNT":
				serverResponse.put("result", "SUCCESS");
				return serverResponse.toString();
			case "DEACTIVATE_ACCOUNT":
				serverResponse.put("result", "SUCCESS");
				return serverResponse.toString();
			}
		case "SEARCH_ACCOUNT":
			serverResponse.put("result", "SUCCESS");
			serverResponse.put("email", "example@email.com");
			serverResponse.put("userID", "abcdefg"); //might not need this
			serverResponse.put("first_name", "Sae");
			serverResponse.put("last_name", "Furukawa");
			serverResponse.put("address", "Claremont, CA");
			serverResponse.put("accountType", "SUPERADMIN"); //change the role for testing
			serverResponse.put("dob", LocalDate.of(2000, 10, 10).toString());
			return serverResponse.toString();
		default:
			serverResponse.put("result", "FAILURE");
			serverResponse.put("reason", "incorrect request");
			return serverResponse.toString();	
	}
    }
    
}
