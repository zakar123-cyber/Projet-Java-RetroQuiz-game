package com.example.retroquiz.controller;

import com.example.retroquiz.HelloApplication;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ImageView backgroundView;
    @FXML
    private Button adminLoginButton;
    @FXML
    private Button participantButton;

    private static final String GIF_PATH = "/retro-neon.gif";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnimatedBackground();
        addPulseAnimation(adminLoginButton);
        addPulseAnimation(participantButton);
    }

    // --- EVENT HANDLERS ---

    @FXML
    private void handleHostLogin(ActionEvent event) {
        try {
            HelloApplication.setScene("host_login.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load host_login.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleParticipantJoin(ActionEvent event) {
        try {
            HelloApplication.setScene("guest_registration.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load guest_registration.fxml");
            e.printStackTrace();
        }
    }

    // --- UTILITY METHODS ---

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

    private void addPulseAnimation(Node node) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), node);
        scaleUp.setToX(1.15);
        scaleUp.setToY(1.15);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        node.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
        });

        node.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });
    }
}
