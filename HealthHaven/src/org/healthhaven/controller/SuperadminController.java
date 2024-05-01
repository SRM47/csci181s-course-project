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
	public void updateAccountTypeDataAnalyst() {
		accountTypeMenu.setText(dataAnalystMenuItem.getText());
	}

	@FXML
	public void authorizeAccount() {
		response.setText(null); // clear

		String rat = accountTypeMenu.getText();
		Account userAccountType = Account.NONE;
		switch (accountTypeMenu.getText()) {
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
			return;
		}
		String serverResponse = superadmin.authorizeAccountCreation(email, userAccountType, dob);
		if (serverResponse==null) {
			response.setText("Error");
			return;
		} 
		JSONObject json = new JSONObject(serverResponse);
		if (json.getString("result").equals("SUCCESS")) {
			response.setText("Account created");
		} else if (json.getString("result").equals("FAILURE")){
			response.setText(json.getString("reason"));
		}

	}
	
	
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
				recordTextArea.setText("");
			} else if (json.getString("result").equals("SUCCESS")){
				this.userID = userID;
				recordTextArea.setText(parseUserData(json));
			}
		}
		
	}
	
	private String parseUserData(JSONObject json) {
	    return "User ID: " + json.optString("userID", "Not available") + "\n" +
	           "First name: " + json.optString("first_name", "Not available") + "\n" +
	           "Last name: " + json.optString("last_name", "Not available") + "\n" +
	           "Email: " + json.optString("email", "Not available") + "\n" +
	           "Account Type: " + json.optString("accountType", "Not available");
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
				UserIDField.setText("");
			}
		}
	}
	@FXML
	public void handleCancel() {
		emailTextfield.setText("");
		response.setText("");
		datepicker.setValue(null);
	}

}
