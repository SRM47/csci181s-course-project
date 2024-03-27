package org.healthhaven.controller;

import org.healthhaven.model.*;

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
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField addressField;
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
    private void handleSaveAction() {
        // Gather data from input fields
        String email = emailField.getText();
        String password = passwordField.getText();// get from input field;
        String address = addressField.getText();// get from input field;
        
        if (email.equals("")) {
        	email = currentUser.getEmail();
        } if (password.equals("")) {
        	password = currentUser.getPassword();
        } if (address.equals("")) {
        	address = currentUser.getAddress();
        }

        // Update User model
        currentUser.updatePersonalRecordOnDB(email, password, address);

        // Refresh displayed user info
        displayUserInfo();
    }
    
    @FXML
    private void handleCancelAction() {
        // Clear all text fields
        emailField.setText("");
        passwordField.setText("");
        addressField.setText("");
    }
    
    
}
