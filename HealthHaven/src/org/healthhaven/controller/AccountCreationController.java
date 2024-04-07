package org.healthhaven.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.healthhaven.gui.Main;
import org.healthhaven.model.AccountCreationService;
import org.healthhaven.model.PasswordGenerator;
import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.scoring.Result;

public class AccountCreationController {
	
	@FXML
	private Button submitButton;
	@FXML
	private Button loginPageButton;
	@FXML
	private Label userTypeLabel;
	@FXML
	private Label emailDisplay;
	@FXML
	private PasswordField passwordTextfield;
	@FXML
	private TextField legalFirstNameTextfield;
	@FXML
	private TextField legalLastNameTextfield;
	@FXML
	private TextField addressTextfield;
	@FXML
	private DatePicker dobDatePicker;
	@FXML
	private ProgressBar passwordStrengthBar;
	@FXML
	private Label response;
	
	private String userType;
	private String userEmail;
	
	private Nbvcxz nbvcxz = new Nbvcxz();
	
	public void setAccountCreation(String email, String userType) {
		this.userEmail = email;
		this.userType = userType;
		
		emailDisplay.setText(email);
		userTypeLabel.setText(userType);
	}
	
	@FXML
	public void realTimePWSec() {

		Result result = nbvcxz.estimate(passwordTextfield.getText());
		
		System.out.println(passwordTextfield.getText());
		
		Float prog = ((float) result.getBasicScore())/4;
		
		passwordStrengthBar.setProgress(prog);
	}
	

	@FXML
	public void handleSubmit() {
		String rat = userType;
		String remail = userEmail;
		String rpw = passwordTextfield.getText();
		String rfn = legalFirstNameTextfield.getText();
		String rln = legalLastNameTextfield.getText();
		String raddress = addressTextfield.getText();
		LocalDate rdob = dobDatePicker.getValue();
		
		// Input validation 
	    if (rpw.isEmpty() || rfn.isEmpty() || rln.isEmpty() || raddress.isEmpty() || rdob == null) {
	        response.setText("Please fill in all fields.");
	        return; // Exit the method if any field is empty
	    }
	    
	    if (passwordChecker(rpw, remail, rfn,rln, rdob).equals("success")) {
	    	String serverResponse = AccountCreationService.createUser(rat, remail, rpw, rfn, rln, raddress, rdob);
			if (serverResponse.equals(null)) {
				response.setText("Error");
			} else {
				JSONObject jsonObj = new JSONObject(serverResponse);
				if (jsonObj.getString("result").equals("FAILURE")) {
					response.setText(jsonObj.getString("reason"));
				} else if (jsonObj.getString("result").equals("SUCCESS")){
					response.setText("Account created");
				}
			}
				
	    }
	    
	}
	
	private String passwordChecker(String password, String email, String first_name, String last_name, LocalDate dob) {
		Result result = nbvcxz.estimate(password);
		
		Integer passCheck = PasswordGenerator.passwordStrength(password, first_name, last_name, dob);
		
		//check for PII in password
		if(passCheck != 1) {
			if(passCheck == 2){
				response.setText("Please remove references to your name from your password");
			}
			else if(passCheck ==3) {
				response.setText("Please remove references to your birthdate from your password");
			}
			return "failure";
		}
		
		// Require minscore of 2
	    if (result.getBasicScore() < 2) {
	    	response.setText("Please strengthen your password: " + result.getFeedback().getWarning());
	        return "failure"; // Exit the method if any field is empty
	    }
	    
	    try {
			if (PasswordGenerator.compromiseChecker(password)==1) {
				response.setText("Password has been compromised. Please enter a new password");
				return "failure";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return "success";
	}
	
	
	
	@FXML
	public void loginPage(ActionEvent actionEvent) throws IOException {
        loadPage("../gui/login.fxml");
    }

    private void loadPage(String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
	

}
