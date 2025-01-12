package com.monapp.controller;

import com.monapp.dao.TacheDAO;
import javafx.stage.Stage;
import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import com.monapp.model.Tache;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private TacheDAO tacheDAO;

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
        rafraichirTableTaches();
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
        // Vérifier que le projet est bien défini
        if (projet == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Aucun projet sélectionné !");
            alert.showAndWait();
            return;
        }

        // Récupérer les tâches sélectionnées
        var selectedTaches = tableTaches.getSelectionModel().getSelectedItems();

        if (selectedTaches == null || selectedTaches.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune tâche sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner au moins une tâche à ajouter au projet.");
            alert.showAndWait();
            return;
        }

        // Ajouter les tâches sélectionnées au projet
        for (Tache tache : selectedTaches) {
            tache.setProjetId(projet.getId()); // Associer l'ID du projet à la tâche
            applicationManager.ajouterTacheAuProjet(tache.getId(), projet.getId()); // Mettez à jour la tâche dans l'application
        }

        // Rafraîchir la table des tâches disponibles
        rafraichirTableTaches();

        // Notifier l'écouteur principal
        if (onTaskAddedListener != null) {
            onTaskAddedListener.run();
        }

        // Afficher un message de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
        confirmationAlert.setTitle("Succès");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Les tâches sélectionnées ont été ajoutées au projet.");
        confirmationAlert.showAndWait();
    }


    private void rafraichirTableTaches() {
        if (applicationManager != null && tableTaches != null) {
            List<Tache> tachesNonAssignees = applicationManager.getListeTaches().stream()
                    .filter(tache -> tache.getProjetId() == null || tache.getProjetId() == 0) // Filtre
                    .collect(Collectors.toList());

            tableTaches.getItems().setAll(tachesNonAssignees);
            tableTaches.refresh();
        } else {
            System.err.println("Erreur : applicationManager ou tableTaches est null.");
        }
    }



    @FXML
    public void fermerFenetre() {
        // Récupérer la fenêtre actuelle et la fermer
        Stage stage = (Stage) tableTaches.getScene().getWindow();
        stage.close();
    }
}
