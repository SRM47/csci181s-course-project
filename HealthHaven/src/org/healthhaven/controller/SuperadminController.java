package org.healthhaven.controller;

import org.healthhaven.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SuperadminController {

    private Superadmin superadmin;

    public void setSuperadmin(Superadmin superadmin) {
        this.superadmin = superadmin;
        // Load doctor-specific information into the dashboard
    }

    // Doctor-specific methods here
}
