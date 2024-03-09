import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

public class ServerTest {
	// Must run server separate before these tests.
	
//	int PORT = 8889;
//	Server testServer;
//
//	@BeforeAll
//	public void setUp() throws Exception {
//		System.out.println("Server Starting");
//		testServer = new Server(PORT, 30, 30);
//		// Start the server in a new thread to enable testing.
//		new Thread(() -> {
//            try {
//            	testServer.start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//	}
//	
//	@AfterAll
//	public void tearDown() throws IOException {
//		testServer.stop();
//	}

	@Test
    public void testUpdateRecord() throws InterruptedException {
        String update_record_message = "UPDATE_RECORD 12345 999 99 12345";
        String response = ServerCommunicator.communicateWithMedicalServer(update_record_message);
        assertEquals(response, "SUCCESS");
    }
    
    @Test
    public void testViewIdDoesNotExists() throws InterruptedException {
        String view_message = "VIEW 99999";
        String response = ServerCommunicator.communicateWithMedicalServer(view_message);
        assertEquals(response, "NONE");
    }
    
    @Test
    public void testViewIdExists() throws InterruptedException {
        String view_message = "VIEW 2079955511";
        String response = ServerCommunicator.communicateWithMedicalServer(view_message);
        assertEquals(response, "2079955511,170,85,2024-03-09T07:26:05.916130Z");
    }
    
    @Test
    public void testCreateAccountEmailExists() {

    	
    	String view_message = "CREATE_ACCOUNT 12345678 hello hello sam malik 101010 2002-12-12 12am Doctor";
        String response = ServerCommunicator.communicateWithMedicalServer(view_message);
        assertEquals(response, "FAILURE");

    }
    
    @Test
    public void testCreateAccountEmailDoesNotExists() {

    	String random_id = generateRandomString(8);
    	String random_username = generateRandomString(10);
    	String view_message = "CREATE_ACCOUNT "+random_id+ " " + random_username + " hello sam malik 101010 2002-12-12 12am Doctor";
        String response = ServerCommunicator.communicateWithMedicalServer(view_message);
        assertEquals(response, "SUCCESS");

    }
    
    @Test
    public void testAuthenticateAccountExists() {
    	String view_message = "AUTHENTICATE_ACCOUNT hello hello 2024-03-09T07:25:50.221346Z";
        String response = ServerCommunicator.communicateWithMedicalServer(view_message);
        assertEquals(response, "2079955511,hello,hello,hello,hello,hello,2002-12-12,2024-03-09T07:25:50.221346Z,Patient");

    }
    
    @Test
    public void testAuthenticateAccountDoesNotExists() {
    	String view_message = "AUTHENTICATE_ACCOUNT noemail hello 2024-03-09T07:25:50.221346Z";
        String response = ServerCommunicator.communicateWithMedicalServer(view_message);
        assertEquals(response, "FAILURE");

    }
    
    public static String generateRandomString(int length) {
        // Define characters allowed in the random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a random object
        Random random = new Random();

        // Create a StringBuilder to store the random string
        StringBuilder sb = new StringBuilder(length);

        // Generate random characters and append them to the StringBuilder
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        // Convert StringBuilder to String and return
        return sb.toString();
    }

}
