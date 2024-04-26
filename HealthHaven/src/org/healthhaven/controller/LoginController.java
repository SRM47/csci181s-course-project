package org.healthhaven.controller;

import org.json.JSONObject;
import org.json.JSONArray;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.scoring.Result;

import java.io.IOException;
import java.time.Instant;

import org.healthhaven.gui.Main;
import org.healthhaven.model.*;

public class LoginController{
	
	@FXML
	private TextField emailTextfield;
	@FXML
	private PasswordField passwordTextfield;
	@FXML
	private Label errorMessage;
	@FXML
	private HBox OTPSectionLogin;
	@FXML
	private TextField OTPLoginTextField;
	@FXML
	private Button OTPVerifyLoginButton;
	@FXML
	private Button submitButton;
	@FXML
	private Hyperlink passwordReset;
	
	private String emailAddress;
	
	@FXML
	public void initialize() {
		OTPSectionLogin.setVisible(false);
	}
	
	@FXML
	public void handleSubmit() throws IOException {
		//reset
		OTPSectionLogin.setVisible(false);
		
		//get input
		String email = emailTextfield.getText();
		String password = passwordTextfield.getText();
		this.emailAddress = email;

		String serverResponse= Login.authenticateUserOnDB(email, password);
		if (serverResponse == null) {
			errorMessage.setText("Login Error");
			
		} else { //either new or existing user
			JSONObject jsonObj = new JSONObject(serverResponse);
			// Access the value associated with the key "request"
	        
	        if (jsonObj.getString("result").equals("FAILURE")) {
	        	errorMessage.setText(jsonObj.optString("reason"));
	        } else if (jsonObj.getString("result").equals("SUCCESS")) {
	        	//existing user
		        if (jsonObj.getString("type").equals("EXISTING")) {
		        	errorMessage.setText("");
		        	//Do OTP verification
		        	OTPSectionLogin.setVisible(true);
		        	errorMessage.setText("OTP sent to your email");
		        	
		        //new user, direct them to account creation
		        } else if (jsonObj.getString("type").equals("NEW")) {
		        	errorMessage.setText("");
		        	// loadAccountCreationPage(email,jsonObj.getString("accountType"));
		        	loadConsentPage(email, jsonObj.getString("userType"));
		        }
	        }
	        
		}

	
	}
	
	@FXML
	public void handleVerifyOTPLogin() throws IOException{
		errorMessage.setText("");
		String otpInput = OTPLoginTextField.getText();
		
		String serverResponse = Login.authenticateOTPLogin(emailAddress, otpInput);
		if (serverResponse.equals(null)) {
			errorMessage.setText("Login Error");
		} else {
			//Read server response
			JSONObject jsonObj = new JSONObject(serverResponse);
			if (jsonObj.getString("result").equals("FAILURE")) {
				errorMessage.setText(jsonObj.getString("reason"));
			} else if (jsonObj.getString("result").equals("SUCCESS")){
				System.out.println(jsonObj.toString());
				User user = Login.existingUserSession(jsonObj);
		    	loadUserPage(user);
		    	errorMessage.setText("");
			}
			
		}
	}
	
	@FXML
	public void handlePasswordReset() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/PasswordReset.fxml"));
        Parent root = loader.load();  		
        
        
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
		
		
	}
	

	private void loadConsentPage(String email, String userType) throws IOException {
		 FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/DataSharingPolicy.fxml"));
	        Parent root = loader.load();
	        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
	        
	        DataSharingPolicyController dataSharingPolicyController = loader.getController();
	        dataSharingPolicyController.intialize(email, userType);
	        
	        stage.setScene(new Scene(root));
	        stage.show();
    }
	
	private void loadUserPage(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/user.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        
    	UserController userController = loader.getController();
        userController.setCurrentUser(user);   		
        
        stage.setScene(new Scene(root));
        stage.show();
    }
	
	
	
	
	
}
