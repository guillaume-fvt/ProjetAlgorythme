module org.example.projectalgorytme {
    requires javafx.controls;
    requires javafx.fxml;

    // Ouvre les packages pour que JavaFX puisse accéder aux contrôleurs et modèles via réflexion
    opens com.monapp.controller to javafx.fxml;
    opens com.monapp.model to javafx.base;

    // Exporte le package com.monapp (contenant ta classe Main)
    exports com.monapp;
}

