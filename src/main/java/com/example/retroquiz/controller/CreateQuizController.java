package com.example.retroquiz.controller;

import com.example.retroquiz.HelloApplication;
import com.example.retroquiz.service.QuizService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateQuizController implements Initializable {

    @FXML
    private ImageView backgroundView;
    @FXML
    private TextArea questionField;
    @FXML
    private TextField optionAField;
    @FXML
    private TextField optionBField;
    @FXML
    private TextField optionCField;
    @FXML
    private TextField optionDField;
    @FXML
    private ComboBox<String> correctOptionBox;
    @FXML
    private TextField categoryField;
    @FXML
    private ComboBox<String> difficultyBox;
    @FXML
    private Label statusLabel;

    private static final String GIF_PATH = "/images/retro-neon.gif";
    private final QuizService quizService = new QuizService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnimatedBackground();
        
        // Initialize ComboBoxes
        correctOptionBox.getItems().addAll("A", "B", "C", "D");
        difficultyBox.getItems().addAll("EASY", "MEDIUM", "HARD");
        difficultyBox.setValue("MEDIUM");
    }

    private void loadAnimatedBackground() {
        try {
            URL imageUrl = getClass().getResource(GIF_PATH);
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toExternalForm(), true);
                backgroundView.setImage(image);

                backgroundView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        backgroundView.fitWidthProperty().bind(newScene.widthProperty());
                        backgroundView.fitHeightProperty().bind(newScene.heightProperty());
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Failed to load background image.");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String question = questionField.getText();
        String aptA = optionAField.getText();
        String optB = optionBField.getText();
        String optC = optionCField.getText();
        String optD = optionDField.getText();
        String correct = correctOptionBox.getValue();
        String category = categoryField.getText();
        String difficulty = difficultyBox.getValue();

        // Validation
        if (isEmpty(question) || isEmpty(aptA) || isEmpty(optB) || isEmpty(optC) || isEmpty(optD) || correct == null) {
            statusLabel.setText("⚠️ Please fill in all fields and select a correct answer.");
            statusLabel.setStyle("-fx-text-fill: #ff00ff;"); // Magenta for error
            return;
        }

        if (isEmpty(category)) {
            category = "General"; // Default category
        }

        boolean success = quizService.addQuestion(question, aptA, optB, optC, optD, correct.charAt(0), category, difficulty);

        if (success) {
            statusLabel.setText("✅ Question Saved Successfully!");
            statusLabel.setStyle("-fx-text-fill: #00ffff;"); // Cyan for success
            clearFields();
        } else {
            statusLabel.setText("❌ Failed to save question. Database error.");
            statusLabel.setStyle("-fx-text-fill: #ff0000;"); // Red for error
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            HelloApplication.setScene("admin_dashboard.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void clearFields() {
        questionField.clear();
        optionAField.clear();
        optionBField.clear();
        optionCField.clear();
        optionDField.clear();
        correctOptionBox.setValue(null);
        categoryField.clear();
        difficultyBox.setValue("MEDIUM");
    }
}
