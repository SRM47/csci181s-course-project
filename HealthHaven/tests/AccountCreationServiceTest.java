import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
	
	@BeforeEach
    public void setUp() {
		try {
			Server server = new Server(8889, 30, 100);
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

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

//    /**
//     * Test a successful case of authenticating a new user (make sure the same email does not exist in db)
//     */
//    @Test
//    void testAuthenticateNewUser() {
//        try (MockedStatic<ServerCommunicator> mockedStatic = Mockito.mockStatic(ServerCommunicator.class)) {
//            // Define the behavior for the static method
//            mockedStatic.when(() -> ServerCommunicator.communicateWithAccountServer(anyString())).thenReturn("SUCCESS");
//
//            // Call the method under test
//            String response = AccountCreationService.doesAccountExist("email@example.com");
//
//            // Assertions
//            Assertions.assertEquals("SUCCESS", response);
//
//            // Verify the static method was called with the expected message
//            mockedStatic.verify(() -> ServerCommunicator.communicateWithAccountServer("NEW_ACCOUNT email@example.com"));
//        }
//    }

    
}
