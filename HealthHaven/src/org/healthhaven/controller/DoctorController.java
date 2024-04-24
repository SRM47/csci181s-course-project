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
		String email = newPatientEmailField.getText();
		LocalDate dob = dobDatePicker.getValue();
		if (email.isEmpty() || dob == null) {
			response.setText("Please fill out all fields.");
		}

		String serverResponse = doctor.authorizeAccountCreation(email, dob);

		response.setText(serverResponse);
	}

	@FXML
	public void handleViewPatientRecord() {
		updateFormContainer.setVisible(false);
		patientRecordArea.setText(null);
		
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
		
		if(weight < 0 || height < 0) {
			updateRecordResponse.setText("No negative numbers!");
			return;
		}
		

		String response = doctor.updatePatientRecordOnDB(patientID, height, weight);

		patientRecordArea.setText(response);

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
	}

	@FXML
	public void handleCancel2() {
		patientHeightField.setText("");
		patientWeightField.setText("");
	}

}
