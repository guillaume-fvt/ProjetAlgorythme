package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProjetController {

    private ApplicationManager applicationManager;

    @FXML
    private TableView<Projet> tableProjets;
    @FXML
    private TextField tfNomProjet;
    @FXML
    private DatePicker dpDateDebut, dpDateFin;

    @FXML
    private Button btnComposerTaches, btnPlacerMembres;

    // Au lieu d'initialize() pour refresh, on y met seulement la config de colonnes
    @FXML
    public void initialize() {
        // si tu as des TableColumn<Projet, ...> tu peux les lier ici
    }

    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
        rafraichirTable();
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
        }
    }

    @FXML
    public void supprimerProjet() {
        Projet selected = tableProjets.getSelectionModel().getSelectedItem();
        if (selected != null) {
            applicationManager.supprimerProjet(selected.getId());
            rafraichirTable();
        }
    }

    /**
     * Composer un projet en un ensemble de tâches (ex: ouvre un pop-up,
     * ou liste des Taches qu'on peut associer)
     */
    @FXML
    public void composerTaches() {
        Projet selected = tableProjets.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Composer un Projet");
            alert.setHeaderText("Tâches du projet : " + selected.getNom());
            alert.setContentText("Ici, tu pourrais afficher/ajouter/supprimer des Taches " +
                    "dans selected.getListeTaches().");
            alert.showAndWait();
        }
    }

    /**
     * Placer des membres depuis la liste du personnel
     */
    @FXML
    public void placerMembres() {
        Projet selected = tableProjets.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Par exemple, lister tous les Employe de applicationManager.getListeEmployes()
            // Choisir ceux à ajouter dans selected.ajouterEmploye(e)
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Placer des Membres");
            alert.setHeaderText("Membres du projet : " + selected.getNom());
            alert.setContentText("Ici, tu pourrais afficher la liste des employés " +
                    "et attribuer un 'rôle' au sein du projet.");
            alert.showAndWait();
        }
    }

    private void rafraichirTable() {
        tableProjets.getItems().setAll(applicationManager.getListeProjets());
    }
}
