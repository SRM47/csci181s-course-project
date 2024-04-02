package org.healthhaven.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class Server {

	private SSLServerSocket server = null;
	private int PORT;
	private int MAX_THREADS;
	private int MAX_REQUESTS;
	private static final String protocol = "TLSv1.3";
	private static final String[] cipherSuites = new String[] { "TLS_AES_128_GCM_SHA256" };
	private static final String path_to_keystore = "src/org/healthhaven/server/cert.jks";

	private final ReentrantLock databaseLock = new ReentrantLock();

	public Server(int portNum, int maxThreads, int maxRequests) throws Exception {
		this.PORT = portNum;
		this.MAX_THREADS = maxThreads;
		this.MAX_REQUESTS = maxRequests;
	}

	public void start() throws Exception {
		// Create the thread pool. With a CachedThreadPoo, the number of threads will
		// grow as needed, and unused threads will be terminated.
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

		// Start the server.
		System.out.println("Beginning process to start server!");
		System.out.println("Trying to connect to port " + PORT);

		// Keystore Configuration
        String keystorePath = "src/org/healthhaven/server/cert.jks";
        String keystorePassword = "healthhaven"; 

        // Certificate Trust Configuration (if using a self-signed certificate)
        String truststorePath = "src/org/healthhaven/server/cert.jks"; // Contains the self-signed cert or CA
        String truststorePassword = "healthhaven"; 

        // Load Keystore (contains server's certificate and private key)
        KeyStore keyStore = KeyStore.getInstance("JKS"); 
        keyStore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        // Load Truststore (contains trusted certificates)
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream(truststorePath), truststorePassword.toCharArray());

        // Initialize KeyManager and TrustManager
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keystorePassword.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Create SSLContext
        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		// Create Server Socket Factory
		SSLServerSocketFactory factory = sslContext.getServerSocketFactory();

		// Bind server to specified port and enable protocols and cipher suites.
		this.server = (SSLServerSocket) factory.createServerSocket(PORT);
		this.server.setEnabledCipherSuites(cipherSuites);

		// Require client authentication
		// this.server.setNeedClientAuth(true);

		System.out.println("Successfully binded server to port " + PORT);

		// Begin listening for connections
		while (true) {
			try {
				// Accept an incoming connection and store in socket object.
				System.out.println("Awaiting connection...");
				SSLSocket sslClientSocket = (SSLSocket) server.accept();
				// Send the client connection to the thread pool.
				executor.execute(() -> handleClientConnection(sslClientSocket));

			} catch (IOException ioe) {
				// Error in connecting to socket.
				System.out.println("Error connecting to socket");
			}
		}

	}

	public void stop() throws IOException {
		server.close();
	}

	private void handleClientConnection(SSLSocket client) {
		System.out.println("Client connected!");
		try {
			// Initialize input data stream.
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			// Read the incoming message.
			String msg = reader.readLine();
			System.out.println(msg);

			// TODO Need to add validation on msg to know it's not a bogus message.
			String[] commands = msg.split(" ");
			for (String c : commands) {
				System.out.println(c);
			}

			// Initialize response variable.
			String response = "NONE";
//			switch (commands[0]) {
//
//			case "UPDATE_RECORD":
//				// Update a Patient's Medical Record (height, weight data)
//				response = MedicalRecordDatabaseHandler.updatePatientMedicalRecords(commands[1], commands[2],
//						commands[3], commands[4]);
//				break;
//			case "VIEW":
//				// View a User's Medical Record (height, weight data)
//				response = MedicalRecordDatabaseHandler.viewPatientMedicalRecord(commands[1]);
//				break;
//			case "REQUEST_PATIENT_DATA_SUMMARY":
//				// Data Analyst Requesting Data from CSV
//				// response = MedicalRecordDatabaseHandler.getAllRecords();
//				response = MedicalRecordDatabaseHandler.getMeans();
//				break;
//			case "CREATE_ACCOUNT":
//				// Create new account by creating entry in database
//				response = AccountInformationDatabaseHandler.createAccount(commands[1], commands[2], commands[3],
//						commands[4], commands[5], commands[6], commands[7], commands[8], commands[9]);
//				break;
//			case "AUTHENTICATE_ACCOUNT":
//				// Check if account associated with an email matches password
//				response = AccountInformationDatabaseHandler.authenticateAccount(commands[1], commands[2], commands[3]);
//				break;
//			case "EXISTING_ACCOUNT":
//				// Check if account associated with an email exists
//				response = AccountInformationDatabaseHandler.accountExistsByEmail(commands[1]) ? "VALID" : "INVALID";
//				break;
//			case "UPDATE_ACCOUNT":
//				// User updating existing personal data and update it in database
//				response = AccountInformationDatabaseHandler.updateAccountInformation(commands[1], commands[2],
//						commands[3], commands[4]);
//				break;
//
//			}
			System.out.println(response);

			// Respond to client with appropriate response.
			// Initialize output data stream.
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

			// Write message to output stream back to client.
			writer.write(response + "\nTERMINATE");
			writer.newLine();
			writer.flush();

		} catch (IOException e) {
			// Occurs when client disconnects.
			e.printStackTrace();
		}

	}

}
