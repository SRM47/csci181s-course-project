package org.healthhaven.controller;

import org.healthhaven.model.Patient;
import org.healthhaven.model.User;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

public class DataSharingSettingController{
	
	@FXML
	private StackPane userProfileSection;
	@FXML
    private TextArea policyText;
	@FXML
	private Label errorMessage;

	@FXML
    private ToggleButton toggleOpt;

    @FXML
    private Label statusLabel;
    
    private Patient currentUser;
	private UserController userController;
	
	public void setUser(Patient user, UserController userController) {
		this.currentUser = user;
		this.userController = userController;
		errorMessage.setText(null);
		
		String policy = "Data Sharing Policy\n\n"
                + "1. Introduction\n"
                + "This policy outlines how HealthHaven handles data sharing for medical research purposes.\n We respect your privacy and provide options to opt-in or opt-out of data sharing.\n\n"
                + "2. Consent for Data Sharing\n"
                + "- Opt-In: You may choose to allow us to use your data for medical research by opting in. \n To opt-in, please [describe the process or provide a link].\n"
                + "- Opt-Out: You can withdraw consent anytime, stopping any future data use for research. \n To opt-out, please [describe the process or provide a link].\n\n"
                + "3. Use of Data\n"
                + "Data shared will be used by certified data analysts for medical research. All personal \n information is anonymized to maintain confidentiality.\n\n"
                + "4. Safeguarding Your Information\n"
                + "We implement strict security measures including:\n"
                + "- De-identifying data to ensure anonymity.\n"
                + "- Enforcing robust access controls.\n"
                + "- Conducting regular security audits.\n\n"
                + "5. Changes to This Policy\n"
                + "We may update this policy and will post changes on our website. Please review periodically.\n\n"
                + "6. Contact Information\n"
                + "For questions or to exercise your rights, contact us at healthhaven845@gmail.com.\n\n"
                + "End of Policy\n"
                + "By participating, you support medical advancements while we ensure your privacy is protected.";
		
		policyText.setText(policy);
		if (currentUser.getDataSharingSetting()) {
			toggleOpt.setText("Opt-Out");
            statusLabel.setText("You are opted in.");
		} else {
			toggleOpt.setText("Opt-In");
            statusLabel.setText("You are opted out.");
		}
		
	}
	  

	@FXML
	public void handleToggle() {
		errorMessage.setText(null);
		if (toggleOpt.isSelected()) {
            toggleOpt.setText("Opt-Out");
            
            String serverResponse = currentUser.updateDataSharingSetting(true);
      
            if (serverResponse == null) {
    			errorMessage.setText("Error");
    			return;
    		} 
            JSONObject jsonObj = new JSONObject(serverResponse);
            if (jsonObj.getString("result").equals("FAILURE")) {
	        	errorMessage.setText(jsonObj.optString("reason"));
            }	else if (jsonObj.getString("result").equals("SUCCESS")) {
            	statusLabel.setText("You are opted in.");
            }
            
        } else {
            toggleOpt.setText("Opt-In");
            String serverResponse = currentUser.updateDataSharingSetting(false);
            
            if (serverResponse == null) {
    			errorMessage.setText("Error");
    			return;
    		}
            JSONObject jsonObj = new JSONObject(serverResponse);
            if (jsonObj.getString("result").equals("FAILURE")) {
	        	errorMessage.setText(jsonObj.optString("reason"));
            } else if (jsonObj.getString("result").equals("SUCCESS")) {
            	statusLabel.setText("You are opted out.");
            }
        }
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
	
}