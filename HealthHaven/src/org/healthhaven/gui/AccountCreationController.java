package org.healthhaven.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountCreationController {
	
	@FXML
	private TextField textfield;
	@FXML
	private Label label;
	@FXML
	private Button submitbutton;
	@FXML
	private Button loginpage;
	@FXML
	private MenuButton atmenu;
	@FXML
	private MenuItem atmenuPA;
	@FXML
	private MenuItem atmenuDR;
	@FXML
	private MenuItem atmenuDPO;
	@FXML
	private MenuItem atmenuDA;
	@FXML
	private TextField email;
	@FXML
	private PasswordField pw;
	@FXML
	private TextField fn;
	@FXML
	private TextField ln;
	@FXML
	private TextField address;
	@FXML
	private DatePicker dob;
	
//	Couldn't figure out how to implement drop down, so this is what we have
	public void updateAT1() {
		atmenu.setText(atmenuDR.getText());
	}
	
	public void updateAT2() {
		atmenu.setText(atmenuPA.getText());
	}
	
	public void updateAT3() {
		atmenu.setText(atmenuDPO.getText());
	}
	
	public void updateAT4() {
		atmenu.setText(atmenuDA.getText());
	}
	
	public void handleSubmit() {
		String rat = atmenu.getText();
		String remail = email.getText();
		String rpw = pw.getText();
		String rfn = fn.getText();
		String rln = ln.getText();
		String raddress = address.getText();
		String rdob = dob.getValue().toString();
		
		System.out.println(rat + remail + rpw + rfn + rln + raddress + rdob);
	}
	
	public void loginPage(ActionEvent actionEvent) throws IOException {
        loadPage("login.fxml");
    }

    private void loadPage(String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) Main.getFirstStage().getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
	

}
