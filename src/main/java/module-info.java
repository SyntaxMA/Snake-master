module com.example.demo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;

    opens com.example.demo2 to javafx.fxml, com.fasterxml.jackson.databind, javafx.media;
    exports com.example.demo2;
}