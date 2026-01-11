package com.example.retroquiz.controller;

import com.example.retroquiz.model.*;
import com.example.retroquiz.service.GameSessionService;
import com.example.retroquiz.service.PlayerService;
import com.example.retroquiz.service.QuizService;
import com.example.retroquiz.util.UserDataInitializer;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Quiz Gameplay screen.
 */
public class QuizController implements Initializable, UserDataInitializer {

    // FXML UI Elements
    @FXML
    private Label questionNumberLabel;
    @FXML
    private Label questionTextLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private ProgressBar timerProgressBar;

    @FXML
    private Button optionAButton;
    @FXML
    private Button optionBButton;
    @FXML
    private Button optionCButton;
    @FXML
    private Button optionDButton;

    // Services
    private final QuizService quizService = new QuizService();
    private final PlayerService playerService = new PlayerService();
    private final GameSessionService gameSessionService = new GameSessionService();

    // Game State
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Player currentPlayer;
    private List<Player> allPlayers;
    private int sessionId = -1;

    // Timer Configuration
    private static final int TIMER_DURATION = 15;
    private Timeline countdown;
    private int timeRemaining;
    private boolean answerSelected = false;

    // Scoring Constants
    private static final int POINTS_CORRECT = 10;
    private static final int POINTS_WRONG = -5;
    private static final int POINTS_NO_ANSWER = -15;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("QuizController initialized.");
    }

    @Override
    public void initData(User user) {
        System.out.println("Initializing Quiz Data for user: " + user.getUsername());

        // 1. Get Session ID
        String code = user.getTournamentCode();
        if (code != null) {
            this.sessionId = gameSessionService.getSessionId(code);
        }

        // 2. Get/Create Player Object
        this.currentPlayer = playerService.getPlayerByUsername(user.getUsername());
        if (this.currentPlayer == null) {
            this.currentPlayer = playerService.createPlayer(user.getUsername());
        }
        this.currentPlayer.resetSession();

        // 3. Load Session Players (Initial Snapshot)
        if (this.sessionId != -1) {
            // Initially just get usernames, but we need Player objects
            // For now, we put strictly the current player in the list or fetch properly if
            // possible
            // But getSessionLeaderboard gives us Player objects with scores!
            this.allPlayers = gameSessionService.getSessionLeaderboard(sessionId);
        } else {
            this.allPlayers = List.of(currentPlayer);
        }

        // 4. Start Quiz
        int qCount = 10;
        if (this.sessionId != -1) {
            qCount = gameSessionService.getSessionQuestionCount(this.sessionId);
        }
        startQuiz(qCount);
    }

    public void setPlayers(List<Player> players) {
        this.allPlayers = players;
        if (players != null && !players.isEmpty()) {
            this.currentPlayer = players.get(0);
        }
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void startQuiz(int questionCount) {
        if (currentPlayer == null) {
            System.err.println("ERROR: Cannot start quiz. Player is null.");
            return;
        }

        // Show loading indicator
        questionTextLabel.setText("Loading questions...");
        enableButtons(false);

        // Load questions asynchronously to prevent UI lag
        java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            return quizService.loadRandomQuestions(questionCount);
        }).thenAccept(loadedQuestions -> {
            // Update UI on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                questions = loadedQuestions;
                if (questions.isEmpty()) {
                    questionTextLabel.setText("Error: No questions found.");
                    return;
                }

                currentQuestionIndex = 0;
                displayQuestion();
                System.out.println("Quiz started with " + questions.size() + " questions.");
            });
        }).exceptionally(ex -> {
            javafx.application.Platform.runLater(() -> {
                questionTextLabel.setText("Error loading questions: " + ex.getMessage());
                ex.printStackTrace();
            });
            return null;
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            endQuiz();
            return;
        }

        Question question = questions.get(currentQuestionIndex);
        answerSelected = false;

        questionNumberLabel.setText("Question " + (currentQuestionIndex + 1) + " / " + questions.size());
        questionTextLabel.setText(question.getQuestion());

        optionAButton.setText("A: " + question.getOptionA());
        optionBButton.setText("B: " + question.getOptionB());
        optionCButton.setText("C: " + question.getOptionC());
        optionDButton.setText("D: " + question.getOptionD());

        resetButtonStyles();
        enableButtons(true);
        startTimer();
    }

    private void startTimer() {
        timeRemaining = TIMER_DURATION;
        updateTimerDisplay();

        if (countdown != null)
            countdown.stop();

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            updateTimerDisplay();
            if (timeRemaining <= 0)
                handleTimeout();
        }));
        countdown.setCycleCount(TIMER_DURATION);
        countdown.play();
    }

    private void updateTimerDisplay() {
        timerLabel.setText("Time: " + timeRemaining + "s");
        timerProgressBar.setProgress((double) timeRemaining / TIMER_DURATION);

        if (timeRemaining <= 3)
            timerLabel.setStyle("-fx-text-fill: #ff0000;");
        else if (timeRemaining <= 7)
            timerLabel.setStyle("-fx-text-fill: #ffff00;");
        else
            timerLabel.setStyle("-fx-text-fill: #00ff00;");
    }

    private void handleTimeout() {
        if (answerSelected)
            return;

        countdown.stop();
        answerSelected = true;

        int points = POINTS_NO_ANSWER;
        ProcessScoreUpdate(points, false, 0); // 0 time taken or full duration? Logic says full.

        Question question = questions.get(currentQuestionIndex);
        highlightCorrectAnswer(question.getCorrectOption());

        // Record details
        if (sessionId != -1) {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                quizService.recordAnswer(sessionId, currentPlayer.getId(), question.getId(), null, false,
                        TIMER_DURATION,
                        points);
            });
        }

        timerLabel.setText("TIME'S UP!");
        timerLabel.setStyle("-fx-text-fill: #ff0000;");
        proceedToNextQuestion();
    }

    @FXML
    private void handleAnswerA() {
        handleAnswer('A', optionAButton);
    }

    @FXML
    private void handleAnswerB() {
        handleAnswer('B', optionBButton);
    }

    @FXML
    private void handleAnswerC() {
        handleAnswer('C', optionCButton);
    }

    @FXML
    private void handleAnswerD() {
        handleAnswer('D', optionDButton);
    }

    private void handleAnswer(char selectedOption, Button selectedButton) {
        if (answerSelected)
            return;

        countdown.stop();
        answerSelected = true;
        enableButtons(false);

        Question question = questions.get(currentQuestionIndex);
        boolean isCorrect = question.isCorrect(selectedOption);
        int timeTaken = TIMER_DURATION - timeRemaining;
        int points = quizService.calculatePoints(isCorrect, true);

        ProcessScoreUpdate(points, isCorrect, timeTaken);

        if (isCorrect) {
            selectedButton.setStyle("-fx-background-color: #00ff00; -fx-text-fill: black;");
            timerLabel.setText("CORRECT! +" + points);
            timerLabel.setStyle("-fx-text-fill: #00ff00;");
        } else {
            selectedButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");
            highlightCorrectAnswer(question.getCorrectOption());
            timerLabel.setText("WRONG! " + points);
            timerLabel.setStyle("-fx-text-fill: #ff0000;");
        }

        if (sessionId != -1) {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                quizService.recordAnswer(sessionId, currentPlayer.getId(), question.getId(), selectedOption, isCorrect,
                        timeTaken, points);
            });
        }

        currentPlayer.setLastAnswerTime(System.currentTimeMillis());
        proceedToNextQuestion();
    }

    private void ProcessScoreUpdate(int points, boolean isCorrect, int timeTaken) {
        currentPlayer.addPoints(points);
        if (isCorrect)
            currentPlayer.recordCorrectAnswer();
        else
            currentPlayer.recordWrongAnswer(); // or recordNoAnswer called in timeout

        updateScoreDisplay();

        // **CRITICAL: Update Session Score in DB**
        if (sessionId != -1) {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                gameSessionService.updatePlayerSessionScore(sessionId, currentPlayer.getId(),
                        currentPlayer.getCurrentScore());
            });
        }
    }

    private void highlightCorrectAnswer(char correctOption) {
        Button btn = switch (correctOption) {
            case 'A' -> optionAButton;
            case 'B' -> optionBButton;
            case 'C' -> optionCButton;
            case 'D' -> optionDButton;
            default -> null;
        };
        if (btn != null)
            btn.setStyle("-fx-background-color: #00ff00; -fx-text-fill: black;");
    }

    private void updateScoreDisplay() {
        scoreLabel.setText("Score: " + currentPlayer.getCurrentScore());
    }

    private void resetButtonStyles() {
        optionAButton.setStyle("");
        optionBButton.setStyle("");
        optionCButton.setStyle("");
        optionDButton.setStyle("");
    }

    private void enableButtons(boolean enable) {
        optionAButton.setDisable(!enable);
        optionBButton.setDisable(!enable);
        optionCButton.setDisable(!enable);
        optionDButton.setDisable(!enable);
    }

    private void proceedToNextQuestion() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            currentQuestionIndex++;
            displayQuestion();
        });
        pause.play();
    }

    private void endQuiz() {
        System.out.println("Quiz ended. Final score: " + currentPlayer.getCurrentScore());

        // Final sync of score and mark finished
        if (sessionId != -1) {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                gameSessionService.updatePlayerSessionScore(sessionId, currentPlayer.getId(),
                        currentPlayer.getCurrentScore());
                gameSessionService.markPlayerFinished(sessionId, currentPlayer.getId());
            });
        }

        // Update player statistics in database
        // Add current score to total points and increment games played
        currentPlayer.addToTotalPoints(currentPlayer.getCurrentScore());
        currentPlayer.incrementGamesPlayed();
        playerService.updatePlayerStats(currentPlayer);

        // Navigate to Waiting Screen if in a session, else go to results
        if (sessionId != -1) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/waiting-for-results.fxml"));
                Scene scene = new Scene(loader.load());

                WaitingForResultsController waitController = loader.getController();
                waitController.setSessionId(sessionId);
                waitController.setPlayers(allPlayers);

                Stage stage = (Stage) questionTextLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Waiting for Players");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Single player logic -> straight to results
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/result-screen.fxml"));
                Scene scene = new Scene(loader.load());

                ResultController resultController = loader.getController();
                resultController.setPlayers(List.of(currentPlayer));

                Stage stage = (Stage) questionTextLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Quiz Results");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
