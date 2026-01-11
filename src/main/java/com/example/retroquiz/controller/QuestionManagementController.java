package com.example.retroquiz.controller;

import com.example.retroquiz.HelloApplication;
import com.example.retroquiz.model.Question;
import com.example.retroquiz.service.QuizService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class QuestionManagementController implements Initializable {

    @FXML
    private TableView<QuestionRow> questionsTable;
    @FXML
    private TableColumn<QuestionRow, Integer> idColumn;
    @FXML
    private TableColumn<QuestionRow, String> questionColumn;
    @FXML
    private TableColumn<QuestionRow, String> categoryColumn;
    @FXML
    private TableColumn<QuestionRow, String> difficultyColumn;
    @FXML
    private TableColumn<QuestionRow, String> correctColumn;

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
    private ComboBox<String> correctAnswerCombo;
    @FXML
    private TextField categoryField;
    @FXML
    private ComboBox<String> difficultyCombo;

    private final QuizService quizService = new QuizService();
    private ObservableList<QuestionRow> questionList = FXCollections.observableArrayList();

    public static class QuestionRow {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty question;
        private final SimpleStringProperty category;
        private final SimpleStringProperty difficulty;
        private final SimpleStringProperty correctAnswer;
        private final Question originalQuestion;

        public QuestionRow(Question q) {
            this.id = new SimpleIntegerProperty(q.getId());
            this.question = new SimpleStringProperty(q.getQuestion());
            this.category = new SimpleStringProperty(q.getCategory());
            this.difficulty = new SimpleStringProperty(q.getDifficulty());
            this.correctAnswer = new SimpleStringProperty(String.valueOf(q.getCorrectOption()));
            this.originalQuestion = q;
        }

        public int getId() {
            return id.get();
        }

        public String getQuestion() {
            return question.get();
        }

        public String getCategory() {
            return category.get();
        }

        public String getDifficulty() {
            return difficulty.get();
        }

        public String getCorrectAnswer() {
            return correctAnswer.get();
        }

        public Question getOriginalQuestion() {
            return originalQuestion;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().id.asObject());
        questionColumn.setCellValueFactory(cellData -> cellData.getValue().question);
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().category);
        difficultyColumn.setCellValueFactory(cellData -> cellData.getValue().difficulty);
        correctColumn.setCellValueFactory(cellData -> cellData.getValue().correctAnswer);

        questionsTable.setItems(questionList);

        // Setup combo boxes
        correctAnswerCombo.setItems(FXCollections.observableArrayList("A", "B", "C", "D"));
        difficultyCombo.setItems(FXCollections.observableArrayList("EASY", "MEDIUM", "HARD"));

        // Handle selection
        questionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateFields(newVal.getOriginalQuestion());
            }
        });

        loadQuestions();
    }

    private void loadQuestions() {
        questionList.clear();
        for (Question q : quizService.getAllQuestions()) {
            questionList.add(new QuestionRow(q));
        }
    }

    private void populateFields(Question q) {
        questionField.setText(q.getQuestion());
        optionAField.setText(q.getOptionA());
        optionBField.setText(q.getOptionB());
        optionCField.setText(q.getOptionC());
        optionDField.setText(q.getOptionD());
        correctAnswerCombo.setValue(String.valueOf(q.getCorrectOption()));
        categoryField.setText(q.getCategory());
        difficultyCombo.setValue(q.getDifficulty());
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateFields()) {
            showAlert("Invalid Input", "Please fill in all fields correctly.");
            return;
        }

        String question = questionField.getText();
        String optionA = optionAField.getText();
        String optionB = optionBField.getText();
        String optionC = optionCField.getText();
        String optionD = optionDField.getText();
        char correctAnswer = correctAnswerCombo.getValue().charAt(0);
        String category = categoryField.getText();
        String difficulty = difficultyCombo.getValue();

        if (quizService.addQuestion(question, optionA, optionB, optionC, optionD,
                correctAnswer, category, difficulty)) {
            showAlert("Success", "Question added successfully!");
            loadQuestions();
            handleClear(null);
        } else {
            showAlert("Error", "Failed to add question.");
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        QuestionRow selected = questionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a question to update.");
            return;
        }

        if (!validateFields()) {
            showAlert("Invalid Input", "Please fill in all fields correctly.");
            return;
        }

        int id = selected.getId();
        String question = questionField.getText();
        String optionA = optionAField.getText();
        String optionB = optionBField.getText();
        String optionC = optionCField.getText();
        String optionD = optionDField.getText();
        char correctAnswer = correctAnswerCombo.getValue().charAt(0);
        String category = categoryField.getText();
        String difficulty = difficultyCombo.getValue();

        if (quizService.updateQuestion(id, question, optionA, optionB, optionC, optionD,
                correctAnswer, category, difficulty)) {
            showAlert("Success", "Question updated successfully!");
            loadQuestions();
            handleClear(null);
        } else {
            showAlert("Error", "Failed to update question.");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        QuestionRow selected = questionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a question to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Question");
        alert.setHeaderText("Are you sure you want to delete this question?");
        alert.setContentText("ID: " + selected.getId() + "\n" + selected.getQuestion());
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (quizService.deleteQuestion(selected.getId())) {
                showAlert("Success", "Question deleted.");
                loadQuestions();
                handleClear(null);
            } else {
                showAlert("Error", "Failed to delete question.");
            }
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        questionField.clear();
        optionAField.clear();
        optionBField.clear();
        optionCField.clear();
        optionDField.clear();
        correctAnswerCombo.setValue(null);
        categoryField.clear();
        difficultyCombo.setValue("MEDIUM");
        questionsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadQuestions();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            HelloApplication.setScene("admin_dashboard.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        return questionField.getText() != null && !questionField.getText().trim().isEmpty() &&
                optionAField.getText() != null && !optionAField.getText().trim().isEmpty() &&
                optionBField.getText() != null && !optionBField.getText().trim().isEmpty() &&
                optionCField.getText() != null && !optionCField.getText().trim().isEmpty() &&
                optionDField.getText() != null && !optionDField.getText().trim().isEmpty() &&
                correctAnswerCombo.getValue() != null &&
                categoryField.getText() != null && !categoryField.getText().trim().isEmpty() &&
                difficultyCombo.getValue() != null;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
