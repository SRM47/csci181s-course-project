package org.healthhaven.controller;

import org.healthhaven.model.User;
import org.healthhaven.model.*;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AccountDeactivationController{
	@FXML
	private StackPane userProfileSection;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label response;
	@FXML
	private Button deactivateButton;
	@FXML
	private Button cancelButton;
	@FXML
	private VBox ConfirmDeactivationSection;
	@FXML
	private Button goBackButton;
	
	private User currentUser;
	private UserController userController;
	
	public void setUser(User user, UserController userController) {
		this.currentUser = user;
		this.userController = userController;
		ConfirmDeactivationSection.setVisible(false);
	}

	@FXML
	public void handleDeactivation() {
		String rpw = passwordField.getText();
		response.setText("");
		ConfirmDeactivationSection.setVisible(false);
		
		if (rpw.equals("")) {
			response.setText("Please enter the password");
			return;
		}
		
		String serverResponse = currentUser.deactivate(rpw, "VALIDATE");
		if (serverResponse.equals(null)){
			response.setText("Likely error in communciating with the port");
			return;
		}
		JSONObject json = new JSONObject(serverResponse);
		if (json.getString("result").equals("FAILURE")) {
			response.setText(json.getString("reason"));
		} else if (json.getString("result").equals("SUCCESS")) {
			ConfirmDeactivationSection.setVisible(true);
		}
	}
	
	@FXML
	public void handleConfirmYes() {
		String serverResponse = currentUser.deactivate(null, "DEACTIVATE");
		if (serverResponse.equals(null)){
			response.setText("Likely error in communciating with the port");
			return;
		}
		JSONObject json = new JSONObject(serverResponse);
		if (json.getString("result").equals("FAILURE")) {
			response.setText(json.getString("reason"));
		} else if (json.getString("result").equals("SUCCESS")){	
			userController.handleLogout();
			//TODO: page navigation
		}
		
	}
	
	@FXML
	public void handleConfirmNo() {
		ConfirmDeactivationSection.setVisible(false);
	}
	
	@FXML
	public void handleGoBack() {
		try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/healthhaven/gui/UserProfile.fxml"));
        	userProfileSection.getChildren().clear();
    		userProfileSection.getChildren().add(loader.load());
        	UserProfileController controller = loader.getController();
        	controller.setUser(currentUser, userController);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
	@FXML
	public void handleCancel() {
		ConfirmDeactivationSection.setVisible(false);
		response.setText("");
		passwordField.setText("");
	}
}