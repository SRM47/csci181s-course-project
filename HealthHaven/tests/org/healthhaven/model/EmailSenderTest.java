//package org.healthhaven.model;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import javax.mail.Transport;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//
//public class EmailSenderTest {
//
//    private MockedStatic<Transport> mockedTransport;
//
//    @BeforeEach
//    public void setUp() {
//        // Mock static Transport.send method before each test
//        mockedTransport = Mockito.mockStatic(Transport.class);
//    }
//
//    @Test
//    public void testSendDefaultPasswordEmail() {
//        // Given
//        String email = "test@example.com";
//        String password = "password123";
//        String userType = "Doctor";
//        String expectedResponse = "Email Sent Successfully!!";
//
//        // When
//        String actualResponse = EmailSender.sendDefaultPasswordEmail(email, password, userType);
//
//        // Then
//        assertEquals(expectedResponse, actualResponse, "Expected success response from email sending");
//
//        // Verify Transport.send was called
//        mockedTransport.verify(() -> Transport.send(any()), Mockito.times(1));
//    }
//}
