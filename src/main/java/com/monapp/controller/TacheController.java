package com.monapp.controller;

import com.monapp.dao.TacheDAO;
import com.monapp.model.ApplicationManager;
import com.monapp.model.StatutTache;
import com.monapp.model.Tache;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TacheController {

    private ApplicationManager applicationManager;
    private final TacheDAO tacheDAO = new TacheDAO(); // Instance du DAO pour gérer les tâches

    @FXML
    private TableView<Tache> tableTaches;
    @FXML
    private TableColumn<Tache, String> colTitre;
    @FXML
    private TableColumn<Tache, String> colStatut;
    @FXML
    private TableColumn<Tache, String> colDateLimite;
    @FXML
    private TableColumn<Tache, String> colPrioritaire;

    @FXML
    private TextField tfTitre;
    @FXML
    private TextArea taDescription;
    @FXML
    private ComboBox<StatutTache> cbStatut;
    @FXML
    private DatePicker dpDateLimite;
    @FXML
    private CheckBox cbPrioritaire;

    @FXML
    public void initialize() {
        // Configurer les colonnes
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut().name()));
        colDateLimite.setCellValueFactory(c -> {
            Tache t = c.getValue();
            if (t.getDateLimite() == null) return new SimpleStringProperty("");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return new SimpleStringProperty(t.getDateLimite().format(fmt));
        });
        colPrioritaire.setCellValueFactory(c -> {
            Tache t = c.getValue();
            String prior = (t.getPriorite() > 0) ? "Oui" : "Non";
            return new SimpleStringProperty(prior);
        });

        // Listener pour afficher les détails d'une tâche sélectionnée
        tableTaches.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                afficherTache(newSelection);
            }
        });

        // Listener pour les clic multiples
        tableTaches.setOnMouseClicked(event -> {
            Tache selected = tableTaches.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherTache(selected); // Remet à jour les champs à chaque clic
            }
        });
    }

    private void viderChamps() {
        tfTitre.clear(); // Vider le champ du titre
        taDescription.clear(); // Vider le champ de description
        cbStatut.setValue(null); // Réinitialiser le ComboBox
        dpDateLimite.setValue(null); // Réinitialiser le DatePicker
        cbPrioritaire.setSelected(false); // Décocher la case à cocher
    }

    private void afficherTache(Tache tache) {
        tfTitre.setText(tache.getTitre());
        taDescription.setText(tache.getDescription());
        cbStatut.setValue(tache.getStatut());
        dpDateLimite.setValue(tache.getDateLimite());
        cbPrioritaire.setSelected(tache.getPriorite() > 0);
    }

    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
        cbStatut.getItems().setAll(StatutTache.values());
        rafraichirTable();
    }

    @FXML
    public void ajouterTache() {
        int priorite = cbPrioritaire.isSelected() ? 1 : 0;
        Tache t = new Tache(
                applicationManager.getListeTaches().size() + 1,
                tfTitre.getText(),
                taDescription.getText(),
                cbStatut.getValue(),
                priorite,
                dpDateLimite.getValue(),
                null,
                null
        );
        applicationManager.ajouterTache(t);
        tacheDAO.ajouterTache(t); // Utilise le DAO pour ajouter dans la base
        rafraichirTable();
        viderChamps();
    }

    @FXML
    public void modifierTache() {
        Tache selected = tableTaches.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setTitre(tfTitre.getText());
            selected.setDescription(taDescription.getText());
            selected.setStatut(cbStatut.getValue());
            selected.setPriorite(cbPrioritaire.isSelected() ? 1 : 0);
            selected.setDateLimite(dpDateLimite.getValue());
            applicationManager.modifierTache(selected);
            tacheDAO.modifierTache(selected); // Utilise le DAO pour mettre à jour dans la base
            rafraichirTable();
            viderChamps();
        }
    }

    @FXML
    public void supprimerTache() {
        Tache selected = tableTaches.getSelectionModel().getSelectedItem();
        if (selected != null) {
            applicationManager.supprimerTache(selected.getId());
            tacheDAO.supprimerTache(selected.getId()); // Utilise le DAO pour supprimer dans la base
            rafraichirTable();
            viderChamps();
        }
    }

    @FXML
    public void ouvrirKanban() {
        KanbanController.ouvrirKanbanScene(applicationManager, tableTaches.getScene().getWindow());
    }

    @FXML
    public void ouvrirCalendrier() {
        // Ouvre la vue calendrier
        CalendarController.ouvrirCalendarScene(applicationManager, tableTaches.getScene().getWindow());
    }

    private void rafraichirTable() {
        tableTaches.getItems().setAll(tacheDAO.getToutesLesTaches()); // Charger les tâches depuis la base
        tableTaches.refresh();
    }

    @FXML
    public void genererRetardsCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport des retards");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        java.io.File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Écrire l'en-tête du fichier CSV
                writer.append("Titre,Description,Statut,Date Limite,Prioritaire,Projet_id,Employé,Retard (heures)\n");

                // Récupérer les tâches depuis la base de données
                List<Tache> taches = tacheDAO.getToutesLesTaches();

                // Parcourir toutes les tâches récupérées
                for (Tache tache : taches) {
                    // Vérifier si la tâche est en retard
                    if (tache.getDateLimite() != null &&
                            tache.getDateLimite().isBefore(java.time.LocalDate.now()) &&
                            tache.getStatut() != StatutTache.TERMINE) {

                        // Calculer le retard en heures
                        long retardHeures = java.time.Duration.between(
                                tache.getDateLimite().atStartOfDay(),
                                java.time.LocalDateTime.now()
                        ).toHours();

                        // Ajouter les détails de la tâche dans le fichier CSV
                        String prioritaire = (tache.getPriorite() > 0) ? "Oui" : "Non";
                        String projetId = (tache.getProjetId() != null) ? tache.getProjetId().toString() : "N/A";
                        String employeNom = (tache.getEmployeAssigne() != null) ? tache.getEmployeAssigne().getNom() : "Non assigné";

                        writer.append(String.format(
                                "%s,%s,%s,%s,%s,%s,%s,%d\n",
                                tache.getTitre(),
                                tache.getDescription(),
                                tache.getStatut().name(),
                                tache.getDateLimite().toString(),
                                prioritaire,
                                projetId,
                                employeNom,
                                retardHeures
                        ));
                    }
                }

                // Afficher une notification de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rapport généré");
                alert.setHeaderText(null);
                alert.setContentText("Le rapport des retards a été enregistré avec succès !");
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


}
