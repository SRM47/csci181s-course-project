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
import java.sql.Connection;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.healthhaven.db.DatabaseConnectionUtil;
import org.healthhaven.db.models.AccountDAO;
import org.healthhaven.model.EmailSender;
import org.healthhaven.model.PasswordGenerator;
import org.healthhaven.model.UserIdGenerator;
import org.json.JSONException;
import org.json.JSONObject;

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
	private Connection conn;

	private final ReentrantLock databaseLock = new ReentrantLock();

	public Server(int portNum, int maxThreads, int maxRequests) throws Exception {
		this.PORT = portNum;
		this.MAX_THREADS = maxThreads;
		this.MAX_REQUESTS = maxRequests;
	}

	public void start() throws Exception {
		this.conn = DatabaseConnectionUtil.connect();
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
			// Read the incoming message json
			String msg = reader.readLine();
			String response = "";
			
			//"type" otp ; password TODO

			JSONObject requestData = new JSONObject(msg);
			switch (requestData.getString("request")) {
			case "UPDATE_ACCOUNT":
				String newAddress = requestData.getString("address");
				String userId = requestData.getString("userId");
				response = AccountDAO.updateUserInformation(this.conn, newAddress, userId);
				break;
				
//				updateUserTable
			case "PASSWORD_RESET":
//				switch(type):
//					case "EMAIL_CHECK" COMES WITH email n timestamp
//					case "VERIFY OTP" comes with email and otp
//					case update password gives me TODO
				
			case "UPDATE_PASSWORD":
				response = AccountDAO.updatePassword(conn, requestData.getString("password"),
						requestData.getString("userId"));
				break;

			case "ALLOW_ACCOUNT_CREATION":
				String email = requestData.getString("email");
				String dob = requestData.getString("dob");
				String userType = requestData.getString("userType"); //TODO this is same as accountType but accounttype is better
				if (AccountDAO.accountExistsByEmail(this.conn, email)) {
					response = "FAILURE";
					break;
				}
				String generatedPassword = PasswordGenerator.generate(16);
				UserIdGenerator g = new UserIdGenerator(16);
				String generatedUserId = g.generate();
				AccountDAO.createTemporaryUser(this.conn, generatedUserId, email, generatedPassword, dob, userType);
				EmailSender.sendDefaultPasswordEmail(email, generatedPassword, userType);
//				result:
//				type: NEW or EXISTING
//				if new, usertype: DOCTOR, etc TODO
				response = "SUCCESS";
				break;
				
			case "CREATE_ACCOUNT":
				boolean success = AccountDAO.updateTemporaryUserAfterFirstLogin(this.conn,
						requestData.getString("first_name"), requestData.getString("last_name"),
						requestData.getString("dob"), requestData.getString("address"), requestData.getString("email"),
						requestData.getString("password"), requestData.getString("accountType"));
				response = success ? "SUCCESS" : "FAILURE";
				break;
				
			case "LOGIN":
				switch (requestData.getString("type")) {
					case "PASSWORD":						
						response = AccountDAO.authenticateUser(this.conn, requestData.getString("email"),
								requestData.getString("password"));
					case "OTP":
						response = AccountDAO.authenticateOTP(this.conn, requestData.getString("email"),
								requestData.getString("OTP"));
				}
//				check if type is password or otp (will havet to store in db)
//				for otp: success, email, userid, first_name, last_name, address, dob, account type TODO
				break; //we should be talking twice bc otp
				
			case "REQUEST_PATIENT_DATA_SUMMARY":
			case "VIEW_RECORD":
			case "CREATE_RECORD":
			default:
			
			}

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
		} catch (JSONException | IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

}
