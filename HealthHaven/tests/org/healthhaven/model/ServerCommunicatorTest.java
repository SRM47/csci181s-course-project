//package org.healthhaven.model;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.net.Socket;
//import java.net.Socket;
//
//public class ServerCommunicatorTest {
//	
//	public interface SocketFactory {
//	    Socket createSocket(String serverAddress, int serverPort) throws Exception;
//	}
//
//    @Test
//    public void testCommunicateWithAccountServer() throws Exception {
//        // Set up the mock socket and streams
//        Socket mockSocket = mock(Socket.class);
//        ByteArrayInputStream mockInput = new ByteArrayInputStream("Mock Response\nTERMINATE\n".getBytes());
//        ByteArrayOutputStream mockOutput = new ByteArrayOutputStream();
//
//        when(mockSocket.getInputStream()).thenReturn(mockInput);
//        when(mockSocket.getOutputStream()).thenReturn(mockOutput);
//
//        // Mock the socket factory to return our mock socket
//        SocketFactory mockFactory = mock(SocketFactory.class);
//        when(mockFactory.createSocket(anyString(), anyInt())).thenReturn(mockSocket);
//
//        // Create an instance of ServerCommunicator with the mocked factory
//        ServerCommunicator communicator = new ServerCommunicator();
//
//        // Execute the method under test
//        String response = communicator.communicateWithAccountServer("Test Message");
//
//        // Verify the response and that the socket was used as expected
//        assertEquals("Mock Response", response, "The response should match the mock response.");
//        assertTrue(mockOutput.toString().contains("Test Message"), "The output stream should contain the sent message.");
//    }
//}
