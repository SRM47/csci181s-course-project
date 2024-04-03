package org.healthhaven.model;

import org.healthhaven.server.ServerCommunicator;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class PasswordGeneratorTest {

    @Test
    public void testGenerate() {
        int length = 10;
        String password = PasswordGenerator.generate(length);
        
        assertNotNull(password, "Generated password should not be null.");
        assertEquals(length, password.length(), "Generated password should match the specified length.");
    }

    @Test
    public void testPasswordStrength() {
        String strongPassword = "Password1!";
        int strength = PasswordGenerator.passwordStrength(strongPassword, "John", "Doe", LocalDate.of(1990, 1, 1));
        assertEquals(1, strength, "Strength should be 1 for a password not containing personal information.");
        
        String weakPasswordWithFirstName = "JohnPassword1!";
        strength = PasswordGenerator.passwordStrength(weakPasswordWithFirstName, "John", "Doe", LocalDate.of(1990, 1, 1));
        assertEquals(2, strength, "Strength should be 2 for a password containing the first name.");
        
        String weakPasswordWithLastName = "DoePassword1!";
        strength = PasswordGenerator.passwordStrength(weakPasswordWithLastName, "John", "Doe", LocalDate.of(1990, 1, 1));
        assertEquals(2, strength, "Strength should be 2 for a password containing the last name.");
        
        String weakPasswordWithYear = "1990Password1!";
        strength = PasswordGenerator.passwordStrength(weakPasswordWithYear, "John", "Doe", LocalDate.of(1990, 1, 1));
        assertEquals(3, strength, "Strength should be 3 for a password containing the birth year.");
        
        String weakPasswordWithMonthDay = "11nPassword1!";
        strength = PasswordGenerator.passwordStrength(weakPasswordWithMonthDay, "John", "Doe", LocalDate.of(1990, 1, 1));
        assertEquals(3, strength, "Strength should be 3 for a password containing the birth month and day.");
    }

    @Test
    public void testCompromiseChecker() throws IOException {
        try (MockedStatic<ServerCommunicator> mocked = mockStatic(ServerCommunicator.class)) {
            // Mock the static method call to return a simulated response
            mocked.when(() -> ServerCommunicator.communicateWithServer(anyString()))
                    .thenReturn("Expected response from pwnedpasswords API");

            // Call your compromiseChecker method
            int result = PasswordGenerator.compromiseChecker("testPassword");

            // Assert based on your expected outcome
            
            assertEquals(1, result, "The compromise check result should match expected outcome.");
        }
    }

    @Test
    public void testSha1Hash() {
        String input = "test";
        String expectedOutput = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3"; // SHA-1 hash of "test"
        String actualOutput = PasswordGenerator.sha1Hash(input);
        
        assertEquals(expectedOutput, actualOutput, "SHA-1 hash should match the expected output.");
    }

    @Test
    public void testIsValidEmail() {
        assertTrue(PasswordGenerator.isValidEmail("example@example.com"), "Should return true for a valid email.");
        assertFalse(PasswordGenerator.isValidEmail("invalid-email"), "Should return false for an invalid email.");
    }
}
