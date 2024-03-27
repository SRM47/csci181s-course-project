package org.healthhaven.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

import org.healthhaven.gui.Main;
import org.healthhaven.model.*;

public class LoginController{
	
	@FXML
	private TextField emailTextfield;
	@FXML
	private Button submitButton;
	@FXML
	private PasswordField passwordTextfield;
	@FXML
	private Button accountCreationButton;
	@FXML
	private Label errorMessage;
	
	private Login loginInstance;
	
	@FXML
	public void initialize() {
		loginInstance = new Login();
		
	}
	
	public void handleSubmit() throws IOException {
		String email = emailTextfield.getText();
		String password = passwordTextfield.getText();
		
		User user = loginInstance.identifyUser(email, password);
		
		if (user.equals(null)) {
			errorMessage.setText("Login Error");
		} else {
			errorMessage.setText("");
			loadPage("../gui/user.fxml", user);	
			
		}
	}
	

	public void loadAccountCreationPage(ActionEvent actionEvent) throws IOException {
        loadPage("../gui/accountcreation.fxml", null);
    }
	
	private void loadPage(String fxml, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        
        if (user !=null) {
        	UserController userController = loader.getController();
            userController.setCurrentUser(user);   		
        }
        
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
	
	
}
