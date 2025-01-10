package com.monapp.controller;

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
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDates.setCellValueFactory(cellData -> {
            Projet p = cellData.getValue();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String debut = (p.getDateDebut() != null) ? p.getDateDebut().format(fmt) : "??";
            String fin   = (p.getDateFin()   != null) ? p.getDateFin().format(fmt)   : "??";
            return new javafx.beans.property.SimpleStringProperty(debut + " -> " + fin);
        });
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
        Projet selected = tableProjets.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/taches-projet-view.fxml"));
            AnchorPane root = loader.load();

            TachesProjetController controller = loader.getController();
            controller.setApplicationManager(this.applicationManager);
            controller.setProjet(selected);

            Stage stage = new Stage();
            stage.setTitle("Composer Tâches pour " + selected.getNom());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root, 500, 400));
            stage.showAndWait();

            rafraichirTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rafraichirTable() {
        tableProjets.getItems().setAll(applicationManager.getListeProjets());
        tableProjets.refresh();
    }
}
