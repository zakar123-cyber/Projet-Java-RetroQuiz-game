package com.example.retroquiz.controller;

import com.example.retroquiz.model.*;
import com.example.retroquiz.service.*;
import com.example.retroquiz.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private ImageView backgroundView;
    @FXML
    private Label welcomeLabel;

    private static final String GIF_PATH = "/images/retro-neon.gif";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnimatedBackground();
    }

    private void loadAnimatedBackground() {
        try {
            URL imageUrl = getClass().getResource(GIF_PATH);
            if (imageUrl == null) {
                System.err.println("CRITICAL FILE ERROR: Could not find GIF at: " + GIF_PATH);
                return;
            }
            Image image = new Image(imageUrl.toExternalForm(), true);
            backgroundView.setImage(image);

            backgroundView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    backgroundView.fitWidthProperty().bind(newScene.widthProperty());
                    backgroundView.fitHeightProperty().bind(newScene.heightProperty());
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to load local background image file.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageQuestions(ActionEvent event) {
        try {
            HelloApplication.setScene("question_management.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load question_management.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageUsers(ActionEvent event) {
        try {
            HelloApplication.setScene("user_management.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load user_management.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewLeaderboard(ActionEvent event) {
        try {
            HelloApplication.setScene("leaderboard.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load leaderboard.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            HelloApplication.setScene("retroquiz.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load retroquiz.fxml");
            e.printStackTrace();
        }
    }
}
