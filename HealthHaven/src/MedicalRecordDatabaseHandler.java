/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class MedicalRecordDatabaseHandler {

	public static String updatePatientMedicalRecords(String patientID, String height, String weight, String timestamp) {
		return "Updated patient medical record.";

	}

	public static String viewPatientMedicalRecord(String patientID) {
		return "This is the Patient's medical record.";

	}

	public static String getAllRecords() {
		return "All records.";

	}

}
