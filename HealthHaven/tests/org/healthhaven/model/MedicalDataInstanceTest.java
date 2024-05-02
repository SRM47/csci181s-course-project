package org.healthhaven.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MedicalDataInstanceTest {

    private MedicalDataInstance instance;
    private float testHeight = 175.5f; // Example height in cm
    private float testWeight = 65.2f; // Example weight in kg
    private Date testTimestamp;
    private String testIdentifier = "test123";

    @BeforeEach
    void setUp() {
        // Using current date for simplicity
        testTimestamp = Date.from(Instant.now());
        instance = new MedicalDataInstance(testHeight, testWeight, testTimestamp, testIdentifier);
    }

    @Test
    void testConstructorAndGetter() {
        assertEquals(testHeight, instance.getHeight(), "Height should match the constructor input");
        assertEquals(testWeight, instance.getWeight(), "Weight should match the constructor input");
        assertEquals(testTimestamp, instance.getTimestamp(), "Timestamp should match the constructor input");
        assertEquals(testIdentifier, instance.getIdentifier(), "Identifier should match the constructor input");
    }

    @Test
    void testPropertyMethods() {
        assertNotNull(instance.heightProperty(), "Height property should not be null");
        assertNotNull(instance.weightProperty(), "Weight property should not be null");
        assertNotNull(instance.timestampProperty(), "Timestamp property should not be null");
        assertNotNull(instance.identifierProperty(), "Identifier property should not be null");
    }

//    @Test
//    void testToString() {
//        String expectedString = "MedicalDataInstance [height=" + testHeight + ", weight=" + testWeight + ", timestamp=" + testTimestamp + ", identifier=" + testIdentifier + "]";
//        assertEquals(expectedString, instance.toString(), "toString should return the correct string representation");
//    }
}
