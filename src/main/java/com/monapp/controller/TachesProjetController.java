package com.monapp.controller;

import com.monapp.dao.EmployeDAO;
import com.monapp.dao.TacheDAO;
import com.monapp.model.Employe;
import javafx.stage.Stage;
import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import com.monapp.model.Tache;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

public class TachesProjetController {

    private ApplicationManager applicationManager;
    private Runnable onTaskAddedListener;
    private Projet projet;

    @FXML
    private  TableView<Tache> tableTaches;
    @FXML
    private  TableView<Tache> tableTachesASupprimer;
    @FXML
    private TableColumn<Tache, String> colTitre;
    @FXML
    private TableColumn<Tache, String> colStatut;

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
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
        tableTachesASupprimer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
        confirmationAlert.setContentText("La tâche sélectionnée a été ajoutée au projet.");
        confirmationAlert.showAndWait();
    }

    @FXML
    public void supprimerTachesAuProjet() {
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
        var selectedTaches = tableTachesASupprimer.getSelectionModel().getSelectedItems();

        if (selectedTaches == null || selectedTaches.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune tâche sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner au moins une tâche à ajouter au projet.");
            alert.showAndWait();
            return;
        }

        // Dissocier les tâches sélectionnées au projet
        for (Tache tache : selectedTaches) {
            applicationManager.supprimerTacheAuProjet(tache.getId(), projet.getId()); // Mettez à jour la tâche dans l'application
        }

        // Rafraîchir la table des tâches disponibles
        rafraichirTachesLiees();

        // Notifier l'écouteur principal
        if (onTaskAddedListener != null) {
            onTaskAddedListener.run();
        }

        // Afficher un message de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
        confirmationAlert.setTitle("Succès");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("La tâche sélectionnée a été dissocié au projet.");
        confirmationAlert.showAndWait();
    }

    private void rafraichirTableTaches() {
        if (applicationManager != null && tableTaches != null) {

            TacheDAO tacheDAO = new TacheDAO();
            List<Tache> toutesLesTaches = tacheDAO.getToutesLesTaches();
            List<Tache> tachesNonAssignees = toutesLesTaches.stream()
                    .filter(tache -> tache.getProjetId() == null || tache.getProjetId() == 0) // Filtre
                    .collect(Collectors.toList());

            tableTaches.getItems().setAll(tachesNonAssignees);
            tableTaches.refresh();
        } else {
            System.err.println("Erreur : applicationManager ou tableTaches est null.");
        }
    }

    // Méthode pour rafraîchir uniquement la table des tâches liées au projet
    public void rafraichirTachesLiees() {
        if (applicationManager != null && tableTaches != null){

        TacheDAO tacheDAO = new TacheDAO();
        List<Tache> toutesLesTaches = tacheDAO.getToutesLesTaches(); // Charger toutes les tâches depuis la base

        // Filtrer les tâches liées au projet actuel
        List<Tache> tachesLiees = toutesLesTaches.stream()
                .filter(tache -> tache.getProjetId()==projet.getId())
                .collect(Collectors.toList());

        // Mettre à jour la table des tâches liées
        tableTachesASupprimer.getItems().setAll(tachesLiees);

        // Rafraîchir la vue pour refléter les nouvelles données
        tableTachesASupprimer.refresh();
        }
        else {
                System.err.println("Erreur : applicationManager ou tableTachesASupprimer est null.");
            }
    }

    // Charge les taches disponibles (n'appartenant pas à un projet)
    public void chargerTacheDisponibles() {
        if (projet == null) {
            System.err.println("Erreur : Aucun projet n'est défini.");
            return;
        }
        TacheDAO tacheDAO = new TacheDAO();
        List<Tache> taches = tacheDAO.getToutesLesTaches(); // Charger tous les employés depuis la base

        // Filtrer les employés qui ne sont pas déjà membres du projet
        List<Tache> TachesDisponibles = taches.stream()
                .filter(tache -> tache.getProjetId() == null || tache.getProjetId() == 0) // Filtre
                .collect(Collectors.toList());

        // Mettre à jour la ListView avec les employés disponibles
        tableTaches.getItems().setAll(TachesDisponibles);
    }

    // Charge les tâches liées à un projet spécifique
    public void chargerTachesLieesAuProjet() {
        if (projet == null) {
            System.err.println("Erreur : Aucun projet n'est défini.");
            return;
        }

        TacheDAO tacheDAO = new TacheDAO();
        List<Tache> taches = tacheDAO.getToutesLesTaches(); // Charger toutes les tâches depuis la base

        // Filtrer les tâches liées au projet
        List<Tache> tachesLiees = taches.stream()
                .filter(tache -> projet.getId() == tache.getProjetId()) // Vérifier si l'ID du projet correspond
                .collect(Collectors.toList());

        // Mettre à jour la TableView avec les tâches liées
        tableTachesASupprimer.getItems().setAll(tachesLiees);
    }


    @FXML
    public void fermerFenetre() {
        // Récupérer la fenêtre actuelle et la fermer
        Stage stage = (Stage) tableTaches.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void fermerFenetreTacheASupprimer() {
        // Récupérer la fenêtre actuelle et la fermer
        Stage stage = (Stage) tableTachesASupprimer.getScene().getWindow();
        stage.close();
    }
}
