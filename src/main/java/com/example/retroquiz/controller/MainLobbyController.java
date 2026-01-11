package com.example.retroquiz.controller;

import com.example.retroquiz.HelloApplication;
import com.example.retroquiz.model.User;
import com.example.retroquiz.service.GameSessionService;
import com.example.retroquiz.util.UserDataInitializer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Main Lobby screen.
 * This screen is accessible after authentication and allows users to:
 * 1. Host a new game (generates a session code)
 * 2. Join an existing game (using a session code)
 */
public class MainLobbyController implements Initializable, UserDataInitializer {

    @FXML
    private ImageView backgroundView;

    @FXML
    private VBox neonBox;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Button hostGameButton;

    @FXML
    private Button joinGameButton;

    @FXML
    private VBox hostSection;

    @FXML
    private VBox joinSection;

    @FXML
    private Label sessionCodeLabel;

    @FXML
    private Button startHostedGameButton;

    @FXML
    private TextField joinCodeField;

    @FXML
    private Button confirmJoinButton;

    private static final String GIF_PATH = "/images/retro-neon.gif";

    private User currentUser;
    private GameSessionService gameSessionService;
    private String currentSessionCode;
    private int currentSessionId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameSessionService = new GameSessionService();
        loadAnimatedBackground();
        applyStyle("cyan");

        // Initially hide host and join sections
        hideAllSections();
    }

    /**
     * Required method from UserDataInitializer interface.
     * This is called by HelloApplication.setScene() immediately after loading the
     * FXML.
     */
    @Override
    public void initData(User user) {
        this.currentUser = user;

        // Display username in the middle
        String displayText = user.isGuest() ? "Guest: " + user.getUsername() : user.getUsername();
        usernameLabel.setText(displayText);

        feedbackLabel.setText("Welcome! Choose an option below.");
        System.out.println("Main Lobby Loaded: User '" + user.getUsername() + "'");
    }

    /**
     * Handle Host Game button click.
     * Generates a session code and shows it to the user.
     */
    @FXML
    private void handleHostGame(ActionEvent event) {
        if (currentUser == null) {
            feedbackLabel.setText("⚠️ Error: User not initialized.");
            return;
        }

        // Generate session code
        String sessionCode = gameSessionService.createGameSession(
                currentUser.getUsername(),
                10, // Default question count
                15 // Default timer duration
        );

        if (sessionCode != null) {
            currentSessionCode = sessionCode;
            currentSessionId = gameSessionService.getSessionId(sessionCode);

            // Show host section with session code
            hideAllSections();
            hostSection.setVisible(true);
            hostSection.setManaged(true);
            sessionCodeLabel.setText("SESSION CODE: " + sessionCode);
            feedbackLabel.setText("✅ Game session created! Share this code with other players.");
            applyStyle("cyan");
        } else {
            feedbackLabel.setText("❌ Failed to create game session. Please try again.");
        }
    }

    /**
     * Handle Join Game button click.
     * Shows input field for session code.
     */
    @FXML
    private void handleJoinGame(ActionEvent event) {
        hideAllSections();
        joinSection.setVisible(true);
        joinSection.setManaged(true);
        joinCodeField.clear();
        feedbackLabel.setText("Enter the session code to join:");
        applyStyle("yellow");
    }

    /**
     * Handle Confirm Join button click.
     * Validates the session code and joins the game.
     */
    @FXML
    private void handleConfirmJoin(ActionEvent event) {
        String code = joinCodeField.getText().trim().toUpperCase();

        if (code.isEmpty()) {
            feedbackLabel.setText("⚠️ Please enter a session code.");
            return;
        }

        if (code.length() != 6) {
            feedbackLabel.setText("⚠️ Session code must be 6 characters.");
            return;
        }

        // Try to join the session
        int sessionId = gameSessionService.joinGameSession(code, currentUser.getUsername());

        if (sessionId > 0) {
            currentSessionCode = code;
            currentSessionId = sessionId;
            feedbackLabel.setText("✅ Successfully joined session: " + code);

            // Navigate to quiz lobby
            proceedToQuizLobby();
        } else {
            feedbackLabel.setText("❌ Failed to join session. Code may be invalid or session is full.");
        }
    }

    /**
     * Handle Start Hosted Game button click.
     * Navigates to quiz lobby with the hosted session.
     */
    @FXML
    private void handleStartHostedGame(ActionEvent event) {
        if (currentSessionCode == null || currentSessionId < 0) {
            feedbackLabel.setText("⚠️ No active session. Please host a game first.");
            return;
        }

        proceedToQuizLobby();
    }

    /**
     * Navigate to quiz lobby with current session information.
     */
    private void proceedToQuizLobby() {
        try {
            // Update user with session code
            User userWithSession = new User(currentUser.getUsername(), currentSessionCode);

            // Navigate to quiz lobby (which should handle the session)
            HelloApplication.setScene("quiz-lobby.fxml", userWithSession);
        } catch (IOException e) {
            System.err.println("Failed to load quiz-lobby.fxml");
            e.printStackTrace();
            feedbackLabel.setText("❌ Error: Failed to navigate to quiz lobby.");
        }
    }

    /**
     * Handle Back to Menu button click.
     */
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        try {
            HelloApplication.setScene("retroquiz.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load retroquiz.fxml");
            e.printStackTrace();
        }
    }

    /**
     * Hide all dynamic sections.
     */
    private void hideAllSections() {
        hostSection.setVisible(false);
        hostSection.setManaged(false);
        joinSection.setVisible(false);
        joinSection.setManaged(false);
    }

    /**
     * Apply neon styling to the UI.
     */
    private void applyStyle(String color) {
        String neonColor;
        String actionStyleClass;

        switch (color) {
            case "yellow":
                neonColor = "#ffff00";
                actionStyleClass = "neon-button yellow";
                break;
            case "cyan":
            default:
                neonColor = "#00ffff";
                actionStyleClass = "neon-button cyan";
                break;
        }

        neonBox.setStyle(
                "-fx-neon-color: " + neonColor + ";" +
                        "-fx-input-color: " + neonColor + ";" +
                        "-fx-secondary-text-color: " + neonColor + ";" +
                        "-fx-effect: dropshadow(gaussian, " + neonColor + ", 20, 0.8, 0, 0);");

        hostGameButton.getStyleClass().setAll(actionStyleClass.split(" "));
        joinGameButton.getStyleClass().setAll(actionStyleClass.split(" "));
    }

    /**
     * Load animated background GIF.
     */
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
}
