package org.healthhaven.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.healthhaven.gui.Main;
import org.healthhaven.model.AccountCreationService;
import org.healthhaven.model.PasswordGenerator;

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
	private MenuButton userTypeMenu;
	@FXML
	private Label emailDisplay;
//	@FXML
//	private MenuButton accountTypeMenu;
//	@FXML
//	private MenuItem patientMenuItem;
//	@FXML
//	private MenuItem doctorMenuItem;
//	@FXML
//	private MenuItem dataProtectionOfficerMenuItem;
//	@FXML
//	private MenuItem dataAnalystMenuItem;
//	@FXML
//	private TextField emailTextfield;
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
		userTypeMenu.setText(userType);
	}
	
	
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
		
		Result result = nbvcxz.estimate(rpw);

		Integer passCheck = PasswordGenerator.passwordStrength(rpw, rfn, rln, rdob);
	

		//check for PII in password
		if(passCheck != 1) {
			if(passCheck == 2){
				response.setText("Please remove references to your name from your password");
			}
			else if(passCheck ==3) {
				response.setText("Please remove references to your birthdate from your password");
			}
			return;
		}
		
		// Require minscore of 2
	    if (result.getBasicScore() < 2) {
	        response.setText("Please strengthen your password: " + result.getFeedback().getWarning());
	        return; // Exit the method if any field is empty
	    }
	    
	    try {
			if (PasswordGenerator.compromiseChecker(rpw)==1) {
				response.setText("Password has been compromised. Please enter a new password");
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    	
	    
	    
	    String serverResponse = AccountCreationService.createUser(rat, remail, rpw, rfn, rln, raddress, rdob);
		response.setText(serverResponse);
		

		
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
