package org.healthhaven.controller;

import java.time.LocalDate;

import org.healthhaven.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class DoctorController {

	@FXML
	private TextField newPatientEmailField;
	@FXML
	private DatePicker dobDatePicker;
	@FXML
	private Label response;
	@FXML
	private Button createPatientButton;
	@FXML
	private Button cancelButton;

	@FXML
	private TextField patientIdField;
	@FXML
	private TextArea patientRecordArea;

	@FXML
	private TextField patientHeightField;
	@FXML
	private TextField patientWeightField;
	@FXML
	private Label updateRecordResponse;
	@FXML
	private Button searchButton;
	@FXML
	private Button cancelButton2;

	@FXML
	private VBox updateFormContainer;

	private Doctor doctor;

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
		updateFormContainer.setVisible(false);

		// Load doctor-specific information into the dashboard
	}

	@FXML
	public void createNewPatient() {
		response.setText("");
		String email = newPatientEmailField.getText();
		LocalDate dob = dobDatePicker.getValue();
		if (email.isEmpty() || dob == null) {
			response.setText("Please fill out all fields.");
		}

		String serverResponse = doctor.authorizeAccountCreation(email, dob);
		if (serverResponse.equals(null)) {
			response.setText("Error");
			return;
		}
		
		JSONObject json = new JSONObject(serverResponse);
		if (json.getString("result").equals("SUCCESS")) {
			response.setText("Account created!");
		} else {
			response.setText(json.getString("reason"));
		}
	}

	@FXML
	public void handleViewPatientRecord() {
		updateFormContainer.setVisible(false);
		patientRecordArea.setText("");
		
		String patientID = patientIdField.getText();

		// Server response
		String patientRecord = doctor.viewPatientRecord(patientID);
		
		if(patientRecord.equals(null)) {
			patientRecordArea.setText("Could not retrive data");
		}
		JSONObject json = new JSONObject(patientRecord);
		
		if (json.getString("result").equals("FAILURE")) {
			patientRecordArea.setText(json.getString("reason"));
			updateFormContainer.setVisible(false);
			patientIdField.setText(null);

		} else {
			patientRecordArea.setText(parseMedicalRecord(json.getJSONArray("records")));

			// Show the update form only if a patient record is successfully retrieved
			updateFormContainer.setVisible(!patientRecord.isEmpty());
			updateFormContainer.setManaged(!patientRecord.isEmpty());
		}

	}

	@FXML
	public void handleUpdatePatientRecord() {
		String patientID = patientIdField.getText();
		
		if (patientHeightField.getText().isEmpty() || patientWeightField.getText().isEmpty()) {
			updateRecordResponse.setText("Nothing to update");
			return;
		}
		
		float height = 0;
		float weight = 0;
		try {
			height = Float.parseFloat(patientHeightField.getText());   
			weight = Float.parseFloat(patientWeightField.getText());   
		} catch (NumberFormatException e) {
			updateRecordResponse.setText("Input only numbers!");
			return;
		} catch (NullPointerException e) {
			updateRecordResponse.setText("Input only numbers!");
			return;
		}
		
		if(weight < 1.5 || height < 30|| height > 250 || weight > 600) {
			updateRecordResponse.setText("Numbers out of range!");
			return;
		}
		

		String response = doctor.updatePatientRecordOnDB(patientID, height, weight);
		if (response.equals(null)) {
			updateRecordResponse.setText("Error");
			return;
		} 
		JSONObject json = new JSONObject(response);
		if (json.getString("result").equals("FAILURE")) {
			patientRecordArea.setText(json.getString("reason"));
		} else if (json.getString("result").equals("SUCCESS")) {
			patientRecordArea.setText("Updated to " + json.getString("details"));
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

	@FXML
	public void handleCancel() {
		newPatientEmailField.setText("");
		response.setText("");
		dobDatePicker.setValue(null);
	}

	@FXML
	public void handleCancel2() {
		patientHeightField.setText("");
		patientWeightField.setText("");
	}

}
