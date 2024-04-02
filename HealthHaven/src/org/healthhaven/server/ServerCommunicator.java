package org.healthhaven.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


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
    public static String communicateWithServer(String message) {
    	
    	
        StringBuilder response = new StringBuilder();
        
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
            writer.write(message);
            writer.newLine();
            writer.flush();
            
            // System.out.println(message);
            boolean responseReceived = false;

            
            while (!responseReceived) {
            	if (reader.ready()) {
            		String line;
            		while ((line = reader.readLine()) != null) {
            			if (line.equals("TERMINATE")) {
                            responseReceived = true;
                            break;
                        }
                        response.append(line);
                    }
            	}
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error communicating with server.";
        }

        return response.toString();
    }
    
    public static void main(String[] args) {
		communicateWithServer("Hello Bye Bye Hello");
	}
}
