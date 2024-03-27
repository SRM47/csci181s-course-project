package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class PatientTest extends UserTest<Patient> {

    @Override
    public Patient createUser() {
        return new Patient(1234567890,"example@example.com","password123", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }
    
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }
    
    @Test
    public void testGenerateUserID() {
        user.generateUserID(); //use user since setUp
        long userID = user.getUserID();
        assertTrue(String.valueOf(userID).startsWith("2"), "Patient userID should start with 2.");
    }

    //TODO: Figure this out
    @Test
    public void testViewPatientRecord(){

    }
}