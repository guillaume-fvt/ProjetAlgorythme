package com.monapp.controller;

import com.monapp.model.StatutTache;
import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.FileWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ProjetController {

    private ApplicationManager applicationManager;

    @FXML
    private TableView<Projet> tableProjets;
    @FXML
    private TableColumn<Projet, String> colNom;
    @FXML
    private TableColumn<Projet, String> colDates;  // On va construire une property "dateDebut - dateFin"
    @FXML
    private TextField tfNomProjet;
    @FXML
    private DatePicker dpDateDebut, dpDateFin;

    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
        rafraichirTable();
    }

    @FXML
    public void initialize() {
        // Configurer les colonnes de la TableView
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDates.setCellValueFactory(cellData -> {
            Projet p = cellData.getValue();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String debut = (p.getDateDebut() != null) ? p.getDateDebut().format(fmt) : "??";
            String fin = (p.getDateFin() != null) ? p.getDateFin().format(fmt) : "??";
            return new javafx.beans.property.SimpleStringProperty(debut + " -> " + fin);
        });

        verifierNotifications();

        // Listener pour mettre à jour les champs
        tableProjets.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                afficherProjet(newSelection);
            }
            else
                viderChamps();
        });

        // Ajouter un listener pour gérer les clics répétés
        tableProjets.setOnMouseClicked(event -> {
            Projet selected = tableProjets.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherProjet(selected);
            }
            else
                viderChamps();
        });
    }

    private void viderChamps() {
        tfNomProjet.clear();
        dpDateDebut.setValue(null);
        dpDateFin.setValue(null);
    }

    private void afficherProjet(Projet projet) {
        tfNomProjet.setText(projet.getNom());
        dpDateDebut.setValue(projet.getDateDebut());
        dpDateFin.setValue(projet.getDateFin());
    }
    @FXML
    public void ajouterProjet() {
        Projet p = new Projet(
                applicationManager.getListeProjets().size() + 1,
                tfNomProjet.getText(),
                dpDateDebut.getValue(),
                dpDateFin.getValue()
        );
        applicationManager.ajouterProjet(p);
        rafraichirTable();
        viderChamps();
    }

    @FXML
    public void modifierProjet() {
        Projet selected = tableProjets.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setNom(tfNomProjet.getText());
            selected.setDateDebut(dpDateDebut.getValue());
            selected.setDateFin(dpDateFin.getValue());
            applicationManager.modifierProjet(selected);
            rafraichirTable();
            viderChamps();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun projet sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un projet dans la table pour le modifier.");
            alert.showAndWait();
        }
    }

    @FXML
    public void supprimerProjet() {
        Projet selected = tableProjets.getSelectionModel().getSelectedItem();
        if (selected != null) {
            applicationManager.supprimerProjet(selected.getId());
            rafraichirTable();
            viderChamps();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun projet sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un projet dans la table pour le supprimer.");
            alert.showAndWait();
        }
    }

    /**
     * Ouvre un pop-up pour choisir les membres à ajouter au projet.
     */
    @FXML
    public void placerMembres() {
        Projet selected = tableProjets.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/membres-projet-view.fxml"));
            AnchorPane root = loader.load();

            MembresProjetController controller = loader.getController();
            controller.setApplicationManager(this.applicationManager);
            controller.setProjet(selected); // le projet sur lequel on ajoute des membres

            Stage stage = new Stage();
            stage.setTitle("Placer Membres sur " + selected.getNom());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Après fermeture, on refresh la table
            rafraichirTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ouvre un pop-up pour associer des Tâches au projet (ajouter/supprimer).
     */
    @FXML
    public void composerTaches() {
        // Vérifier qu'un projet est sélectionné
        Projet selectedProjet = tableProjets.getSelectionModel().getSelectedItem();
        if (selectedProjet == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun projet sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un projet dans la table avant de composer des tâches.");
            alert.showAndWait();
            return;
        }

        try {
            // Charger la vue des tâches
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/taches-selection-view.fxml"));
            AnchorPane root = loader.load();

            // Récupérer le contrôleur associé
            TachesSelectionController controller = loader.getController();
            controller.setApplicationManager(this.applicationManager);
            controller.setProjet(selectedProjet);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Associer des Tâches à " + selectedProjet.getNom());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root, 600, 400));

            // Ajouter un listener pour détecter lorsqu'une tâche est ajoutée
            controller.setOnTaskAddedListener(() -> {
                Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmationAlert.setTitle("Tâche ajoutée");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("Une tâche a été ajoutée avec succès au projet \"" + selectedProjet.getNom() + "\".");
                confirmationAlert.showAndWait();

                // Rafraîchir la table des projets
                rafraichirTable();
            });

            // Afficher la fenêtre (ne pas fermer automatiquement après ajout)
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void rafraichirTable() {
        tableProjets.getItems().setAll(applicationManager.getListeProjets());
        tableProjets.refresh();
    }

    @FXML
    public void genererRapportProjetsCSV() {
        // Utiliser FileChooser pour choisir où enregistrer le fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport des projets");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        java.io.File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Écrire l'en-tête du fichier CSV
                writer.append("Nom du Projet,Date Début,Date Fin,Taux d'Avancement (%)\n");

                // Parcourir les projets affichés dans le tableau
                for (Projet projet : tableProjets.getItems()) {
                    // Calculer le taux d'avancement
                    int totalTaches = projet.getListeTaches().size();
                    long tachesTerminees = projet.getListeTaches().stream()
                            .filter(tache -> tache.getStatut() == StatutTache.TERMINE)
                            .count();
                    double tauxAvancement = (totalTaches > 0) ? ((double) tachesTerminees / totalTaches) * 100 : 0;

                    // Ajouter les données du projet dans le fichier CSV
                    writer.append(String.format(
                            "%s,%s,%s,%.2f\n",
                            projet.getNom(),
                            projet.getDateDebut() != null ? projet.getDateDebut().toString() : "??",
                            projet.getDateFin() != null ? projet.getDateFin().toString() : "??",
                            tauxAvancement
                    ));
                }

                // Afficher une notification de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rapport généré");
                alert.setHeaderText(null);
                alert.setContentText("Le rapport des projets a été enregistré avec succès !");
                alert.showAndWait();

            } catch (IOException e) {
                // Afficher une notification d'erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors de l'enregistrement du rapport.");
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }
    @FXML
    public void verifierNotifications() {
        StringBuilder message = new StringBuilder();

        for (Projet projet : tableProjets.getItems()) {
            // Calculer les tâches terminées et le total
            long tachesTerminees = projet.getListeTaches().stream()
                    .filter(tache -> tache.getStatut() == StatutTache.TERMINE)
                    .count();
            long totalTaches = projet.getListeTaches().size();
            double tauxAvancement = (totalTaches > 0) ? ((double) tachesTerminees / totalTaches) * 100 : 0;

            // 1. Tâches retardées
            long tachesRetardees = projet.getListeTaches().stream()
                    .filter(tache -> tache.getDateLimite() != null &&
                            tache.getDateLimite().isBefore(java.time.LocalDate.now()) &&
                            tache.getStatut() != StatutTache.TERMINE)
                    .count();
            if (tachesRetardees > 0) {
                message.append("⚠ Projet \"").append(projet.getNom())
                        .append("\" a ").append(tachesRetardees)
                        .append(" tâche(s) en retard.\n");
            }

            // 2. Projet approchant de son échéance
            if (projet.getDateFin() != null &&
                    projet.getDateFin().isBefore(java.time.LocalDate.now().plusDays(7)) &&
                    projet.getDateFin().isAfter(java.time.LocalDate.now())) {
                message.append("⏳ Projet \"").append(projet.getNom())
                        .append("\" approche de son échéance (Date fin : ")
                        .append(projet.getDateFin()).append(").\n");
            }

            // 3. Projet terminé
            if (tachesTerminees == totalTaches && totalTaches > 0) {
                message.append("✅ Projet \"").append(projet.getNom())
                        .append("\" est terminé ! Félicitations !\n");
                continue; // Pas besoin de vérifier les paliers pour un projet terminé
            }

            // 4. Avancement par paliers
            int palierActuel = (int) (tauxAvancement / 25) * 25;
            int palierPrecedent = projet.getPalierPrecedent(); // Variable stockée dans Projet
            if (palierActuel > palierPrecedent) {
                message.append("📈 Projet \"").append(projet.getNom())
                        .append("\" a atteint ").append(palierActuel).append("% d'avancement.\n");
                projet.setPalierPrecedent(palierActuel); // Met à jour le palier atteint
            }
        }

        // Afficher une alerte si des notifications existent
        if (message.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notifications sur les Projets");
            alert.setHeaderText("Détails des Projets");
            alert.setContentText(message.toString());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aucune Notification");
            alert.setHeaderText(null);
            alert.setContentText("Aucune notification pour les projets.");
            alert.showAndWait();
        }
    }

}