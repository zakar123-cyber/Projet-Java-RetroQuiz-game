package com.example.retroquiz.controller;

import com.example.retroquiz.model.*; // <-- CORRECTED IMPORT

import com.example.retroquiz.service.*; // <-- CORRECTED IMPORT

import com.example.retroquiz.HelloApplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label; // Ensure Label is imported
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GuestRegistrationController implements Initializable {

    // --- FXML UI Elements ---

    // Feedback Labels (Must match FXML fx:id)
    @FXML
    private Label guestFeedbackLabel;
    @FXML
    private Label loginFeedbackLabel;
    @FXML
    private Label registerFeedbackLabel;

    @FXML
    private ImageView backgroundView;
    @FXML
    private VBox neonBox;

    // Dynamic Sections (Containers)
    @FXML
    private VBox guestSection;
    @FXML
    private VBox loginSection;
    @FXML
    private VBox registerSection;

    // Action Buttons
    @FXML
    private Button joinGuestButton;
    @FXML
    private Button loginUserButton;
    @FXML
    private Button registerButton;

    // Guest Fields
    @FXML
    private TextField guestNameField;

    // Login Fields
    @FXML
    private TextField loginUsernameField;
    @FXML
    private TextField loginPasswordField;

    // Register Fields
    @FXML
    private TextField registerUsernameField;
    @FXML
    private TextField registerPasswordField;

    private static final String GIF_PATH = "/images/retro-neon.gif";

    // Instantiate the mock backend service
    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnimatedBackground();
        // Initialize to Guest Mode (Cyan)
        applyStyle("cyan");
    }

    // --- Dynamic Styling Utility ---
    // (Existing code for applyStyle is retained)
    private void applyStyle(String color) {
        String neonColor;
        String actionStyleClass;

        switch (color) {
            case "yellow":
                neonColor = "#ffff00";
                actionStyleClass = "neon-button yellow";
                break;
            case "magenta":
                neonColor = "#ff00ff";
                actionStyleClass = "neon-button magenta";
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

        joinGuestButton.getStyleClass().setAll(actionStyleClass.split(" "));
        loginUserButton.getStyleClass().setAll(actionStyleClass.split(" "));
        registerButton.getStyleClass().setAll(actionStyleClass.split(" "));
    }

    // --- Section Visibility Handlers ---
    // (Existing code for hideAllSections, showGuestSection, showLoginSection,
    // showRegisterSection is retained)

    private void hideAllSections() {
        guestSection.setVisible(false);
        guestSection.setManaged(false);
        loginSection.setVisible(false);
        loginSection.setManaged(false);
        registerSection.setVisible(false);
        registerSection.setManaged(false);
    }

    @FXML
    private void showGuestSection(ActionEvent event) {
        hideAllSections();
        guestSection.setVisible(true);
        guestSection.setManaged(true);
        applyStyle("cyan");
    }

    @FXML
    private void showLoginSection(ActionEvent event) {
        hideAllSections();
        loginSection.setVisible(true);
        loginSection.setManaged(true);
        applyStyle("yellow");
    }

    @FXML
    private void showRegisterSection(ActionEvent event) {
        hideAllSections();
        registerSection.setVisible(true);
        registerSection.setManaged(true);
        applyStyle("magenta");
    }

    // --- Action Event Handlers (IMPLEMENTED BACKEND LOGIC) ---

    @FXML
    private void handleJoinQuiz(ActionEvent event) {
        // GUEST JOIN
        String guestName = guestNameField.getText().trim();
        guestFeedbackLabel.setText(""); // Clear previous message

        if (guestName.isEmpty()) {
            guestFeedbackLabel.setText("⚠️ Please enter a guest name.");
            return;
        }

        // SUCCESS: Create guest user and proceed
        // NOTE: We rely on the lobby/quiz controller to actually create the DB entry
        // OR we create it here.
        // Given PlayerService modification, we should create it here to flag it properly.
        PlayerService playerService = new PlayerService();
        Player guestPlayer = playerService.createPlayer(guestName, true);
        
        if (guestPlayer != null) {
            User currentUser = new User(guestPlayer.getUsername(), null);
            proceedToQuizLobby(currentUser);
        } else {
             guestFeedbackLabel.setText("⚠️ Failed to create guest session.");
        }
    }

    @FXML
    private void handleLoginAndJoin(ActionEvent event) {
        // EXISTING USER LOGIN
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText();
        loginFeedbackLabel.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            loginFeedbackLabel.setText("⚠️ Username and Password are required.");
            return;
        }

        if (userService.loginUser(username, password)) {
            // SUCCESS: Create logged-in user and proceed
            User currentUser = new User(username, null);
            proceedToQuizLobby(currentUser);
        } else {
            loginFeedbackLabel.setText("❌ Login failed. Check username and password.");
        }
    }

    @FXML
    private void handleRegisterAndJoin(ActionEvent event) {
        // NEW USER REGISTRATION
        String username = registerUsernameField.getText().trim();
        String password = registerPasswordField.getText();
        registerFeedbackLabel.setText("");

        if (username.length() < 3 || password.length() < 4) {
            registerFeedbackLabel.setText("⚠️ Username needs 3+ chars, Password needs 4+ chars.");
            return;
        }

        if (userService.registerUser(username, password)) {
            // SUCCESS: Create user and proceed
            User currentUser = new User(username, null);
            registerFeedbackLabel.setText("✅ Registration successful! Starting game...");
            proceedToQuizLobby(currentUser);
        } else {
            registerFeedbackLabel.setText("❌ Registration failed. Username already taken.");
        }
    }

    // --- Navigation and Data Passing ---

    /**
     * Helper method to switch to the main lobby scene and pass the User object.
     * 
     * @param user The User (Guest or Registered) who is joining.
     */
    // Inside GuestRegistrationController.java

    private void proceedToQuizLobby(User user) {
        try {
            // Redirect to main lobby after authentication
            HelloApplication.setScene("main-lobby.fxml", user);

        } catch (IOException e) {
            System.err.println("Failed to load main-lobby.fxml.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMenu(ActionEvent event) {
        try {
            HelloApplication.setScene("retroquiz.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load retroquiz.fxml");
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

    // --- Background Loading Utility ---
    // (Existing code for loadAnimatedBackground is retained)
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
