package org.healthhaven.controller;

import org.healthhaven.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.control.TextArea;

public class SuperadminController {

    private Superadmin superadmin;
    
    @FXML
    private TextArea recordTextArea;
    

    public void setSuperadmin(Superadmin superadmin) {
        this.superadmin = superadmin;
        listAccounts();
        // Load doctor-specific information into the dashboard
    }

    private void listAccounts() {
    	String accountRecords = superadmin.viewAccountList();;
        recordTextArea.setText(accountRecords);
    }
}
