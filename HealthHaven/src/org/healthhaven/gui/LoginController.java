package org.healthhaven.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
	
	@FXML
	private TextField textfield;
	@FXML
	private Label label;
	@FXML
	private Button submitbutton;
	
	
	public void handleSubmit() {
		label.setText(textfield.getText());
	}
	
	

}
