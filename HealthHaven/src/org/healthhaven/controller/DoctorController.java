package org.healthhaven.controller;

import java.time.LocalDate;

import org.healthhaven.model.*;

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
		String patientID = patientIdField.getText();

		// Server response
		String patientRecord = doctor.viewPatientRecord(patientID);

		if (patientRecord.equals("FAILURE")) {
			patientRecordArea.setText("Could not retrive data");
			updateFormContainer.setVisible(false);

		} else {
			patientRecordArea.setText(patientRecord);

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
