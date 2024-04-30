package org.healthhaven.model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SaltyHash {
	
	private static final int ITERATIONS = 20000;
	private static final int KEY_LENGTH = 256;
	
	public static void Test() {}
	
	public static String genSalt() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] randomBytes1 = new byte[16];
		secureRandom.nextBytes(randomBytes1);
		return Base64.getEncoder().encodeToString(randomBytes1);
	}
	
//	//ChatGPT and StackOverflow
//	public static String pwHash(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
//		
//		
//		char[] password2 = password.toCharArray();
//		byte[] salt2 = Base64.getDecoder().decode(salt);
//		
//		
//		PBEKeySpec spec = new PBEKeySpec(password2, salt2, ITERATIONS, KEY_LENGTH);
//
//		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//		byte[] rHash = skf.generateSecret(spec).getEncoded();
//		
//		return Base64.getEncoder().encodeToString(rHash);
//	}
	
	public static String pwHash(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return hashWithSalt(password, salt);
	}
	
	
	
	//ChatGPT and StackOverflow
	public static String hashWithSalt(String plaintext, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		char[] password2 = plaintext.toCharArray();
		byte[] salt2 = Base64.getDecoder().decode(salt);
		
		
		PBEKeySpec spec = new PBEKeySpec(password2, salt2, ITERATIONS, KEY_LENGTH);

		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		byte[] rHash = skf.generateSecret(spec).getEncoded();
		
		return Base64.getEncoder().encodeToString(rHash);
	}


	public static boolean checkPassword(String password, String salt, String expectedHash) throws NoSuchAlgorithmException, InvalidKeySpecException{
		if (password == null || salt == null || expectedHash == null) {
			return false;
		}
		
		String testHash = pwHash(password, salt);
		
		return testHash.equals(expectedHash);
	
		
	}
}