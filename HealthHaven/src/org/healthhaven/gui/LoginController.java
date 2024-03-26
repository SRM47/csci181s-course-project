package org.healthhaven.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

import javafx.event.ActionEvent;

public class LoginController extends Main{
	
	@FXML
	private TextField textfield;
	@FXML
	private TextField email;
	@FXML
	private Label label;
	@FXML
	private Button submitbutton;
	@FXML
	private PasswordField password;
	@FXML
	private Button acbutton;
	
	
	
	public void handleSubmit() {
		String lemail;
		lemail = email.getText();
		
		String lpw;
		lpw = password.getText();
		
		System.out.println(lemail + lpw);
	}
	

	public void acPage(ActionEvent actionEvent) throws IOException {
        loadPage("accountcreation.fxml");
    }

    private void loadPage(String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
	
}
