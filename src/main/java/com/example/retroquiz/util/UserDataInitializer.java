// src/main/java/com/example/retroquiz/UserDataInitializer.java

package com.example.retroquiz.util;

import com.example.retroquiz.model.User; // Important: use the correct package for User

public interface UserDataInitializer {
    /**
     * Initializes the controller with data, typically after the FXML has been loaded.
     * @param user The User object containing guest/login details.
     */
    void initData(User user);
}
