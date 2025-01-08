module org.example.projectalgorytme {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.monapp.controller to javafx.fxml;
    opens com.monapp.model to javafx.base;

    exports com.monapp;
}
