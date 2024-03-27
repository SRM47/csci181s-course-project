package org.healthhaven.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.healthhaven.gui.Main;
import org.healthhaven.model.AccountCreationService;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountCreationController {
	
	@FXML
	private Button submitButton;
	@FXML
	private Button loginPageButton;
	@FXML
	private MenuButton accountTypeMenu;
	@FXML
	private MenuItem patientMenuItem;
	@FXML
	private MenuItem doctorMenuItem;
	@FXML
	private MenuItem dataProtectionOfficerMenuItem;
	@FXML
	private MenuItem dataAnalystMenuItem;
	@FXML
	private TextField emailTextfield;
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
	private Label response;
	
	public void updateAccountTypeDoctor() {
		accountTypeMenu.setText(doctorMenuItem.getText());
	}
	
	public void updateAccountTypePatient() {
		accountTypeMenu.setText(patientMenuItem.getText());
	}
	
	public void updateAccountTypeDataProtectionOfficer() {
		accountTypeMenu.setText(dataProtectionOfficerMenuItem.getText());
	}
	
	public void updateAccountTypeDataAnalyst() {
		accountTypeMenu.setText(dataAnalystMenuItem.getText());
	}
	
	public void handleSubmit() {
		String rat = accountTypeMenu.getText();
		String remail = emailTextfield.getText();
		String rpw = passwordTextfield.getText();
		String rfn = legalFirstNameTextfield.getText();
		String rln = legalLastNameTextfield.getText();
		String raddress = addressTextfield.getText();
		LocalDate rdob = dobDatePicker.getValue();
		
		// Input validation
	    if (rat.isEmpty() || remail.isEmpty() || rpw.isEmpty() || rfn.isEmpty() || rln.isEmpty() || raddress.isEmpty() || dobDatePicker.getValue() == null) {
	        response.setText("Please fill in all fields.");
	        return; // Exit the method if any field is empty
	    }
	    
		try {
			String serverResponse = AccountCreationService.createUser(rat, remail, rpw, rfn, rln, raddress, rdob);
			response.setText(serverResponse);
		
		} catch (DateTimeParseException e) {
			response.setText("Invalid date format. Please enter the date in yyyy-mm-dd format.");
		}	
		
	}
	
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
