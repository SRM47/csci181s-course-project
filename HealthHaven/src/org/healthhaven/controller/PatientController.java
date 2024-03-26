package org.healthhaven.controller;

import org.healthhaven.model.*;

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
        recordTextArea.setText(medicalRecord);
    }
}
