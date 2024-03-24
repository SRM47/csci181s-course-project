

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
			Server servert = new Server(8889, 30, 100);
			// Start the server.
			servert.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
