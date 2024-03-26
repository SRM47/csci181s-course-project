package org.healthhaven.controller;

import org.healthhaven.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class DataAnalystController {

    private DataAnalyst dataAnalyst;
    
    @FXML
    private TextArea recordTextArea;

    public void setDataAnalyst(DataAnalyst dataAnalyst) {
        this.dataAnalyst = dataAnalyst;
        showDataSummary();
        // Load doctor-specific information into the dashboard
    }

    // Doctor-specific methods here
    
    @FXML
    public void showDataSummary() {
    	// Assuming viewPatientRecord returns the medical record as a String
        String dataSummary = dataAnalyst.performDataAnalysis();
        recordTextArea.setText(dataSummary);
    }
}
