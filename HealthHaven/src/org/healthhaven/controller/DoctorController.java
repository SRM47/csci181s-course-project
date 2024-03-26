package org.healthhaven.controller;

import org.healthhaven.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DoctorController {

    private Doctor doctor;

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        // Load doctor-specific information into the dashboard
    }

    // Doctor-specific methods here
}
