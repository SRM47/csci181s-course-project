import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class AccountCreationService {
    public static User createAccount(Scanner scanner, User.Account userType) {
        System.out.print("Enter the email: ");
        String email = scanner.nextLine();

        String password = "";
        System.out.print("Enter the password: ");
        password = scanner.nextLine();

//        if (userType == User.Account.DOCTOR || userType == User.Account.SUPERADMIN){
//            password = PasswordGenerator.generate(10);
//        } else{
//            System.out.print("Enter the password: ");
//            password = scanner.nextLine();
//        }

        // First communication with the server to check if the email already exists
        if (authenticateNewUser(email)) {
            System.out.print("Enter the first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter the last name: ");
            String lastName = scanner.nextLine();
            System.out.print("Enter the address: ");
            String address = scanner.nextLine();
            LocalDate dateOfBirth = null;
            while (dateOfBirth == null) {
                System.out.print("Enter the date of birth (yyyy-mm-dd): ");
                String dobInput = scanner.nextLine();
                try {
                    dateOfBirth = LocalDate.parse(dobInput, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
                }
            }

            //Account creation
            boolean proceed = false;
            while (!proceed) {
                System.out.println("Do you want to create a new account? 1. Save 2. Quit: ");
                int subChoice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline left by nextInt()

                if (subChoice == 1) {
                    proceed = true; // Break out of the loop and continue with account creation
                    String serverResponse = updateAccountDB(userType, email, password, firstName, lastName, address, dateOfBirth);
                    if (serverResponse.equals("SUCCESS")){
                        return createUserInstance(userType, email, password, firstName, lastName, address, dateOfBirth);
                    }
                    System.out.println("Error creating an account.");
                    return null;
                } else if (subChoice == 2) {
                    System.out.println("Account creation canceled.");
                    return null; // Exit the method indicating that the account was not created
                } else {
                    System.out.println("Invalid choice. Please enter 1 to save or 2 to quit.");
                }
            }
        }
        System.out.println("Email address already exists or error connecting with the server.");
        return null;

    }

    private static int selectAccountType(Scanner scanner) {
        int accountType = 0;
        while (accountType < 1 || accountType > 5) {
            System.out.println("Select an account type 1. Patient 2. Doctor 3. Data Science Analyst 4. Data Protection Officer 5. Super Admin: ");
            try {
                accountType = Integer.parseInt(scanner.nextLine());
                if (accountType < 1 || accountType > 5) {
                    System.out.println("Invalid account type. Please select a number between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return accountType;
    }


    private static User createUserInstance(User.Account userType, String email, String password, String legal_first_name, String legal_last_name, String address,
                                           LocalDate dob) {


        return switch (userType) {
            case DOCTOR -> new Doctor(email, password, legal_first_name, legal_last_name, address, dob);
            case PATIENT -> new Patient(email, password, legal_first_name, legal_last_name, address, dob);
            case DATA_ANALYST -> new DataAnalyst(email, password, legal_first_name, legal_last_name, address, dob);
            case DPO -> new DataProtectionOfficer(email, password, legal_first_name, legal_last_name, address, dob);
            case SUPERADMIN -> new Superadmin(email, password, legal_first_name, legal_last_name, address, dob);
            default -> null;
        };
    }

    private static boolean authenticateNewUser(String email){
        String message = String.format("NEW_ACCOUNT %s", email);
        System.out.println("Message: " + message);
        String serverResponse = ServerCommunicator.communicateWithAccountServer(message);
        System.out.println("Server response: " + serverResponse);

        return (serverResponse.equals("VALID"));
    }

    private static String updateAccountDB(User.Account userType, String email, String password, String first_name, String last_name, String address, LocalDate dob){
        Instant timestamp = Instant.now();
        String account = userType.getAccountName();
        String message = String.format(("CREATE_ACCOUNT %s %s %s %s %s %s %s %s"), account, email, password, first_name, last_name, address, dob, timestamp.toString());
        return ServerCommunicator.communicateWithAccountServer(message);
    }

}
