import java.util.ArrayList;

/**
 * 
 */

/**
 * @author sameermalik
 *
 */
public class MedicalRecordDatabaseHandler {
	
	private static String databaseName = "medical_records.csv";

	public static String updatePatientMedicalRecords(String patientId, String height, String weight, String timestamp) {
		String[] newData = { patientId, height, weight, timestamp };
		boolean success = CSVHandler.appendToCSV(databaseName, newData);
		return success ? "SUCCESS" : "FAILURE";
	}

	public static String viewPatientMedicalRecord(String patientId) {
		ArrayList<String> patientIds = CSVHandler.readColumnValue(databaseName, 0);
		int index = patientIds.indexOf(patientId);
		System.out.println(patientIds);
		System.out.println(patientId);
		System.out.println(index);
		return CSVHandler.readRowByIndex(databaseName, index);
	}

	public static String getAllRecords() {
		return CSVHandler.readAll(databaseName);
	}

}
