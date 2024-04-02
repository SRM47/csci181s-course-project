package org.healthhaven.controller;

import java.io.IOException;

import org.healthhaven.gui.Main;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PasswordResetController{
	
	@FXML
	private TextField emailTextfield;
	@FXML
	private Button submitButton;
	@FXML
	private Label submitResponse;
	@FXML
	private HBox OTPSection;
	@FXML
	private TextField OTPPasscodeField;
	@FXML
	private Button submitOTPButton;
	@FXML
	private Label OTPResponse;
	@FXML
	private VBox passwordResetSection;
	@FXML
	private PasswordField PasswordField1;
	@FXML
	private ProgressBar passwordStrengthBar;
	@FXML
	private PasswordField PasswordField2;
	@FXML
	private Label PasswordResetMessage;
	@FXML
	private Button PasswordResetButton;
	@FXML
	private Hyperlink login;
	
	@FXML
	public void initialize() {
		OTPSection.setVisible(false);
		passwordResetSection.setVisible(false);
	}
	
	@FXML
	public void handleSubmit() {
		String email = emailTextfield.getText();
		if (email.isEmpty()) {
			submitResponse.setText("Please fill in the field");
		} else {
			submitResponse.setText("");
			//PasswordReset.verifyEmail(email);
		}
	}
	
	@FXML 
	public void handleOTPVerify() {
		
	}
	
	@FXML
	public void handlePasswordReset() {
		
	}
	
	@FXML
	public void handleLogin() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/login.fxml"));
        Parent root = loader.load();  		
        
        
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
	}
}