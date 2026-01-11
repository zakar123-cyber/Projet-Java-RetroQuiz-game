package com.example.retroquiz.controller;

import com.example.retroquiz.HelloApplication;
import com.example.retroquiz.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML
    private TableView<UserRaw> usersTable;
    @FXML
    private TableColumn<UserRaw, String> usernameColumn;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final UserService userService = new UserService();
    private ObservableList<UserRaw> userList = FXCollections.observableArrayList();

    public static class UserRaw {
        private final SimpleStringProperty username;

        public UserRaw(String username) {
            this.username = new SimpleStringProperty(username);
        }

        public String getUsername() {
            return username.get();
        }

        public void setUsername(String username) {
            this.username.set(username);
        }

        public SimpleStringProperty usernameProperty() {
            return username;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usersTable.setItems(userList);

        // Handle selection
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                usernameField.setText(newVal.getUsername());
                passwordField.clear(); // Don't show password
            }
        });

        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        for (String u : userService.getAllUsers()) {
            userList.add(new UserRaw(u));
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUsers();
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        UserRaw selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to update.");
            return;
        }

        String currentUsername = selected.getUsername();
        String newUsername = usernameField.getText();
        String newPassword = passwordField.getText();

        if (newUsername == null || newUsername.trim().isEmpty()) {
            showAlert("Invalid Input", "Username cannot be empty.");
            return;
        }

        if (userService.updateUser(currentUsername, newUsername, newPassword)) {
            showAlert("Success", "User updated successfully.");
            loadUsers();
            usernameField.clear();
            passwordField.clear();
        } else {
            showAlert("Error", "Failed to update user. Username might be taken.");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        UserRaw selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete user: " + selected.getUsername() + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userService.deleteUser(selected.getUsername())) {
                showAlert("Success", "User deleted.");
                loadUsers();
                usernameField.clear();
                passwordField.clear();
            } else {
                showAlert("Error", "Failed to delete user.");
            }
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
