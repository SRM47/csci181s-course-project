package org.healthhaven.controller;

import org.healthhaven.model.*;
import org.json.JSONObject;
import org.json.JSONArray;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class PatientController {
	@FXML
    private TextArea recordTextArea;

    private Patient patient;

    public void setPatient(Patient patient) {
        this.patient = patient;
        handleViewRecords();
    }

    @FXML
    private void handleViewRecords() {
        // Assuming viewPatientRecord returns the medical record as a String
        String medicalRecord = patient.viewPatientRecord();
        if (medicalRecord==null) {
        	recordTextArea.setText("Error");
        }
        JSONObject json = new JSONObject(medicalRecord);
        if (json.getString("result").equals("SUCCESS")) {
        	recordTextArea.setText(parseMedicalRecord(json.getJSONArray("records")));
        }    
    }
    
    private String parseMedicalRecord(JSONArray records) {
    	// Parse the records JSON string into a JSON array
        JSONArray jsonArray = new JSONArray(records);

        // StringBuilder to build the resulting string
        StringBuilder result = new StringBuilder();

        // Iterate through each element in the JSON array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject record = jsonArray.getJSONObject(i);

            // Extract values from the JSON object
            int height = record.getInt("Height");
            int weight = record.getInt("Weight");
            String timestamp = record.getString("Timestamp");

            // Append formatted string to result
            result.append("Height: ").append(height)
                  .append(", Weight: ").append(weight)
                  .append(", Timestamp: ").append(timestamp)
                  .append("\n");  // Each record in one line
        }
        return result.toString();
    }
}
