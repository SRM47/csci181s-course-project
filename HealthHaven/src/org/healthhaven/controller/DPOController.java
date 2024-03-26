package org.healthhaven.controller;

import org.healthhaven.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DPOController {

    private DataProtectionOfficer dpo;

    public void setDPO(DataProtectionOfficer dpo) {
        this.dpo = dpo;
        // Load doctor-specific information into the dashboard
    }

    // Doctor-specific methods here
}
