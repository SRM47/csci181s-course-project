import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
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

	public Server(int portNum, int maxThreads, int maxRequests) throws Exception {
		this.PORT = portNum;
		this.MAX_THREADS = maxThreads;
		this.MAX_REQUESTS = maxRequests;
	}

	public void start() throws Exception {
		// Create the thread pool. With a CachedThreadPoo, the number of threads will
		// grow as needed,
		// and unused threads will be terminated.
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
		}

		// Begin listening for connections
		while (true) {
			try {
				// Accept an incoming connection and store in socket object.
				Socket client = server.accept();
				// Send the client connection to the thread pool.
				executor.execute(() -> handleClientConnection(client));

			} catch (IOException ioe) {
				// Error in connecting to socket.
				System.out.println("Error connecting to socket");
			}
		}

	}

	public void handleClientConnection(Socket client) {
		try {
			// Initialize input data stream.
			DataInputStream streamIn = new DataInputStream(new BufferedInputStream(client.getInputStream()));
			// Read the incoming message.
			String msg = streamIn.readUTF();
			// TODO Decipher the message and perform the appropriate task.
			System.out.println(msg);
		} catch (IOException e) {
			// Occurs when client disconnects.
			e.printStackTrace();
		}

	}

}
