package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private ApplicationManager applicationManager;

    @FXML
    private BorderPane mainBorderPane;

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    @FXML
    public void initialize() {
        // Rien de spécial ici
    }

    @FXML
    public void handleMenuEmployes() {
        ouvrirNouvelleFenetre("/com/monapp/employe-view.fxml", "Gestion des Employés");
    }

    @FXML
    public void handleMenuProjets() {
        ouvrirNouvelleFenetre("/com/monapp/projet-view.fxml", "Gestion des Projets");
    }

    @FXML
    public void handleMenuTaches() {
        ouvrirNouvelleFenetre("/com/monapp/tache-view.fxml", "Gestion des Tâches");
    }

    /**
     * Ouvre une nouvelle fenêtre (Stage) avec le FXML indiqué.
     * Injecte l'applicationManager dans le contrôleur avant d'afficher la fenêtre.
     */
    private void ouvrirNouvelleFenetre(String cheminFXML, String titreFenetre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            AnchorPane root = loader.load();

            // Récupérer le contrôleur
            Object controller = loader.getController();
            // Injecter l'applicationManager + rafraîchir la table
            if (controller instanceof EmployeController) {
                ((EmployeController) controller).setApplicationManager(this.applicationManager);
            } else if (controller instanceof ProjetController) {
                ((ProjetController) controller).setApplicationManager(this.applicationManager);
            } else if (controller instanceof TacheController) {
                ((TacheController) controller).setApplicationManager(this.applicationManager);
            }

            // Créer la fenêtre
            Stage fenetre = new Stage();
            fenetre.setTitle(titreFenetre);
            fenetre.initModality(Modality.WINDOW_MODAL);
            fenetre.initOwner(mainBorderPane.getScene().getWindow());

            Scene scene = new Scene(root);
            fenetre.setScene(scene);
            fenetre.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
