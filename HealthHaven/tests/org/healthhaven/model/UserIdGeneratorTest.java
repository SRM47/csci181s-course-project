package org.healthhaven.model;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

public class UserIdGeneratorTest {

    @Test
    public void testGenerateDefaultLengthId() {
        UserIdGenerator generator = new UserIdGenerator();
        String userId = generator.generate();

        assertNotNull(userId, "Generated user ID should not be null.");
        assertEquals(8, userId.length(), "Default length user ID should be 8 characters long.");
        assertTrue(userId.matches("[A-Z0-9]+"), "User ID should only contain allowed characters.");
    }

    @Test
    public void testGenerateCustomLengthId() {
        int customLength = 12;
        UserIdGenerator generator = new UserIdGenerator(customLength);
        String userId = generator.generate();

        assertNotNull(userId, "Generated user ID should not be null.");
        assertEquals(customLength, userId.length(), "Custom length user ID should match specified length.");
        assertTrue(userId.matches("[A-Z0-9]+"), "User ID should only contain allowed characters.");
    }

    @Test
    public void testGenerateIdWithSecureRandom() {
        int length = 10;
        SecureRandom secureRandom = new SecureRandom();
        UserIdGenerator generator = new UserIdGenerator(length, secureRandom);
        String userId = generator.generate();

        assertNotNull(userId, "Generated user ID should not be null.");
        assertEquals(length, userId.length(), "User ID length should match the specified length.");
        assertTrue(userId.matches("[A-Z0-9]+"), "User ID should only contain allowed characters.");
    }

    @Test
    public void testInvalidLengthThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new UserIdGenerator(-1);
        }, "Constructing UserIdGenerator with negative length should throw IllegalArgumentException.");
    }
}
