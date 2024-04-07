package org.healthhaven.controller;

import org.healthhaven.model.*;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class UserProfileController {
	@FXML
    private TextArea userInfoArea;
    @FXML
    private TextField addressField;
    @FXML
    private Label response;
    @FXML 
    private Button updateButton;

    private User currentUser;
    
    @FXML
    public void initialize() {
    }

    public void setUser(User user) {
        this.currentUser = user;
        displayUserInfo();
    }
    
    private void displayUserInfo() {
        // Assuming User has a toString() or a similar method to get a formatted string of user info
    	userInfoArea.setText(currentUser.toString());
    }

    // Methods to handle profile updates
    @FXML
    public void handleSaveAction() {
        // Gather data from input fields
        String address = addressField.getText();// get from input field;
        
   
        if (address.equals("")||address.equals(currentUser.getAddress())) {
        	response.setText("Nothing to update.");
        }

        // Update User model
        String serverResponse = currentUser.updatePersonalRecordOnDB(address);
        JSONObject jsonObj = new JSONObject(serverResponse);
        if (jsonObj.get("result").equals("SUCCESS")) {
        	currentUser.setAddress(address);
        }
        // Refresh displayed user info
        displayUserInfo();
    }
    
    @FXML
    public void handleCancelAction() {
        // Clear all text fields
        addressField.setText("");
    }
    
    
}
