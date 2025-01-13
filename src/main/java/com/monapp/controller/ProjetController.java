package com.monapp.controller;

import com.monapp.dao.ProjetDAO;
import com.monapp.dao.TacheDAO;
import com.monapp.model.StatutTache;
import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import com.monapp.model.Tache;
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
import java.util.List;
import java.util.stream.Collectors;

public class ProjetController {

    private ApplicationManager applicationManager;
    private final ProjetDAO projetDAO = new ProjetDAO();

    @FXML
    private TableView<Tache> tableTaches;
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
        projetDAO.ajouterProjet(p);
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
            projetDAO.modifierProjet(selected);
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
            projetDAO.supprimerProjet(selected.getId());
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
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun projet sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un projet dans la table avant d'ajouter des membres.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/membres-projet-view.fxml"));
            AnchorPane root = loader.load();

            // Récupérer le contrôleur associé à la vue
            MembresProjetController controller = loader.getController();
            controller.setApplicationManager(this.applicationManager);
            controller.setProjet(selected);

            // Charger la liste des employés depuis la base
            controller.chargerEmployesDisponibles();

            // Afficher la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Placer Membres sur " + selected.getNom());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Rafraîchir la table après fermeture de la fenêtre
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
        Projet selectedProjet = tableProjets.getSelectionModel().getSelectedItem();
        if (selectedProjet == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun projet sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un projet.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/taches-selection-view.fxml"));
            AnchorPane root = loader.load();

            TachesSelectionController controller = loader.getController();
            controller.setApplicationManager(this.applicationManager);
            controller.setProjet(selectedProjet);

            controller.setOnTaskAddedListener(() -> {
                // Rafraîchir les tâches après ajout
                rafraichirTableTaches();
            });

            Stage stage = new Stage();
            stage.setTitle("Associer des Tâches");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rafraichirTable() {
        tableProjets.getItems().setAll(projetDAO.getTousLesProjets());
        tableProjets.refresh();
    }

    private void rafraichirTableTaches() {
        if (tableTaches == null) {
            System.err.println("Erreur : tableTaches est null.");
            return;
        }
        if (applicationManager == null) {
            System.err.println("Erreur : applicationManager est null.");
            return;
        }

        // Récupérer toutes les tâches depuis le gestionnaire d'application
        List<Tache> toutesLesTaches = applicationManager.getListeTaches();

        // Filtrer les tâches qui ne sont pas assignées à un projet
        List<Tache> tachesNonAssignees = toutesLesTaches.stream()
                .filter(tache -> tache.getProjetId() == 0 || tache.getProjetId() == null)
                .collect(Collectors.toList());

        // Mettre à jour la table avec les tâches filtrées
        tableTaches.getItems().setAll(tachesNonAssignees);
        tableTaches.refresh();
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

        // Charger tous les projets depuis la base de données
        ProjetDAO projetDAO = new ProjetDAO();
        List<Projet> projets = projetDAO.getTousLesProjets();

        for (Projet projet : projets) {
            // Charger les tâches associées au projet
            List<Tache> taches = projetDAO.getTachesByProjetId(projet.getId());
            projet.setListeTaches(taches);

            // Calculer les tâches terminées et le total
            long tachesTerminees = taches.stream()
                    .filter(tache -> tache.getStatut() == StatutTache.TERMINE)
                    .count();
            long totalTaches = taches.size();
            double tauxAvancement = (totalTaches > 0) ? ((double) tachesTerminees / totalTaches) * 100 : 0;

            // 1. Tâches retardées
            long tachesRetardees = taches.stream()
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
                    projet.getDateFin().isBefore(java.time.LocalDate.now().plusDays(4)) &&
                    projet.getDateFin().isAfter(java.time.LocalDate.now())&& tachesTerminees!=totalTaches) {
                message.append("⏳ Projet \"").append(projet.getNom())
                        .append("\" approche de son échéance (Date fin : ")
                        .append(projet.getDateFin()).append(").\n");
            }

            // 3. Projet terminé
            if (totalTaches > 0 && tachesTerminees == totalTaches) {
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

                // Mettre à jour le palier dans la base de données
                boolean success = projetDAO.mettreAJourPalier(projet.getId(), palierActuel);
                if (!success) {
                    System.err.println("Erreur lors de la mise à jour du palier pour le projet ID " + projet.getId());
                }
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