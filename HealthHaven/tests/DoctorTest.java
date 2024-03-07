import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.time.Instant;
import java.time.LocalDate;


public class DoctorTest {private Doctor doctor;

    @Before
    public void setUp() {
        // Existing user
        doctor = new Doctor(1234567890,"doctor@example.com","password123", "John",
                "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }

    @Test
    public void testGetEmail() {
        assertEquals("doctor@example.com", doctor.getEmail());
    }

    @Test
    public void testGetLegalFirstName() {
        assertEquals("John", doctor.getLegal_first_name());
    }

    @Test
    public void testGetLegalLastName() {
        assertEquals("Doe", doctor.getLegal_last_name());
    }

    @Test
    public void testGetAddress() {
        assertEquals("123 Main St", doctor.getAddress());
    }

    @Test
    public void testGetDob() {
        assertEquals(LocalDate.of(1980, 1, 1), doctor.getDob());
    }

    @Test
    public void testUpdatePatientRecordOnDB(){
        String serverResponse = doctor.updatePatientRecordOnDB((long) doctor.getUserID(), "111","111");
        //Instant timestamp = Instant.now();
        assertEquals("SUCCESS", serverResponse);
    }

    @Test
    public void testViewPatientRecord(){
        String serverResponse = doctor.viewPatientRecord((long) doctor.getUserID());
        //Here we have to first see what's stored in database and the format that server returns.
        assertEquals("", serverResponse);
    }


}
