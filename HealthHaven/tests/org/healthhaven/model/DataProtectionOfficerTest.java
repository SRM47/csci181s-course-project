package org.healthhaven.model;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.healthhaven.model.User.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataProtectionOfficerTest extends UserTest<DataProtectionOfficer>{
	@Override
    public DataProtectionOfficer createUser() {
        return new DataProtectionOfficer("example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }
	
    @Override
    protected Account getExpectedAccountType() {
        return Account.DPO;
    }
    
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testGenerateUserID() {
        // Ensuring that the generated userID starts with '1', indicating a Doctor's userID
        user.generateUserID();
        long userID = user.getUserID();
        assertTrue(Long.toString(userID).startsWith("4"), "DPO userID should start with 4.");
    }

}