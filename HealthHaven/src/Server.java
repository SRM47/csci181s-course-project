import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
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

/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class Server {

	private ServerSocket server;
	private int PORT;
	private int MAX_THREADS;
	private int MAX_REQUESTS;
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
		System.out.println("Beginning process to start server");
		try {
			System.out.println("Trying to connect to port " + PORT);
			// Bind server to specified port.
			this.server = new ServerSocket(PORT);
			System.out.println("Successfully binded server to port " + PORT);
		} catch (IOException exception) {
			System.out.println("Error in creating the server");
			System.out.println(exception);
		}

		// Begin listening for connections
		while (true) {
			try {
				// Accept an incoming connection and store in socket object.
				System.out.println("Awaiting connection...");
				Socket client = server.accept();
				// Send the client connection to the thread pool.
				executor.execute(() -> handleClientConnection(client));

			} catch (IOException ioe) {
				// Error in connecting to socket.
				System.out.println("Error connecting to socket");
			}
		}

	}

	private void handleClientConnection(Socket client) {
		try {
			// Initialize input data stream.
			DataInputStream streamIn = new DataInputStream(new BufferedInputStream(client.getInputStream()));
			// Read the incoming message.
			String msg = streamIn.readUTF();
			// TODO Decipher the message and perform the appropriate task.
			System.out.println(msg);

			// TODO Need to add validation on msg to know it's not a bogus message.
			String[] commands = msg.split(" ");

			// Initialize response variable.
			String response = "NONE";
			switch (commands[0]) {
			
			case "UPDATE_RECORD":
				// Update a Patient's Medical Record (height, weight data)
                response = MedicalRecordDatabaseHandler.updatePatientMedicalRecords(commands[1], commands[2], commands[3], commands[4]);
				break;
			case "VIEW":
				// View a User's Medical Record (height, weight data)
                response = MedicalRecordDatabaseHandler.viewPatientMedicalRecord(commands[1]);
				break;
			case "REQUEST_PATIENT_DATA_SUMMARY":
				// Data Analyst Requesting Data from CSV
				response = MedicalRecordDatabaseHandler.getAllRecords();
				break;
			case "CREATE_ACCOUNT":
				// Create new account by creating entry in database
				response = AccountInformationDatabaseHandler.createAccount(commands[1], commands[2], commands[3], commands[4], commands[5], commands[6], commands[7], commands[8], commands[9]);
				break;
			case "AUTHENTICATE_ACCOUNT":
				// Check if account associated with an email matches password
				response = AccountInformationDatabaseHandler.authenticateAccount(commands[1], commands[2], commands[3]);
				break;
			case "EXISTING_ACCOUNT":
				// Check if account associated with an email exists
				response = AccountInformationDatabaseHandler.accountExistsByEmail(commands[1]) ? "VALID" : "INVALID";
				break;
			case "UPDATE_ACCOUNT":
				// User updating existing personal data and update it in database
				response = AccountInformationDatabaseHandler.updateAccountInformation(commands[1], commands[2], commands[3], commands[4]);
				break;

			}
			// Respond to client with appropriate response.
			// Initialize output data stream.
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			// Write message to output stream back to client.
			writer.write(response);
            writer.newLine();
            writer.flush();
			
		} catch (IOException e) {
			// Occurs when client disconnects.
			e.printStackTrace();
		}

	}
	
	
	

}
