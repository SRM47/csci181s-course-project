/**
 * 
 */
package org.healthhaven.model;

import java.util.Random;
import java.security.SecureRandom;

public class UserIdGenerator {
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_LENGTH = 8;

    private int length;
    private Random randomSource;

    /**
     * Constructor to create a UserIdGenerator with default options.
     */
    public UserIdGenerator() {
        this(DEFAULT_LENGTH);
    }

    /**
     *  Constructor to create a UserIdGenerator with a specified length.
     *
     *  @param length The desired length of the generated user IDs.
     */
    public UserIdGenerator(int length) {
        this(length, new Random()); // Uses standard Random for basic scenarios
    }

    /**
     * Constructor to create a UserIdGenerator with a specified length and random source.
     * This is useful for increased security.
     *
     * @param length The desired length of the generated user IDs.
     * @param randomSource A source of randomness (e.g., SecureRandom)
     */
    public UserIdGenerator(int length, Random randomSource) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        this.length = length;
        this.randomSource = randomSource;
    }

    /**
     * Generates a unique user ID.
     *
     * @return A randomly generated user ID string.
     */
    public String generate() {
        StringBuilder userIdBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = randomSource.nextInt(ALLOWED_CHARACTERS.length());
            userIdBuilder.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }

        return userIdBuilder.toString();
    }
}
