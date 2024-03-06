/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class ServerHandler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Instantiate the server.
			Server server = new Server(8888, 30, 100);
			// Start the server.
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
