package org.healthhaven.controller;

import org.healthhaven.model.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class UserController {
	
	@FXML
    private BorderPane mainContentArea;
	
	@FXML
    private Button homeButton, profileButton;
	
	private User currentUser; // This could be set during login
	

    public void initialize() {
    	homeButton.setOnAction(event -> loadHomePage());
        profileButton.setOnAction(event -> loadProfilePage());
        
        loadHomePage();
    }
    
    private void loadHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            
            switch (currentUser.getAccountType()) {
	            case DOCTOR:
	                loader.setLocation(getClass().getResource("DoctorDashboard.fxml"));
	                mainContentArea.setCenter(loader.load());
	                DoctorController doctorController = loader.getController();
	                doctorController.setDoctor((Doctor) currentUser);
	                break;
	                
	            case PATIENT:
	                loader.setLocation(getClass().getResource("PatientDashboard.fxml"));
	                mainContentArea.setCenter(loader.load());
	                PatientController patientController = loader.getController();
	                patientController.setPatient((Patient) currentUser);
	                
	                break;
	            case SUPERADMIN:
	                loader.setLocation(getClass().getResource("SuperAdminDashboard.fxml"));
	                mainContentArea.setCenter(loader.load());
	                SuperadminController superadminController = loader.getController();
	                superadminController.setSuperadmin((Superadmin) currentUser);
	                break;
	                
	            case DPO:
	                loader.setLocation(getClass().getResource("DataProtectionOfficerDashboard.fxml"));
	                mainContentArea.setCenter(loader.load());
	                DPOController dpoController = loader.getController();
	                dpoController.setDPO((DataProtectionOfficer) currentUser);
	                break;
	                
	            case DATA_ANALYST:
	                loader.setLocation(getClass().getResource("DataAnalystDashboard.fxml"));
	                mainContentArea.setCenter(loader.load());
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

    private void loadProfilePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserProfile.fxml"));
            mainContentArea.setCenter(loader.load());
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
    	

}
