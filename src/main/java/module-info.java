module org.example.projectalgorytme {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.projectalgorytme to javafx.fxml;
    exports org.example.projectalgorytme;
}