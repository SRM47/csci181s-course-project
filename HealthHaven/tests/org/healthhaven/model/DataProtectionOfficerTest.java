package org.healthhaven.model;

import java.time.LocalDate;

import org.healthhaven.model.User.Account;

public class DataProtectionOfficerTest extends UserTest<DataProtectionOfficer> {

    @Override
    public DataProtectionOfficer createUser() {
        // Returns an instance of DataProtectionOfficer with predefined values
        // Assuming "userId" is a String that represents a unique identifier for the user
        return new DataProtectionOfficer("DPO123", "example@example.com", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }

    @Override
    protected Account getExpectedAccountType() {
        // Specifies the expected account type for DataProtectionOfficer
        return Account.DPO;
    }

    // Here you could add tests for any DataProtectionOfficer-specific functionality,
    // but given the current implementation, only inherited User functionality and properties need to be tested,
    // which are already covered by the tests in UserTest.
}
