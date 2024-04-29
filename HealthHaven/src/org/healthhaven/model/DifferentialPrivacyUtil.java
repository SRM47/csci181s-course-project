package org.healthhaven.model;

import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class DifferentialPrivacyUtil {

    private static final Random random = new Random();

    public static double generateLaplaceNoise(double epsilon, double sensitivity) {
        double scale = sensitivity / epsilon;
        double u = 0.5 - random.nextDouble();  
        return -scale * Math.signum(u) * Math.log(1 - 2 * Math.abs(u));
    }
    
    
    public static String applyDP(JSONArray dataArray) {
        JSONArray noisyDataArray = new JSONArray();
        HashMap<String, String> patientIdMap = new HashMap<>();

        double epsilon = 0.1;  // Privacy parameter
        double sensitivity = 1.0;  // Sensitivity for height and weight
        
        
//        System.out.println(patientData.toString());

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject patientData = dataArray.getJSONObject(i);
            double height = patientData.getDouble("height");
            double weight = patientData.getDouble("weight");
            String entryDate = patientData.getString("entryDate");  // Retrieve the entry date
            String patientID = patientData.getString("patientid");

            // Check if patientID already has a pseudonym; if not, generate one
            String pseudonym = patientIdMap.get(patientID);
            if (pseudonym == null) {
                pseudonym = "patient " + (100000 + random.nextInt(900000));  // Generate a pseudonym in the range 100000-999999
                patientIdMap.put(patientID, pseudonym);
            }

            // Apply noise directly to each height and weight
            double noisyHeight = height + generateLaplaceNoise(epsilon, sensitivity);
            double noisyWeight = weight + generateLaplaceNoise(epsilon, sensitivity);

            // Create a new JSON object for the noisy data
            JSONObject noisyPatientData = new JSONObject();
            noisyPatientData.put("height", noisyHeight);
            noisyPatientData.put("weight", noisyWeight);
            noisyPatientData.put("entryDate", entryDate);
            noisyPatientData.put("patientID", pseudonym);  // Use the pseudonym instead of the real ID

            noisyDataArray.put(noisyPatientData);
        }

        // Wrap the noisy data array within an "entries" object
        JSONObject result = new JSONObject();
        result.put("entries", noisyDataArray);
        return result.toString();
    }
}
