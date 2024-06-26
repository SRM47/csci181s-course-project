package org.healthhaven.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyStore;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import org.healthhaven.db.DatabaseConnectionUtil;
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
	private static final String[] cipherSuites = new String[] { "TLS_AES_256_GCM_SHA384" };
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
		// Implement Atomic: All or nothing.
		this.conn.setAutoCommit(false);
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
			System.out.println(requestData);
			JSONObject serverResponse = APIHandler.processAPIRequest(requestData, this.conn, client);
			System.out.println(serverResponse);

			// Respond to client with appropriate response.
			// Initialize output data stream.
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

			// Write message to output stream back to client.
			writer.write(serverResponse.toString());
			writer.newLine();
			writer.flush();

		} catch (IOException e) {
			// Occurs when client disconnects.
			e.printStackTrace();
		} catch (JSONException | IllegalArgumentException e) {
			JSONObject jsonError = returnFailureResponse("Error parsing request json");
			try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				writer.write(jsonError.toString());
				writer.newLine();
				writer.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		}

	}
	
	private static JSONObject returnFailureResponse(String reason) {
		JSONObject serverResponse = new JSONObject();
		serverResponse.put("result", "FAILURE");
		serverResponse.put("reason", reason);
		return serverResponse;	
	}

}
