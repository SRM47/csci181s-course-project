package org.healthhaven.controller;

import java.io.IOException;

import org.healthhaven.gui.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class DataSharingPolicyController{
	@FXML
    private TextArea policyText;
	
	private String email;
	private String userType;
    
	
	public void intialize(String email, String userType) {
		this.email = email;
		this.userType = userType;
		
		String policy = "Data Sharing Policy\n\n"
                + "1. Introduction\n"
                + "This policy outlines how HealthHaven handles data sharing for medical research purposes.\n We respect your privacy and provide options to opt-in or opt-out of data sharing.\n"
                + "Note that this data sharing policy primarily applies to Patient, who consent to preserve \n their data on our medical data management system.\n\n"
                + "2. Consent for Data Sharing\n"
                + "- Opt-In: You may choose to allow us to use your data for medical research by opting in. \n To opt-in, please [describe the process or provide a link].\n"
                + "- Opt-Out: You can withdraw consent anytime, stopping any future data use for research. \n To opt-out, please [describe the process or provide a link].\n"
                + "Note that after you create your account, you will be able to specify your option from your profile \n page.\n\n"
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
		
	}
	
	@FXML
	public void handleAgree() throws IOException {
		// loadAccountCreationPage(email,jsonObj.getString("accountType"));
    	loadAccountCreationPage(email, userType);
	}
	
	private void loadAccountCreationPage(String email, String userType) throws IOException {
		 FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/accountcreation.fxml"));
	        Parent root = loader.load();
	        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
	        
	        AccountCreationController accountController = loader.getController();
	        accountController.setAccountCreation(email, userType);
	        
	        stage.setScene(new Scene(root));
	        stage.show();
   }
	  
	
	@FXML
	public void handleGoBack() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("../gui/login.fxml"));
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
}
	
}