package com.monapp;

import com.monapp.controller.MainController;
import com.monapp.model.ApplicationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private ApplicationManager applicationManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Créer le manager (logique métier) et charger éventuellement des données
            applicationManager = new ApplicationManager();
            // applicationManager.chargerDonnees(); // si tu as implémenté la persistance

            // Charger le FXML principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/main-view.fxml"));
            BorderPane root = loader.load();

            // Récupérer le contrôleur et lui injecter l'ApplicationManager
            MainController mainController = loader.getController();
            mainController.setApplicationManager(applicationManager);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Gestion de Tâches Collaboratives");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
