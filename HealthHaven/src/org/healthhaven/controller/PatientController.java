package org.healthhaven.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.healthhaven.model.*;
import org.json.JSONObject;
import org.json.JSONArray;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class PatientController {
	@FXML
	private Label response;
	@FXML
    private TableView<MedicalDataInstance> dataTable;
    
    @FXML
    private TableColumn<MedicalDataInstance, Float> heightColumn;
    
    @FXML
    private TableColumn<MedicalDataInstance, Float> weightColumn;
    
    @FXML
    private TableColumn<MedicalDataInstance, Date> timestampColumn;

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
        	response.setText("Error");
        }
        JSONObject json = new JSONObject(medicalRecord);
        if (json.getString("result").equals("SUCCESS")) {
        	heightColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, Float>("height"));
	    	weightColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, Float>("weight"));
	    	timestampColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, Date>("timestamp"));
	        
	        dataTable.getItems().setAll(parseMedicalInformationResultJSON(json));
			
        }   else if (json.getString("result").equals("FAILURE")) {
        	response.setText(json.getString("reason"));
        }
    }
    
    private ObservableList<MedicalDataInstance> parseMedicalInformationResultJSON(JSONObject json) {
    	ObservableList<MedicalDataInstance> dataList = FXCollections.observableArrayList(); // Recommended initialization
    	if (json==null) {
    		return dataList;
    	}
        JSONArray entriesArray = json.optJSONArray("records"); // Get the 'entries' array
        if (entriesArray == null) {
        	return dataList;
        }

        for (int i = 0; i < entriesArray.length(); i++) { // Using length() for the loop
            JSONObject entry = entriesArray.getJSONObject(i);

            float height = (float) entry.getDouble("Height");
            float weight = (float) entry.getDouble("Weight");
            String dateString = entry.getString("Timestamp");

            // Convert dateString to a Date object (assuming you have a suitable method)
            Date date = null;
			try {
				date = convertDateStringToDate(dateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

            // Create MedicalDataInstance
            MedicalDataInstance dataInstance = new MedicalDataInstance(height, weight, date, null);
            System.out.println(dataInstance);

            // Add to the list
            dataList.add(dataInstance);
        }

        return dataList;
    	
    }
    
    private Date convertDateStringToDate(String dateString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));  // Set the timezone to UTC as indicated by 'Z'
        return formatter.parse(dateString);
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
