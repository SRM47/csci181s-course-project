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
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.scoring.Result;

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
	@FXML
	private Login loginInstance;
	
	@FXML
	public void initialize() {
		loginInstance = new Login();
		
	}
	
	public void handleSubmit() throws IOException {
		String email = emailTextfield.getText();
		String password = passwordTextfield.getText();
		
		User user = loginInstance.identifyExistingUser(email, password);
		
		if (user.equals(null)) {
			//Next, check if this is a new user.
			String serverResponse = loginInstance.identifyNewUser(email, password);
			if (serverResponse.equals("FAILURE")) {
				errorMessage.setText("Login Error");
			} else {
				errorMessage.setText("");
				loadAccountCreationPage(email,serverResponse);
			}
		} else {
			errorMessage.setText("");
			loadLoginPage(user);	
			
		}
	}
	

	private void loadAccountCreationPage(String email, String serverResponse) throws IOException {
		 FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/accountcreation.fxml"));
	        Parent root = loader.load();
	        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
	        
	        AccountCreationController accountController = loader.getController();
	        accountController.setAccountCreation(email, serverResponse);
	        
	        stage.setScene(new Scene(root));
	        stage.show();
    }
	
	private void loadLoginPage(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/user.fxml"));
        Parent root = loader.load();
        
    	UserController userController = loader.getController();
        userController.setCurrentUser(user);   		
        
        
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
	
	
	
	
	
}
