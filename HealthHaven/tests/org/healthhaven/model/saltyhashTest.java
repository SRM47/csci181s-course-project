package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.jupiter.api.Test;

public class saltyhashTest {

    @Test
    public void testGenSalt() {
        String salt1 = SaltyHash.genSalt();
        String salt2 = SaltyHash.genSalt();
        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2, "Generated salts should be unique");
    }

    @Test
    public void testPwHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "password123";
        String salt = SaltyHash.genSalt();
        
        String hashedPassword = SaltyHash.pwHash(password, salt);
        assertNotNull(hashedPassword, "Hashed password should not be null");
        
        String hashedPasswordAgain = SaltyHash.pwHash(password, salt);
        assertEquals(hashedPassword, hashedPasswordAgain, "Hashing the same password with the same salt should produce the same result");
    }

    @Test
    public void testCheckPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "password123";
        String salt = SaltyHash.genSalt();
        String hashedPassword = SaltyHash.pwHash(password, salt);
        
        assertTrue(SaltyHash.checkPassword(password, salt, hashedPassword), "Password check should return true for correct password");
        assertFalse(SaltyHash.checkPassword("wrongPassword", salt, hashedPassword), "Password check should return false for incorrect password");
    }

    @Test
    public void testPwHashWithInvalidKeySpec() {
        String password = "password123";
        String salt = ""; // Invalid salt
        
        assertThrows(InvalidKeySpecException.class, () -> {
            SaltyHash.pwHash(password, salt);
        }, "Should throw InvalidKeySpecException for decoding invalid salt");
    }

    @Test
    public void testCheckPasswordWithInvalidHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "password123";
        String salt = SaltyHash.genSalt();
        String invalidHash = "invalidHash";
        
        assertFalse(SaltyHash.checkPassword(password, salt, invalidHash), "Password check should return false for invalid hash");
    }
}
