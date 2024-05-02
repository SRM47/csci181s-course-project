package org.healthhaven.model;

import org.junit.jupiter.api.Test;

import java.security.Key;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class AttemptLimitTest {

    @Test
    public void withinLastHourTest() {
    	List<Long> wlhInputTest = new ArrayList<>();
    	List<Long> wlhOutputTest = new ArrayList<>();

    	
    	Long currentTime = Instant.now().getEpochSecond();
  
    	
    	wlhInputTest.add(1683011964L);
    	wlhInputTest.add(currentTime);
    	
    	wlhOutputTest.add(currentTime);
    	
    	List<Long> checkOut = AttemptLimit.timesWithinLastHour(wlhInputTest);
    	
    	

    	assertEquals(true, wlhOutputTest.equals(checkOut), "Timelists Match");
    }
	
    
    @Test
    public void withinAttemptTest() {
    	List<Long> wlhInputTest = new ArrayList<>();
    	List<Long> wlhOutputTest = new ArrayList<>();

    	
    	Long currentTime = Instant.now().getEpochSecond();
  
    	
    	wlhInputTest.add(1683011964L);
    	wlhInputTest.add(currentTime);
    	
    	wlhOutputTest.add(currentTime);
    	
    	
    	List<List<Long>> checkOut = AttemptLimit.withinAttemptLimit(wlhInputTest);
    	
    	assertEquals(true, wlhOutputTest.equals(checkOut.get(1)), "Timelists Correct Update");
    	assertEquals(1, checkOut.get(0).get(0), "Timelists Correct Int Return");
    	
    }
    
    @Test
    public void withinAttemptTest2() {
    	List<Long> wlhInputTest = new ArrayList<>();
    	List<Long> wlhOutputTest = new ArrayList<>();

    	
    	List<Long> wlhInputTest2 = AttemptLimit.addTime(wlhInputTest);
    	List<Long> wlhInputTest3 = AttemptLimit.addTime(wlhInputTest2);
    	List<Long> wlhInputTest4 = AttemptLimit.addTime(wlhInputTest3);
    	
    
    	
    	
    	List<List<Long>> checkOut = AttemptLimit.withinAttemptLimit(wlhInputTest4);
    	
    	assertEquals(true, wlhInputTest4.equals(checkOut.get(1)), "Timelists Correct Update");
    	assertEquals(0, checkOut.get(0).get(0), "Timelists Correct Int Return");
    	
    }
    
    
    @Test
    public void toStringTests() {
    	
    	
    	List<Long> wlhInputTest = new ArrayList<>();
    	
    	
    	List<Long> wlhInputTest2 = AttemptLimit.addTime(wlhInputTest);
    	List<Long> wlhInputTest3 = AttemptLimit.addTime(wlhInputTest2);
    	List<Long> wlhInputTest4 = AttemptLimit.addTime(wlhInputTest3);
    	
    	String wlhInpuStr = AttemptLimit.tListtoString(wlhInputTest4);
    	
    	List<Long> wlhInpuStrList = AttemptLimit.StringtotList(wlhInpuStr);
    	
    	
    
    	assertEquals(true, wlhInputTest4.equals(wlhInpuStrList), "Conversion List>String>List Okay");
    	
    }
	
	
	
	
//	@Test
//    public void testGenerateAndVerifyTOTP() throws Exception {
//        // Generate a secret key for testing
//        String secretKey = TOTP.genSecretKey();
//        
//        // Generate a TOTP using the secret key
//        String generatedTOTP = TOTP.genTOTP2(secretKey);
//        
//        // Verify the TOTP using the same key
//        boolean verificationResult = TOTP.verTOTP(secretKey, generatedTOTP);
//        
//        // Assert that the TOTP was verified successfully
//        assertEquals(true, verificationResult, "The TOTP should be verified successfully.");
//    }
//
//    @Test
//    public void testTOTPVerificationFailsForIncorrectTOTP() throws Exception {
//        // Generate a secret key for testing
//        String secretKey = TOTP.genSecretKey();
//        
//        // Use an incorrect TOTP
//        String incorrectTOTP = "12345678";
//        
//        // Attempt to verify the incorrect TOTP
//        boolean verificationResult = TOTP.verTOTP(secretKey, incorrectTOTP);
//        
//        // Assert that the verification fails
//        assertEquals(false, verificationResult, "The incorrect TOTP should not be verified.");
//    }
//    
//    @Test
//    public void testTOTPVerificationFailsForIncorrectTOTPKey() throws Exception {
//        // Generate a secret key for testing
//        String secretKey = TOTP.genSecretKey();
//        
//        String generatedTOTP = TOTP.genTOTP2(secretKey);
//        
//        String secretKey2 = TOTP.genSecretKey();
//        
//        // Use an incorrect TOTP
////        String incorrectTOTP = "12345678";
//        
//        // Attempt to verify the incorrect TOTP
//        boolean verificationResult = TOTP.verTOTP(secretKey, generatedTOTP);
//        boolean verificationResult2 = TOTP.verTOTP(secretKey2, generatedTOTP);
//        
//        // Assert that the verification fails
//        assertEquals(true, verificationResult, "The incorrect TOTP should not be verified.");
//        assertEquals(false, verificationResult2, "The incorrect TOTP should not be verified.");
//    }
}
