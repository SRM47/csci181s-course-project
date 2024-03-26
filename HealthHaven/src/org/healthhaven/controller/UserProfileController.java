package org.healthhaven.controller;

import org.healthhaven.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserProfileController {
	@FXML
    private Label userInfoLabel;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField addressField;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        // Update the view with user information
    }
    
    private void displayUserInfo() {
        // Assuming User has a toString() or a similar method to get a formatted string of user info
        userInfoLabel.setText(currentUser.toString());
    }

    // Methods to handle profile updates
    @FXML
    private void handleSaveAction() {
        // Gather data from input fields
        String email = emailField.getText();
        String password = passwordField.getText();// get from input field;
        String address = addressField.getText();// get from input field;

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
