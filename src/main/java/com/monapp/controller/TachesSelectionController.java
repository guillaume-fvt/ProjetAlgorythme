package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import com.monapp.model.Tache;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class TachesSelectionController {

    private ApplicationManager applicationManager;
    private Projet projet;

    @FXML
    private TableView<Tache> tableTaches;
    @FXML
    private TableColumn<Tache, String> colTitre;
    @FXML
    private TableColumn<Tache, String> colStatut;

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
        rafraichirTable();
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    @FXML
    public void initialize() {
        // Initialiser les colonnes
        colTitre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitre()));
        colStatut.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatut().name()));
    }

    @FXML
    public void ajouterTachesAuProjet() {
        // Récupérer les tâches sélectionnées
        var selectedTaches = tableTaches.getSelectionModel().getSelectedItems();

        if (selectedTaches.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune tâche sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner au moins une tâche à ajouter au projet.");
            alert.showAndWait();
            return;
        }

        // Ajouter les tâches sélectionnées au projet
        selectedTaches.forEach(projet::ajouterTache);

        // Fermer la fenêtre après mise à jour
        Stage stage = (Stage) tableTaches.getScene().getWindow();
        stage.close();
    }

    private void rafraichirTable() {
        tableTaches.getItems().setAll(applicationManager.getListeTaches());
        tableTaches.refresh();
    }
}
