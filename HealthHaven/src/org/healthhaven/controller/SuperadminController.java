package org.healthhaven.controller;

import java.time.LocalDate;

import org.healthhaven.model.*;
import org.healthhaven.model.User.Account;
import org.json.JSONObject;

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
	private TextField emailTextfield;
	@FXML
	private MenuButton accountTypeMenu;
	@FXML
	private MenuItem patientMenuItem;
	@FXML
	private MenuItem doctorMenuItem;
	@FXML
	private MenuItem dataAnalystMenuItem;
	@FXML
	private DatePicker datepicker;
	@FXML
	private TextField UserIDField;
	@FXML
	private Button searchButton;
	@FXML
	private Label deactivationResponse;
	@FXML
	private Button deactivateButton;
	@FXML
	private TextArea recordTextArea;
	
	@FXML
	private Button viewAccountButton;
	
	private String userID;

	public void setSuperadmin(Superadmin superadmin) {
		this.superadmin = superadmin;
		// Load doctor-specific information into the dashboard
	}

//	@FXML
//	public void clickViewAccountList() {
//		listAccounts();
//	}

	@FXML
	public void updateAccountTypeDoctor() {
		accountTypeMenu.setText(doctorMenuItem.getText());
	}

	@FXML
	public void updateAccountTypePatient() {
		accountTypeMenu.setText(patientMenuItem.getText());
	}

	@FXML
	public void updateAccountTypeDataAnalyst() {
		accountTypeMenu.setText(dataAnalystMenuItem.getText());
	}

	@FXML
	public void authorizeAccount() {
		response.setText(null); // clear

		String rat = accountTypeMenu.getText();
		Account userAccountType = Account.NONE;
		switch (accountTypeMenu.getText()) {
		case "Patient":
			userAccountType = Account.PATIENT;
			break;
		case "Doctor":
			userAccountType = Account.DOCTOR;
			break;
		case "Data Analyst":
			userAccountType = Account.DATA_ANALYST;
			break;

		}
		String email = emailTextfield.getText();
		LocalDate dob = datepicker.getValue();

		// Input validation
		if (rat.isEmpty() || email.isEmpty() || dob == null) {
			response.setText("Please fill in all fields.");
		}
		String serverResponse = superadmin.authorizeAccountCreation(email, userAccountType, dob);
		if (serverResponse==null) {
			response.setText("Error");
			return;
		} 
		JSONObject json = new JSONObject(serverResponse);
		if (json.getString("result").equals("SUCCESS")) {
			response.setText("Account created");
		} else if (json.getString("result").equals("FAILTURE")){
			response.setText(json.getString("reason"));
		}

	}
	
//	//TODO: Incomplete
//	private void listAccounts() {
//		String accountRecords = superadmin.viewAccountList();
//		;
//		recordTextArea.setText(accountRecords);
//	}
	
	@FXML
	public void handleUserIdSearch() {
		this.userID = null;
		deactivationResponse.setText(null);
		String userID = UserIDField.getText();
		if (userID.equals("")) {
			deactivationResponse.setText("Please enter the UserID");
			return;
		} 
		String serverResponse = superadmin.searchByUserId(userID);
		if (serverResponse.equals(null)) {
			deactivationResponse.setText("Likely error in communicating with the server");
		} else {
			JSONObject json = new JSONObject(serverResponse);
			if (json.getString("result").equals("FAILURE")) {
				deactivationResponse.setText(json.getString("reason"));
			} else if (json.getString("result").equals("SUCCESS")){
				this.userID = userID;
				recordTextArea.setText(parseUserData(json));
				//deactivationResponse.setText("Successfully deactivated");
			}
		}
		
	}
	
	private String parseUserData(JSONObject json) {
		return "User ID: " + json.getString("userID") + "\n" +
	               "First name: " + json.getString("first_name") + "\n" +
	               "Last name: " + json.getString("last_name") + "\n" +
	               "Email: " + json.getString("email") + "\n" +
 	               "Account Type: " + json.getString("accountType");
	}
	
	@FXML
	public void handleDeactivate() {
		String serverResponse = superadmin.deactivateAccount(userID);
		if (serverResponse.equals(null)) {
			deactivationResponse.setText("Likely error in communicating with the server");
		} else {
			JSONObject json = new JSONObject(serverResponse);
			if (json.getString("result").equals("FAILURE")) {
				deactivationResponse.setText(json.getString("reason"));
			} else if (json.getString("result").equals("SUCCESS")){
				deactivationResponse.setText("Successfully deactivated");
				recordTextArea.setText("");
			}
		}
	}
	@FXML
	public void handleCancel() {
		emailTextfield.setText("");
		datepicker.setValue(null);
	}

}
