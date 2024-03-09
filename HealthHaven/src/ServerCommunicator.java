import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

public class ServerCommunicator {

    private static final String ACCOUNT_SERVER_ADDRESS = "localhost";
    private static final int ACCOUNT_SERVER_PORT = 8889; // Example port for account data server
    private static final String MEDICAL_SERVER_ADDRESS = "localhost";
    private static final int MEDICAL_SERVER_PORT = 8889; // Example port for medical data server

    /**
     * Communicates with the server dedicated to account data.
     *
     * @param message The message to send to the account server.
     * @return The server's response as a String.
     */
    public static String communicateWithAccountServer(String message) {
        return communicate(ACCOUNT_SERVER_ADDRESS, ACCOUNT_SERVER_PORT, message);
    }

    /**
     * Communicates with the server dedicated to medical data.
     *
     * @param message The message to send to the medical server.
     * @return The server's response as a String.
     */
    public static String communicateWithMedicalServer(String message) {
        return communicate(MEDICAL_SERVER_ADDRESS, MEDICAL_SERVER_PORT, message);
    }

    /**
     * Generic method to handle communication with any server.
     *
     * @param serverAddress The server's address.
     * @param serverPort The server's port.
     * @param message The message to send to the server.
     * @return The server's response as a String.
     */
    private static String communicate(String serverAddress, int serverPort, String message) {
        StringBuilder response = new StringBuilder();
        
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

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
}
