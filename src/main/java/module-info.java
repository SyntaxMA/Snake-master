module com.example.demo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens com.example.demo2 to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.demo2;
}