import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.time.LocalDate;

/**
 * Class for testing creation of a new doctor. It's not reflected in the db, so we are not updating any data on account db.
 */
public class NewDoctorTest {private Doctor doctor;

    @Before
    public void setUp() {
        // Existing user
        doctor = new Doctor("doctor@example.com","password123", "John",
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
    public void testAccessPersonalRecord(){

    }



}
