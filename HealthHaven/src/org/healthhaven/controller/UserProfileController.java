package org.healthhaven.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.healthhaven.model.*;
import org.healthhaven.model.User.Account;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.scoring.Result;

public class UserProfileController {
	@FXML
	private StackPane UserProfileSection;
	@FXML
    private TextArea userInfoArea;
	@FXML
	private HBox updateOptionField;
	@FXML
	private MenuButton updateSelectionMenu;
	@FXML
	private MenuItem upddateAddressMenu;
	@FXML
	private MenuItem updatePasswordMenu;
	@FXML
	private Button confirmUpdateButton;
    @FXML
    private TextField addressField;
    @FXML
    private Label response;
    @FXML
    private VBox updatePasswordSection;
    @FXML
    private PasswordField PasswordField1;
    @FXML
    private ProgressBar passwordStrengthBar;
    @FXML
    private PasswordField PasswordField2;
    @FXML
    private Label PasswordResetMessage;
    @FXML
    private HBox updateActionField;
    @FXML 
    private Button updateButton;
    @FXML
    private Hyperlink dataSharingLink;
    @FXML
    private Hyperlink accountDeactivationLink;

    private User currentUser;
    private UserController userController;
    private Nbvcxz nbvcxz = new Nbvcxz();

    public void setUser(User user, UserController userController) {
        this.currentUser = user;
        this.userController = userController;
        addressField.setVisible(false);
        updatePasswordSection.setVisible(false);
        updateActionField.setVisible(false);
        response.setText(null);
        displayUserInfo();
        dataSharingLink.setVisible(false);
        
        if (currentUser.getAccountType() == Account.PATIENT) {
        	dataSharingLink.setVisible(true);
        }
    }
    
    private void displayUserInfo() {
        // Assuming User has a toString() or a similar method to get a formatted string of user info
    	userInfoArea.setText(parseUserData(currentUser));
    }
    
    private String parseUserData(User currentUser) {
		return "User ID: " + currentUser.getUserID() + "\n" +
	               "First name: " + currentUser.getLegal_first_name() + "\n" +
	               "Last name: " + currentUser.getLegal_last_name() + "\n" +
	               "Email: " + currentUser.getEmail()+ "\n" +
	               "Address: " + currentUser.getAddress() + "\n" +
 	               "Account Type: " + currentUser.getAccountType().toString();
	}
    
    @FXML
    public void updateAddressOptionMenu() {
    	updateSelectionMenu.setText(upddateAddressMenu.getText());
    }
    
    @FXML
    public void updatePasswordOptionMenu() {
    	updateSelectionMenu.setText(updatePasswordMenu.getText());
    }
    
    @FXML
    public void handleUpdateOption() {
    	String updateOption = updateSelectionMenu.getText();
    	
    	if (updateOption.equals("Address")) {
    		addressField.setVisible(true);
    		updatePasswordSection.setVisible(false);
    		updateActionField.setVisible(true);
    		response.setText(null);
    	} else if (updateOption.equals("Password")){
    		addressField.setVisible(false);
    		updatePasswordSection.setVisible(true);
    		updateActionField.setVisible(true);
    		response.setText(null);
    	} else {
    		response.setText("error");
    	}
    }
    
    @FXML
	public void realTimePWSec() {

		Result result = nbvcxz.estimate(PasswordField1.getText());
		
		System.out.println(PasswordField1.getText());
		
		Float prog = ((float) result.getBasicScore())/4;
		
		passwordStrengthBar.setProgress(prog);
	}
    

    // Methods to handle profile updates
    @FXML
    public void handleUpdateAction() {
        // Gather data from input fields
    	if (addressField.isVisible()) {
    		String address = addressField.getText();// get from input field;
    		if (address.equals("")||address.equals(currentUser.getAddress())) {
            	response.setText("Nothing to update.");
            }  else {
	        	// Update User model
	            String serverResponse = currentUser.updatePersonalRecordOnDB(address, "address");
	            JSONObject jsonObj = new JSONObject(serverResponse);
	            if (jsonObj.get("result").equals("SUCCESS")) {
	            	response.setText("Address got updated");
	            	currentUser.setAddress(address);
	            	displayUserInfo();
	                updatePasswordSection.setVisible(false);
	            } else if (jsonObj.get("result").equals("FAILURE")) {
	            	response.setText(jsonObj.getString("reason"));
	            }
            	
            }
    	} else if (updatePasswordSection.isVisible()) {
    		handlePasswordUpdate();
    	} else {
    		response.setText("Error! Try again");
    	}
    }
    
    @FXML
    public void handleDataPrivacySetting() {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/healthhaven/gui/DataSharingSetting.fxml"));
    		UserProfileSection.getChildren().clear();
    		UserProfileSection.getChildren().add(loader.load());
        	DataSharingSettingController controller = loader.getController();
        	controller.setUser((Patient) currentUser, userController);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    @FXML
    public void handleDeactivateAccount() {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/healthhaven/gui/accountDeactivation.fxml"));
    		UserProfileSection.getChildren().clear();
    		UserProfileSection.getChildren().add(loader.load());
        	AccountDeactivationController controller = loader.getController();
        	controller.setUser(currentUser, userController);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }
    private void handlePasswordUpdate() {
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
		
		if (passwordChecker(currentUser, rpw1).equals("success")) {
			String serverResponse = currentUser.updatePersonalRecordOnDB(rpw1, "password");
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
			    		displayUserInfo();
			            addressField.setVisible(false);
			    	}
			    }
		}		  
    }
    
    private String passwordChecker(User currentUser, String rpw1) {
		Result result = nbvcxz.estimate(rpw1);
		String remail = currentUser.getEmail();
		String rfn = currentUser.getLegal_first_name();
		String rln = currentUser.getLegal_last_name();
		LocalDate dob = currentUser.getDob();
		
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
    public void handleCancelAction() {
        // Clear all text fields
        addressField.setText("");
        PasswordField1.setText("");
        PasswordField2.setText("");
    }
    
    
}
