package org.healthhaven.controller;

import java.io.IOException;
import java.time.LocalDate;

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
	private Button homeButton, profileButton, logoutButton;

	private User currentUser;// This could be set during login

	@FXML
	public void loadHomePage() {
		try {
			System.out.println("Home Page loading");
			FXMLLoader loader = new FXMLLoader();

			switch (currentUser.getAccountType()) {
			case DOCTOR:
				loader.setLocation(getClass().getResource("/org/healthhaven/gui/Doctor.fxml"));
				mainContentArea.getChildren().clear();
				mainContentArea.getChildren().add(loader.load());
				DoctorController doctorController = loader.getController();
				doctorController.setDoctor((Doctor) currentUser);
				break;

			case PATIENT:
				loader.setLocation(getClass().getResource("/org/healthhaven/gui/Patient.fxml"));
				mainContentArea.getChildren().clear();
				mainContentArea.getChildren().add(loader.load());
				PatientController patientController = loader.getController();
				patientController.setPatient((Patient) currentUser);
				break;
				
			case SUPERADMIN:
				loader.setLocation(getClass().getResource("/org/healthhaven/gui/SuperAdmin.fxml"));
				mainContentArea.getChildren().clear();
				mainContentArea.getChildren().add(loader.load());
				SuperadminController superadminController = loader.getController();
				superadminController.setSuperadmin((Superadmin) currentUser);
				break;

			case DATA_ANALYST:
				loader.setLocation(getClass().getResource("/org/healthhaven/gui/DataAnalyst.fxml"));
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/healthhaven/gui/UserProfile.fxml"));
			mainContentArea.getChildren().clear();
			mainContentArea.getChildren().add(loader.load());
			UserProfileController controller = loader.getController();
			controller.setUser(currentUser, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCurrentUser(User user) {
		this.currentUser = user;
		loadHomePage();
	}

	@FXML
	public void handleLogout() {
		
		currentUser.logout();

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/org/healthhaven/gui/login.fxml"));
			entireScreen.getChildren().clear();
			entireScreen.setCenter(loader.load());
			LoginController loginController = loader.getController();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
