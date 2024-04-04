package org.healthhaven.controller;

import java.time.LocalDate;

import org.healthhaven.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextArea;

public class SuperadminController {

    private Superadmin superadmin;
    
    @FXML
    private StackPane mainView;
    @FXML 
    private Label response;
    @FXML
    private Button authorizeAccountButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button viewAccountButton;
    @FXML
    private TextArea recordTextArea;
    @FXML
    private TextField emailTextfield;
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
	private DatePicker datepicker;
    

    public void setSuperadmin(Superadmin superadmin) {
        this.superadmin = superadmin;
        // Load doctor-specific information into the dashboard
    }
    
    @FXML
    public void clickViewAccountList() {
    	listAccounts();
    }
    
    @FXML
    public void updateAccountTypeDoctor() {
		accountTypeMenu.setText(doctorMenuItem.getText());
	}
	
    @FXML
	public void updateAccountTypePatient() {
		accountTypeMenu.setText(patientMenuItem.getText());
	}
    
	@FXML
	public void updateAccountTypeDataProtectionOfficer() {
		accountTypeMenu.setText(dataProtectionOfficerMenuItem.getText());
	}
	
	@FXML
	public void updateAccountTypeDataAnalyst() {
		accountTypeMenu.setText(dataAnalystMenuItem.getText());
	}
	
	@FXML
	public void authorizeAccount() {
		response.setText(null); //clear
		
		String rat = accountTypeMenu.getText();
		String remail = emailTextfield.getText();
		LocalDate dob = datepicker.getValue();
		
		//Input validation
		if (rat.isEmpty()|| remail.isEmpty()||dob==null) {
			response.setText("Please fill in all fields.");
		}
		String serverResponse = superadmin.authorizeAccountCreation(remail, rat, dob);
		response.setText(serverResponse);
		
	}

    private void listAccounts() {
    	String accountRecords = superadmin.viewAccountList();;
        recordTextArea.setText(accountRecords);
    }
    
    @FXML
    public void handleCancel() {
    	emailTextfield.setText("");
    	datepicker.setValue(null);
    }
    
    
}
