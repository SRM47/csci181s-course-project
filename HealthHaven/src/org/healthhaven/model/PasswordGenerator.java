package org.healthhaven.model;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordGenerator {

    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = UPPER_CASE.toLowerCase();
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%&*";
    private static final String ALL_CHARS = UPPER_CASE + LOWER_CASE + DIGITS + SPECIAL_CHARS;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random password.
     *
     * @param length The desired length of the password.
     * @return A randomly generated password.
     */
    public static String generate() {
    	int length = 8;
        if (length < 4) {
           throw new IllegalArgumentException("Password length must be at least 4 characters.");
        }

        StringBuilder password = new StringBuilder(length);

        // Ensure the password includes at least one character of each type
        password.append(UPPER_CASE.charAt(random.nextInt(UPPER_CASE.length())));
        password.append(LOWER_CASE.charAt(random.nextInt(LOWER_CASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Fill the remaining spots with random characters from all types
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle the password to avoid predictable patterns
        return shufflePassword(password.toString());
    }

    /**
     * Shuffles the characters in the password to avoid predictable patterns.
     *
     * @param password The password to shuffle.
     * @return A new string with the characters of the password shuffled.
     */
    private static String shufflePassword(String password) {
        char[] array = password.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return new String(array);
    }
    
    
    public static int passwordStrength(String password, String fn, String ln, LocalDate dob) {
    	 
//    	// At least 10 characters
//        if (password.length() < 10) {
//            return false;
//        }
//        
//        // At least one uppercase letter
//        if (!password.matches(".*[A-Z].*")) {
//            return false;
//        }
//        
//        // At least one lowercase letter
//        if (!password.matches(".*[a-z].*")) {
//            return false;
//        }
//        
//        // At least one digit
//        if (!password.matches(".*\\d.*")) {
//            return false;
//        }
//        
//        // At least one special character
//        Pattern specialChars = Pattern.compile("[^a-zA-Z0-9]");
//        Matcher matcher = specialChars.matcher(password);
//        if (!matcher.find()) {
//            return false;
//        }
        
        // first name not in password
        if(password.toLowerCase().contains(fn.toLowerCase())) {
        	return 2;
        }
        
        // last name not in password
        if(password.toLowerCase().contains(ln.toLowerCase())) {
        	return 2;
        }
        
        // make sure year not in password
        if(password.contains(Integer.toString(dob.getYear()))){
        	return 3;
        }
        
        // further date of birth check
        String bd1 = Integer.toString(dob.getMonthValue()) + Integer.toString(dob.getDayOfMonth());
        String bd2 = Integer.toString(dob.getDayOfMonth()) + Integer.toString(dob.getMonthValue());
        
        if(password.contains(bd1) || password.contains(bd2)) {
        	return 3;
        }
        
        return 1;
    	
    }
}
