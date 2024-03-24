
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
		if (index == -1) {
			return "NONE";
		}
		System.out.println(patientIds);
		System.out.println(patientId);
		System.out.println(index);
		return CSVHandler.readRowByIndex(databaseName, index);
	}

	public static String getAllRecords() {
		return CSVHandler.readAll(databaseName);
	}
	
	public static String getMeans() {
		ArrayList<String> heights = CSVHandler.readColumnValue(databaseName, 1);
		float mean_height = 0;
		for(String s: heights) {
			mean_height += Float.parseFloat(s);
		}
		mean_height /= heights.size();
		
		ArrayList<String> weights = CSVHandler.readColumnValue(databaseName, 2);
		float mean_weight = 0;
		for(String s: weights) {
			mean_weight += Float.parseFloat(s);
		}
		mean_weight /= weights.size();
		
		return "Mean Height: " + mean_height + "\nMean Weight: " + mean_weight + "\n";
	}

}
