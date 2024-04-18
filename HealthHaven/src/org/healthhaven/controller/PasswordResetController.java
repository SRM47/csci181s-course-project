package org.healthhaven.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.healthhaven.gui.Main;
import org.healthhaven.model.PasswordGenerator;
import org.healthhaven.model.PasswordReset;
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
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.scoring.Result;

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
	
	private JSONObject json;
	private String email;
	
	private Nbvcxz nbvcxz = new Nbvcxz();
	
	
	@FXML
	public void initialize() {
		OTPSection.setVisible(false);
		passwordResetSection.setVisible(false);
	}
	
	@FXML
	public void handleSubmit() {
		this.json = null;
		OTPSection.setVisible(false);
		passwordResetSection.setVisible(false);
		
		String email = emailTextfield.getText();
		if (email.isEmpty()) {
			submitResponse.setText("Please fill in the field");
		} else {
			submitResponse.setText("");
			String serverResponse = PasswordReset.verifyEmail(email);
			JSONObject jsonObj = new JSONObject(serverResponse);
			if (jsonObj.getString("result").equals("FAILURE")||serverResponse.equals(null)) {
				submitResponse.setText(jsonObj.getString("reason"));
			} else {
				if (jsonObj.getString("result").equals("SUCCESS")) {
					this.email = email;
					OTPSection.setVisible(true);
					OTPResponse.setText("OTP sent to your email");
					emailTextfield.setText("");
				}
			}
		}
	}
	
	@FXML 
	public void handleOTPVerify() {
		passwordResetSection.setVisible(false);
		
		String otpInput = OTPPasscodeField.getText();
		
		String serverResponse = PasswordReset.confirmOTP(email, otpInput);
		
		if (serverResponse.equals(null)) {
			OTPResponse.setText("Error. Try again");
		} else {
			JSONObject jsonObj = new JSONObject(serverResponse);
			if (jsonObj.getString("result").equals("FAILURE")) {
				OTPResponse.setText(jsonObj.getString("reason"));
			}
			else if (jsonObj.getString("result").equals("SUCCESS")) {
					this.json = jsonObj;
					OTPSection.setVisible(false);
					passwordResetSection.setVisible(true);
					OTPPasscodeField.setText("");
			}
				
		}
	}
	
	@FXML
	public void realTimePWSec() {

		Result result = nbvcxz.estimate(PasswordField1.getText());
		
		//System.out.println(PasswordField1.getText());
		
		Float prog = ((float) result.getBasicScore())/4;
		
		passwordStrengthBar.setProgress(prog);
	}
	
	
	@FXML
	public void handlePasswordReset() {
		OTPSection.setVisible(false);
		String rpw1 = PasswordField1.getText();
		String rpw2 = PasswordField2.getText();
		
		if (rpw1.isEmpty()||rpw2.isEmpty()) {
			PasswordResetMessage.setText("Please fill in all fields");
			return;
		} 
		if (!rpw1.equals(rpw2)) {
			PasswordResetMessage.setText("Password does not match.");
			return;
		}
		
		if (passwordChecker(json, rpw1).equals("success")) {
			 String serverResponse = PasswordReset.updatePassword(json.getString("email"), rpw1);
			    if (serverResponse.equals(null)) {
			    	PasswordResetMessage.setText("Error.");
			    } else {
			    	JSONObject jsonObj = new JSONObject(serverResponse);
			    	if (jsonObj.getString("result").equals("FAILURE")) {
			    		PasswordResetMessage.setText(jsonObj.getString("reason"));
			    	} else if (jsonObj.getString("result").equals("SUCCESS")) {
			    		PasswordResetMessage.setText("Password got updated");
			    		PasswordField1.setText("");
			    		PasswordField2.setText("");
			    	}
			    }
		}		    	
		
	}
	
	private String passwordChecker(JSONObject json, String rpw1) {
		Result result = nbvcxz.estimate(rpw1);
		String remail = json.getString("email");
		String rfn = json.getString("first_name");
		String rln = json.getString("last_name");
		String rdob = json.getString("dob");
		LocalDate dob = LocalDate.parse(rdob);
		
		Integer passCheck = PasswordGenerator.passwordStrength(rpw1, rfn, rln, dob);
		
		//check for PII in password
		if(passCheck != 1) {
			if(passCheck == 2){
				PasswordResetMessage.setText("Please remove references to your name from your password");
			}
			else if(passCheck ==3) {
				PasswordResetMessage.setText("Please remove references to your birthdate from your password");
			}
			return "failure";
		}
		
		// Require minscore of 2
	    if (result.getBasicScore() < 2) {
	    	PasswordResetMessage.setText("Please strengthen your password: " + result.getFeedback().getWarning());
	        return "failure"; // Exit the method if any field is empty
	    }
	    
	    try {
			if (PasswordGenerator.compromiseChecker(rpw1)==1) {
				PasswordResetMessage.setText("Password has been compromised. Please enter a new password");
				return "failure";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return "success";
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