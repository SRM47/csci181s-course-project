package org.healthhaven.model;

import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

public class TOTPTest {

    @Test
    public void testGenerateAndVerifyTOTP() throws Exception {
        // Generate a secret key for testing
        Key secretKey = TOTP.secretKey();
        
        // Generate a TOTP using the secret key
        String generatedTOTP = TOTP.genTOTP(secretKey);
        
        // Verify the TOTP using the same key
        int verificationResult = TOTP.verTOTP(secretKey, generatedTOTP);
        
        // Assert that the TOTP was verified successfully
        assertEquals(1, verificationResult, "The TOTP should be verified successfully.");
    }

    @Test
    public void testTOTPVerificationFailsForIncorrectTOTP() throws Exception {
        // Generate a secret key for testing
        Key secretKey = TOTP.secretKey();
        
        // Use an incorrect TOTP
        String incorrectTOTP = "12345678";
        
        // Attempt to verify the incorrect TOTP
        int verificationResult = TOTP.verTOTP(secretKey, incorrectTOTP);
        
        // Assert that the verification fails
        assertEquals(0, verificationResult, "The incorrect TOTP should not be verified.");
    }
}
