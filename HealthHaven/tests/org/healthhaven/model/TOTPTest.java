package org.healthhaven.model;

import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

public class TOTPTest {

    @Test
    public void testGenerateAndVerifyTOTP() throws Exception {
        // Generate a secret key for testing
        String secretKey = TOTP.genSecretKey();
        
        // Generate a TOTP using the secret key
        String generatedTOTP = TOTP.genTOTP2(secretKey);
        
        // Verify the TOTP using the same key
        boolean verificationResult = TOTP.verTOTP(secretKey, generatedTOTP);
        
        // Assert that the TOTP was verified successfully
        assertEquals(true, verificationResult, "The TOTP should be verified successfully.");
    }

    @Test
    public void testTOTPVerificationFailsForIncorrectTOTP() throws Exception {
        // Generate a secret key for testing
        String secretKey = TOTP.genSecretKey();
        
        // Use an incorrect TOTP
        String incorrectTOTP = "12345678";
        
        // Attempt to verify the incorrect TOTP
        boolean verificationResult = TOTP.verTOTP(secretKey, incorrectTOTP);
        
        // Assert that the verification fails
        assertEquals(false, verificationResult, "The incorrect TOTP should not be verified.");
    }
    
    @Test
    public void testTOTPVerificationFailsForIncorrectTOTPKey() throws Exception {
        // Generate a secret key for testing
        String secretKey = TOTP.genSecretKey();
        
        String generatedTOTP = TOTP.genTOTP2(secretKey);
        
        String secretKey2 = TOTP.genSecretKey();
        
        // Use an incorrect TOTP
//        String incorrectTOTP = "12345678";
        
        // Attempt to verify the incorrect TOTP
        boolean verificationResult = TOTP.verTOTP(secretKey, generatedTOTP);
        boolean verificationResult2 = TOTP.verTOTP(secretKey2, generatedTOTP);
        
        // Assert that the verification fails
        assertEquals(true, verificationResult, "The incorrect TOTP should not be verified.");
        assertEquals(false, verificationResult2, "The incorrect TOTP should not be verified.");
    }
}
