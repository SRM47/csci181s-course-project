package org.healthhaven.model;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

public class TOTP {
	
	static Duration duration = Duration.ofSeconds(30);
	
//	final static TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(duration,8,"HmacSHA512");
//	static Instant now = Instant.now();
//	final static Duration timestep = totp.getTimeStep();
	
	
	
	
//	public static Key secretKey() throws NoSuchAlgorithmException {
//		
//		System.out.println(now.toString());
//		System.out.println(timestep.toString());
//		
//		KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
//		
//		final int macLengthInBytes = Mac.getInstance(totp.getAlgorithm()).getMacLength();
//		keyGenerator.init(macLengthInBytes * 8);
//		
//		Key key = keyGenerator.generateKey();
//		System.out.println(secretKeytoString(key));
//		
//		return key;
//	}
	
	//With inspiration from ChatGPT
	public static String genSecretKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] randomBytes1 = new byte[32];
		secureRandom.nextBytes(randomBytes1);
		return Base64.getEncoder().encodeToString(randomBytes1);
	}
	
	
	public static String genTOTP2(String key) {
		long timestep = 30;
		long timeCounter = Instant.now().getEpochSecond() / timestep;
		byte [] timeCounterBytes = ByteBuffer.allocate(8).putLong(timeCounter).array();
		byte [] keyBytes = key.getBytes();
		
		byte[] hmacDigest = hmacSHA256(keyBytes, timeCounterBytes);
		int truncatedHash = hashTruncate(hmacDigest);
		
		int clearsignOTP = truncatedHash & 0x7FFFFFFF;
		int rOTP = (int) (clearsignOTP % Math.pow(10,6));
		
		return String.valueOf(rOTP);
	}
	
	//genTOTP2 with time input
	public static String genTOTP2(String key, Instant intime) {
		long timestep = 30;
		long timeCounter = intime.getEpochSecond() / timestep;
		byte [] timeCounterBytes = ByteBuffer.allocate(8).putLong(timeCounter).array();
		byte [] keyBytes = key.getBytes();
		
		byte[] hmacDigest = hmacSHA256(keyBytes, timeCounterBytes);
		int truncatedHash = hashTruncate(hmacDigest);
		
		int clearsignOTP = truncatedHash & 0x7FFFFFFF;
		int iOTP = (int) (clearsignOTP % Math.pow(10,6));
		
		String sOTP = String.valueOf(iOTP);
		int lString = sOTP.length();
		
		if (lString == 6){
			return sOTP;
		} else {
			for(int i = 0; i < (6 - lString); i++) {
				sOTP = "0" + sOTP;
			}
			return sOTP;
		}
	
	}
	
	
	public static byte[] hmacSHA256(byte[] key, byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(data);
			return digest.digest(key);
		} catch (NoSuchAlgorithmException e){
			e.printStackTrace();
            return null;
		}
	}
	
	public static int hashTruncate(byte[] fullHash) {
		
		// extract last four bits from hash
		int offset = fullHash[fullHash.length - 1] & 0x0F;
		
		int truncatedhash = 0;
		for (int i = 0; i < 4; i++) {
			//operate in eight bit sections
			truncatedhash <<= 8;
			//add bits to truncated hash
			truncatedhash |= (fullHash[offset + i] & 0xFF);
		}
		
		return truncatedhash;
	}
	
//	public static String genTOTP(Key key) throws InvalidKeyException {
//		now = Instant.now();
//		String pass = totp.generateOneTimePasswordString(key, now);
//		return pass;	
//	}
//	
	
	public static boolean verTOTP(String key, String pass){
		
		Instant now = Instant.now();
		
		for(int i=0; i<6; i++) {
			
			Instant checktime = now.minus(duration.multipliedBy(i));
			
			String totpCheck = genTOTP2(key, checktime);
			System.out.println("totpcheck: " + totpCheck);
			
			if (pass.equals(totpCheck)){
				return true;
			}
		}
		return false;
		
	}
	
//	//Baeldung
//	public static String secretKeytoString(Key key) {
//		byte[] rawData = key.getEncoded();
//		String encodedKey = Base64.getEncoder().encodeToString(rawData);
//		return encodedKey;
//	}
}



