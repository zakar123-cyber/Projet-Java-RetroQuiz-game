module com.example.retroquiz {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // You likely need this for ImageView/Image

    // --- ADD THIS LINE ---
    requires java.sql;
    // ---------------------

    // You likely also need this for loading FXML and initialization:
    requires java.base;
    requires com.microsoft.sqlserver.jdbc;

    opens com.example.retroquiz to javafx.fxml;
    opens com.example.retroquiz.controller to javafx.fxml;

    exports com.example.retroquiz;
    exports com.example.retroquiz.controller;
    exports com.example.retroquiz.model;
    exports com.example.retroquiz.service;
    exports com.example.retroquiz.repository;
    exports com.example.retroquiz.util;
}