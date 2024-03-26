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

import javafx.event.ActionEvent;

public class LoginController{
	
	@FXML
	private TextField emailTextfield;
	@FXML
	private Button submitButton;
	@FXML
	private PasswordField passwordTextfield;
	@FXML
	private Button accountCreationButton;
	
	
	
	public void handleSubmit() {
		String email = emailTextfield.getText();
		
		String password = passwordTextfield.getText();
		
		System.out.println(email + password);
	}
	

	public void loadAccountCreationPage(ActionEvent actionEvent) throws IOException {
        loadPage("../gui/accountcreation.fxml");
    }
	
	private void loadPage(String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
	
}
