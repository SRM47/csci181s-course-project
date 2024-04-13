package org.healthhaven.model;
import org.healthhaven.model.User.Account;
import org.healthhaven.server.ServerCommunicator;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class totpTests{

    public String key;
    public String key2;
    
    
//    @Test
//    public void testGenTOTP() throws InvalidKeyException, NoSuchAlgorithmException {
//    	String pass = TOTP.genTOTP(key);
//        System.out.println("pass" + pass);
//    }
    
    @Test
    public void testGenTOTP2() throws InvalidKeyException, NoSuchAlgorithmException, InterruptedException {
    	key = TOTP.genSecretKey();
    	System.out.println("key"+ key);
    	String pass = TOTP.genTOTP2(key);
    	String pass2 = TOTP.genTOTP2(key);
        System.out.println("pass " + pass + "pass 2" + pass2);
        Thread.sleep(30001);
        String pass3 = TOTP.genTOTP2(key);
        System.out.println("pass3 " + pass3);
        key2 = TOTP.genSecretKey();
        System.out.println("key"+ key2);
        String pass4 = TOTP.genTOTP2(key);
        System.out.println("pass4 " + pass4);
        
        boolean test1 = TOTP.verTOTP(key,pass);
        System.out.println("test1: " + String.valueOf(test1));
        assertTrue(test1);
        boolean test2 = TOTP.verTOTP(key,pass2);
        System.out.println("test2: " + String.valueOf(test2));
        assertTrue(test2);
        boolean test3 = TOTP.verTOTP(key2,pass);
        System.out.println("test3: " + String.valueOf(test3));
        assertFalse(test3);
        boolean test4 = TOTP.verTOTP(key2,pass2);
        System.out.println("test2: " + String.valueOf(test4));
        assertFalse(test4);

    }
    
    
}
