import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;


public class AccountCreationServiceTest {

//	need mockito

    static Stream<User.Account> userTypesProvider() {
        return Stream.of(User.Account.DOCTOR, User.Account.PATIENT, User.Account.DATA_ANALYST, User.Account.DPO, User.Account.SUPERADMIN);
    }

    /**
     * For each user type, it test a creation of user instance.
     * @param userType
     */
    @ParameterizedTest
    @MethodSource("userTypesProvider")
    void testCreateUserInstance(User.Account userType) {
        User user = AccountCreationService.createUserInstance(userType, "email@example.com", "password", "John", "Doe", "123 Main St", LocalDate.now());

        switch (userType) {
            case DOCTOR:
                Assertions.assertTrue(user instanceof Doctor);
                break;
            case PATIENT:
                Assertions.assertTrue(user instanceof Patient);
                break;
            case DATA_ANALYST:
                Assertions.assertTrue(user instanceof DataAnalyst);
                break;
            case DPO:
                Assertions.assertTrue(user instanceof DataProtectionOfficer);
                break;
            case SUPERADMIN:
                Assertions.assertTrue(user instanceof Superadmin);
                break;
            default:
                Assertions.fail("Unexpected user type");
        }
    }

    /**
     * Test a successful case of authenticating a new user (make sure the same email does not exist in db)
     */
    @Test
    void testAuthenticateNewUser() {
        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            // Define the behavior for the static method
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("Success");

            // Call the method under test
            String response = AccountCreationService.authenticateNewUser("email@example.com");

            // Assertions
            Assertions.assertEquals("SUCCESS", response);

            // Verify the static method was called with the expected message
            mockedStatic.verify(() -> ServerCommunicator.communicateWithAccountServer("NEW_ACCOUNT email@example.com"));
        }
    }

    /**
     * Test successful case of updating the account record on DB.
     */
    @Test
    void testUpdateAccountDB() {
        // Prepare the inputs
        User.Account userType = User.Account.DOCTOR;
        String email = "doctor@example.com";
        String password = "password";
        String first_name = "John";
        String last_name = "Doe";
        String address = "123 Main St";
        LocalDate dob = LocalDate.of(1980, 1, 1);

        // Expected format of the message sent to the server
        // Note: The actual timestamp can't be predicted, so it's not included in the verification
        String expectedStartOfMessage = String.format("CREATE_ACCOUNT %s %s %s %s %s %s %s",
                userType.getAccountName(), email, password, first_name,
                last_name, address, dob.format(DateTimeFormatter.ISO_LOCAL_DATE));

        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
            // Assume the server always responds with "Success" for simplicity
            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("SUCCESS");

            // Call the method under test
            String result = AccountCreationService.updateAccountDB(userType, email, password, first_name, last_name, address, dob);

            // Verify the server communicator was called once
            mockedStatic.verify(() -> ServerCommunicator.communicateWithAccountServer(anyString()), times(1));

            // Assert the response is as expected (based on mocked behavior)
            Assertions.assertEquals("SUCCESS", result);

        }
    }
}
