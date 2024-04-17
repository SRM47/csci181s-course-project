package org.healthhaven.model;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.jupiter.api.Test;

public class saltyhashTest{

//    public String key;
//    public String key2;
    
    
//    @Test
//    public void testGenTOTP() throws InvalidKeyException, NoSuchAlgorithmException {
//    	String pass = TOTP.genTOTP(key);
//        System.out.println("pass" + pass);
//    }
    
//    @Test
//    public void testGenTOTP2() throws InvalidKeyException, NoSuchAlgorithmException, InterruptedException {
//    	key = TOTP.genSecretKey();
//    	System.out.println("key"+ key);
//    	String pass = TOTP.genTOTP2(key);
//    	String pass2 = TOTP.genTOTP2(key);
//        System.out.println("pass " + pass + "pass 2" + pass2);
//        Thread.sleep(30001);
//        String pass3 = TOTP.genTOTP2(key);
//        System.out.println("pass3 " + pass3);
//        key2 = TOTP.genSecretKey();
//        System.out.println("key"+ key2);
//        String pass4 = TOTP.genTOTP2(key);
//        System.out.println("pass4 " + pass4);
//        
//        boolean test1 = TOTP.verTOTP(key,pass);
//        System.out.println("test1: " + String.valueOf(test1));
//        assertTrue(test1);
//        boolean test2 = TOTP.verTOTP(key,pass2);
//        System.out.println("test2: " + String.valueOf(test2));
//        assertTrue(test2);
//        boolean test3 = TOTP.verTOTP(key2,pass);
//        System.out.println("test3: " + String.valueOf(test3));
//        assertFalse(test3);
//        boolean test4 = TOTP.verTOTP(key2,pass2);
//        System.out.println("test2: " + String.valueOf(test4));
//        assertFalse(test4);
//
//    }
    
    @Test
    public void testSaltyHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
    	String salt1 = SaltyHash.genSalt();
    	String salt2 = SaltyHash.genSalt();

    	System.out.println("salt1 " + salt1);
    	System.out.println("salt2 " + salt2);
    	
    	
    	String hash1 = SaltyHash.pwHash("Test",salt1);
    	String hash2 = SaltyHash.pwHash("Test",salt2);
    	String hash3 = SaltyHash.pwHash("Test1",salt1);
    	String hash4 = SaltyHash.pwHash("Test1",salt2);
    	
  
    	System.out.println("hash1 " + hash1);
    	System.out.println("hash2 " + hash2);
    	System.out.println("hash3 " + hash3);
    	System.out.println("hash4 " + hash4);
    	
    	
    	Boolean testout1 = SaltyHash.checkPassword("Test", salt1, hash1);
    	Boolean testout2 = SaltyHash.checkPassword("Test", salt1, hash2);
    	Boolean testout3 = SaltyHash.checkPassword("Test1", salt1, hash3);
    	Boolean testout4 = SaltyHash.checkPassword("Test1", salt2, hash2);
    	
    	System.out.println("testout1 " + testout1);
    	System.out.println("testout2 " + testout2);
    	System.out.println("testout3 " + testout3);
    	System.out.println("testout4 " + testout4);
    	
    	
    }
    
    
}
