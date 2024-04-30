package org.healthhaven.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.healthhaven.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class DoctorController {

	@FXML
	private TextField newPatientEmailField;
	@FXML
	private DatePicker dobDatePicker;
	@FXML
	private Label response1;
	@FXML
	private Button createPatientButton;
	@FXML
	private Button cancelButton;

	@FXML
	private TextField patientIdField;
	@FXML
	private Label response2;
	
    @FXML
    private TableView<MedicalDataInstance> dataTable;
    
    @FXML
    private TableColumn<MedicalDataInstance, Float> heightColumn;
    
    @FXML
    private TableColumn<MedicalDataInstance, Float> weightColumn;
    
    @FXML
    private TableColumn<MedicalDataInstance, Date> timestampColumn;

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
		response1.setText("");
		String email = newPatientEmailField.getText();
		LocalDate dob = dobDatePicker.getValue();
		if (email.isEmpty() || dob == null) {
			response1.setText("Please fill out all fields.");
		}

		String serverResponse = doctor.authorizeAccountCreation(email, dob);
		if (serverResponse.equals(null)) {
			response1.setText("Error");
			return;
		}
		
		JSONObject json = new JSONObject(serverResponse);
		if (json.getString("result").equals("SUCCESS")) {
			response1.setText("Account created!");
		} else {
			response1.setText(json.getString("reason"));
		}
	}

	@FXML
	public void handleViewPatientRecord() {
		updateFormContainer.setVisible(false);
		dataTable.getItems().setAll(parseMedicalInformationResultJSON(null));
		
		String patientID = patientIdField.getText();

		// Server response
		showPatientRecord(patientID);

	}
	
	private void showPatientRecord(String patientID) {

		// Server response
		String patientRecord = doctor.viewPatientRecord(patientID);
		
		//Displaying the result
		if(patientRecord.equals(null)) {
			response2.setText("Could not retrive data");
			return;
		} 
		
		System.out.print(patientRecord);
		
		JSONObject json = new JSONObject(patientRecord);
		if (json.getString("result").equals("FAILURE")) {
			response2.setText(json.getString("reason"));
			updateFormContainer.setVisible(false);
			patientIdField.setText(null);

		} else if (json.getString("result").equals("SUCCESS")) {
			heightColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, Float>("height"));
	    	weightColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, Float>("weight"));
	    	timestampColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, Date>("timestamp"));
	        
	        dataTable.getItems().setAll(parseMedicalInformationResultJSON(json));
	        updateFormContainer.setVisible(!patientRecord.isEmpty());
			updateFormContainer.setManaged(!patientRecord.isEmpty());
			
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
            MedicalDataInstance dataInstance = new MedicalDataInstance(height, weight, date);
            System.out.println(dataInstance);

            // Add to the list
            dataList.add(dataInstance);
        }

        return dataList;
    	
    }
	
	private Date convertDateStringToDate(String dateString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(dateString);
    } 
	

	@FXML
	public void handleUpdatePatientRecord() {
		response2.setText("");
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
			response2.setText(json.getString("reason"));
		} else if (json.getString("result").equals("SUCCESS")) {
			response2.setText("Success!");
			showPatientRecord(patientID);
		}
		

	}

	@FXML
	public void handleCancel() {
		newPatientEmailField.setText("");
		response1.setText("");
		dobDatePicker.setValue(null);
	}

	@FXML
	public void handleCancel2() {
		patientHeightField.setText("");
		patientWeightField.setText("");
	}

}
