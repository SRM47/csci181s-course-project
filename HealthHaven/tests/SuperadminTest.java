import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class SuperadminTest extends UserTest<Superadmin> {

    @Override
    public Superadmin createUser() {
        // Create a Superadmin instance with known attributes for testing.
        return new Superadmin("example@example.com", "password123", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        // Additional setup if needed.
    }

    @Test
    void testGenerateUserID() {
        user.generateUserID();
        long userID = user.getUserID();
        assertTrue(String.valueOf(userID).startsWith("5"), "Superadmin userID should start with 5.");
    }
}
//
//    @Test
//    void testSelectAccountType() {
//        // Simulate selecting a Doctor account type.
//        String input = "1\n";
//        System.setIn(new ByteArrayInputStream(input.getBytes()));
//        Scanner scanner = new Scanner(System.in);
//        
//        User.Account selectedType = Superadmin.selectAccountType(scanner);
//        assertEquals(User.Account.DOCTOR, selectedType, "Should select a Doctor account type.");
//
//        scanner.close();
//        System.setIn(System.in); // Reset System.in to its original state.
//    }
//
////    @Test
////    void testViewAccountList() {
////        // Assuming ServerCommunicator can be mocked or its call can be intercepted.
////        // The actual implementation of mocking static methods or external systems
////        // depends on the ability to inject mocks or refactor the design for testability.
////    }
//}
