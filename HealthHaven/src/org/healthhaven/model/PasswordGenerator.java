package org.healthhaven.model;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.*;

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
    public static String generate(int length) {
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
    

    public static int compromiseChecker(String password) throws IOException {
    	
    	String sHash = sha1Hash(password);

    	String first5 = sHash.substring(0,5);
    	System.out.println(first5);
    	String suffix = sHash.substring(5);
    	System.out.println(suffix);
    	
    	String apiUrl = "https://api.pwnedpasswords.com/range/" + first5;

    	// Create a URL object from the API URL string
        URL url = new URL(apiUrl);

        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to GET
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line+",");
        }

        reader.close();

        connection.disconnect();
        
        String[] responseList = response.toString().split(",");
        
        
    	for(String item: responseList) {
    		//System.out.println("checking" + item); 
    		if(item.contains(suffix.toUpperCase())) {
    			System.out.println("compromised"); 
    			return 1;
    		}
    	}
    	
    	return 2;        

    }



    //ChatGPT
    public static String sha1Hash(String password) {
    	try {
            // Create MessageDigest instance for SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");


            // Add the input string bytes to the digest
            md.update(password.getBytes());

            // Compute the hash
            byte[] hashBytes = md.digest();

            // Convert hash bytes to hexadecimal representation
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            String hash = sb.toString();

            return hash;

        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-1 algorithm not available.");
            e.printStackTrace();

            return "";
        }
    }
    
    //Chat-GPT
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
}
