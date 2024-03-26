package org.healthhaven.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class UserController {
	
	@FXML
    private BorderPane contentArea;
	
	@FXML
	private String mainPage;

    public void initialize() {
        // Example user type check
        String userType = getUserType(); // Implement this method based on your application's logic
        
        if ("doctor".equals(userType)) {
        	mainPage = "DoctorDashboard.fxml";
        } else if ("patient".equals(userType)) {
            mainPage = "PatientDashboard.fxml";
        } else if ("SA".equals(userType)) {
        	mainPage = "SuperAdminDashboard.fxml";
        } else if ("DSA".equals(userType)) {
        	mainPage = "DataAnalystDashboard.fxml";
        }
        
        loadContent(mainPage);
    }

    private void loadContent(String fxmlPath) {
        try {
            contentArea.setCenter(FXMLLoader.load(getClass().getResource(fxmlPath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Placeholder for your method to determine the user type
    private String getUserType() {
        // This method should return either "doctor" or "patient"
        return "doctor"; // Example return value
    }
    
    // Example method handlers for navigation buttons
    @FXML
    private void handleHome() {
    	loadContent(mainPage);
        // Implement navigation logic
    }
    
    @FXML
    private void handleProfile() {
    	loadContent("ProfileDashboard.fxml");
        // Implement navigation logic
    }
	
	

}
