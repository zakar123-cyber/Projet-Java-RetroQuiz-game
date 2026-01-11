package com.example.retroquiz;

import com.example.retroquiz.model.User; // <-- Import User
import com.example.retroquiz.util.UserDataInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Initial scene load
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/retroquiz.fxml")), 800, 600);

        stage.setTitle("RetroQuiz Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Helper method to switch scenes (standard, no data passing).
     */
    public static void setScene(String fxmlFileName) throws IOException {
        setScene(fxmlFileName, null); // Call the overloaded version with null data
    }

    /**
     * Helper method to switch scenes and pass User data to the new controller.
     * This is the NEW method we need.
     */
    public static void setScene(String fxmlFileName, User user) throws IOException {
        URL fxmlUrl = HelloApplication.class.getResource("/fxml/" + fxmlFileName);

        if (fxmlUrl == null) {
            System.err.println("CRITICAL FXML ERROR: Could not find FXML file: " + fxmlFileName);
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Scene newScene = new Scene(fxmlLoader.load(),
                primaryStage.getScene().getWidth(),
                primaryStage.getScene().getHeight());

        // --- Data Passing Logic ---
        Object controller = fxmlLoader.getController();

        // Check if the new controller needs initialization data (i.e., the User object)
        if (user != null && controller instanceof UserDataInitializer) {
            System.out.println("Initializing new scene controller with User data.");
            ((UserDataInitializer) controller).initData(user);
        }
        // --------------------------

        primaryStage.setScene(newScene);
        primaryStage.setTitle("RetroQuiz Quiz"); // Updated title
    }

    public static void main(String[] args) {
        launch();
    }
}
