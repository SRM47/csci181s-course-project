package org.healthhaven.controller;

import org.healthhaven.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    

    public void setSuperadmin(Superadmin superadmin) {
        this.superadmin = superadmin;
        // Load doctor-specific information into the dashboard
    }
    
    public void clickViewAccountList() {
    	listAccounts();
    }
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
	
	public void authorizeAccount() {
		response.setText(null); //clear
		
		String rat = accountTypeMenu.getText();
		String remail = emailTextfield.getText();
		
		//Input validation
		if (rat.isEmpty()|| remail.isEmpty()) {
			response.setText("Please fill in all fields.");
		}
		String serverResponse = superadmin.authorizeAccountCreation(remail, rat);
		response.setText(serverResponse);
		
	}

    private void listAccounts() {
    	String accountRecords = superadmin.viewAccountList();;
        recordTextArea.setText(accountRecords);
    }
    
    
}
