package com.monapp.controller;

import javafx.stage.Stage;
import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import com.monapp.model.Tache;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class TachesSelectionController {

    private ApplicationManager applicationManager;
    private Runnable onTaskAddedListener;
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

    public void setOnTaskAddedListener(Runnable listener) {
        this.onTaskAddedListener = listener;
    }

    @FXML
    public void initialize() {
        // Initialiser les colonnes
        colTitre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitre()));
        colStatut.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatut().name()));

        // Permettre la sélection multiple dans la table
        tableTaches.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    public void ajouterTachesAuProjet() {
        // Récupérer les tâches sélectionnées
        var selectedTaches = tableTaches.getSelectionModel().getSelectedItems();

        if (selectedTaches.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aucune tâche sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner au moins une tâche à ajouter au projet.");
            alert.showAndWait();
            return;
        }

        // Ajouter les tâches sélectionnées au projet
        selectedTaches.forEach(projet::ajouterTache);

        // Appeler le listener pour notifier le contrôleur principal
        if (onTaskAddedListener != null) {
            onTaskAddedListener.run();
        }

        // Rafraîchir la table après ajout
        rafraichirTable();
    }

    private void rafraichirTable() {
        if (applicationManager != null) {
            tableTaches.getItems().setAll(applicationManager.getListeTaches());
            tableTaches.refresh();
        } else {
            System.err.println("ApplicationManager est null. Impossible de charger les tâches.");
        }
    }

    @FXML
    public void fermerFenetre() {
        // Récupérer la fenêtre actuelle et la fermer
        Stage stage = (Stage) tableTaches.getScene().getWindow();
        stage.close();
    }
}
