package org.healthhaven.controller;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import org.healthhaven.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DataAnalystController{

    private DataAnalyst dataAnalyst;
    
    @FXML
    private CheckBox beforeCheckBox;
    
    @FXML
    private CheckBox afterCheckBox;
    
    @FXML
	private DatePicker datePicker;
    
    @FXML
    private Button requestDataButton;
    
    @FXML
    private TableView<MedicalDataInstance> dataTable;
    
    @FXML
    private TableColumn<MedicalDataInstance, Float> heightColumn;
    
    @FXML
    private TableColumn<MedicalDataInstance, Float> weightColumn;
    
    @FXML
    private TableColumn<MedicalDataInstance, Date> timestampColumn;
    
    @FXML
    private TableColumn<MedicalDataInstance, String> identifierColumn;
    

    public void setDataAnalyst(DataAnalyst dataAnalyst) {
        this.dataAnalyst = dataAnalyst;
    }
    
    @FXML
    public void requestData() {
    	boolean after = afterCheckBox.isSelected();
    	boolean before = beforeCheckBox.isSelected();
    	if (!(after ^ before)) {
    		return;
    	}
    	LocalDate date = datePicker.getValue();
    	
        JSONObject result_data = dataAnalyst.performDataAnalysis(after, before, date);
        System.out.println(result_data);
        
        heightColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, Float>("height"));
    	weightColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, Float>("weight"));
    	timestampColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, Date>("timestamp"));
    	identifierColumn.setCellValueFactory(new PropertyValueFactory<MedicalDataInstance, String>("identifier"));
        
        dataTable.getItems().setAll(parseMedicalInformationResultJSON(result_data));
    }
    
    private ObservableList<MedicalDataInstance> parseMedicalInformationResultJSON(JSONObject result) {
    	ObservableList<MedicalDataInstance> dataList = FXCollections.observableArrayList(); // Recommended initialization
        
        JSONArray entriesArray = result.optJSONArray("entries"); // Get the 'entries' array
        if (entriesArray == null) {
        	return dataList;
        }

        for (int i = 0; i < entriesArray.length(); i++) { // Using length() for the loop
            JSONObject entry = entriesArray.getJSONObject(i);

            float height = (float) entry.getDouble("height");
            float weight = (float) entry.getDouble("weight");
            String dateString = entry.getString("entryDate");
            String identifier = entry.getString("identifier");

            // Convert dateString to a Date object (assuming you have a suitable method)
            Date date = null;
			try {
				date = convertDateStringToDate(dateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

            // Create MedicalDataInstance
            MedicalDataInstance dataInstance = new MedicalDataInstance(height, weight, date, identifier);
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

}
