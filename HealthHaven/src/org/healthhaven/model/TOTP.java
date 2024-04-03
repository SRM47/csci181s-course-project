package org.healthhaven.model;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

public class TOTP {
	
	final static TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();
	static Instant now = Instant.now();
	final static Duration timestep = totp.getTimeStep();

	
	
	public static Key secretKey() throws NoSuchAlgorithmException {
		
		System.out.println(now.toString());
		System.out.println(timestep.toString());
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
		
		final int macLengthInBytes = Mac.getInstance(totp.getAlgorithm()).getMacLength();
		keyGenerator.init(macLengthInBytes * 8);
		
		Key key = keyGenerator.generateKey();
		
		return key;
	}
	
	
	public static String genTOTP(Key key) throws InvalidKeyException {
		now = Instant.now();
		String pass = totp.generateOneTimePasswordString(key, now);
		return pass;	
	}
	
	
	public static int verTOTP(Key key, String pass) throws InvalidKeyException {
		
		now = Instant.now();
		
		for(int i=0; i<6; i++) {
			Instant checktime = now.minus(timestep.multipliedBy(i));
			
			String totpCheck = totp.generateOneTimePasswordString(key, checktime);
			
			if (pass.equals(totpCheck)){
				return 1;
			}
		}
		return 0;
		
	}
}



