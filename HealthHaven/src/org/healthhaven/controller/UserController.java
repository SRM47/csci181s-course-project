package org.healthhaven.controller;

import java.io.IOException;

import org.healthhaven.model.*;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

public class UserController {
	
	@FXML
    private StackPane mainContentArea;
	
	@FXML
	private BorderPane entireScreen;
	
	@FXML
    private Button homeButton, profileButton;
	
	private User currentUser; // This could be set during login
	

    public void initialize() {
    	homeButton.setOnAction(event -> loadHomePage());
        profileButton.setOnAction(event -> loadProfilePage());
        
        loadHomePage();
    }
    
    
    @FXML
    public void loadHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            
            switch (currentUser.getAccountType()) {
	            case DOCTOR:
	                loader.setLocation(getClass().getResource("Doctor.fxml"));
	                mainContentArea.getChildren().clear();
	                mainContentArea.getChildren().add(loader.load());
	                DoctorController doctorController = loader.getController();
	                doctorController.setDoctor((Doctor) currentUser);
	                break;
	                
	            case PATIENT:
	                loader.setLocation(getClass().getResource("Patient.fxml"));
	                mainContentArea.getChildren().clear();
	                mainContentArea.getChildren().add(loader.load());
	                PatientController patientController = loader.getController();
	                patientController.setPatient((Patient) currentUser);
	                
	                break;
	            case SUPERADMIN:
	                loader.setLocation(getClass().getResource("SuperAdmin.fxml"));
	                mainContentArea.getChildren().clear();
	                mainContentArea.getChildren().add(loader.load());
	                SuperadminController superadminController = loader.getController();
	                superadminController.setSuperadmin((Superadmin) currentUser);
	                break;
	                
	            case DPO:
	                loader.setLocation(getClass().getResource("DataProtectionOfficer.fxml"));
	                mainContentArea.getChildren().clear();
	                mainContentArea.getChildren().add(loader.load());
	                DPOController dpoController = loader.getController();
	                dpoController.setDPO((DataProtectionOfficer) currentUser);
	                break;
	                
	            case DATA_ANALYST:
	                loader.setLocation(getClass().getResource("DataAnalyst.fxml"));
	                mainContentArea.getChildren().clear();
	                mainContentArea.getChildren().add(loader.load());
	                DataAnalystController dataAnalystController = loader.getController();
	                dataAnalystController.setDataAnalyst((DataAnalyst) currentUser);
	                break;
	                
	            default:
	                break;
              
            }
            
     
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void loadProfilePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserProfile.fxml"));
            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(loader.load());
            UserProfileController controller = loader.getController();
            controller.setUser(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        initialize();
    }
    
    @FXML
    public void handleLogout() {
  
        try {
        	FXMLLoader loader = new FXMLLoader();
        	loader.setLocation(getClass().getResource("login.fxml"));
			entireScreen.setCenter(loader.load());
			LoginController loginController = loader.getController();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    	

}
