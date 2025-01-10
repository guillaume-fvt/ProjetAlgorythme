package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjetController {

    private ApplicationManager applicationManager;

    @FXML
    private TableView<Projet> tableProjets;
    @FXML
    private TableColumn<Projet, String> colNomProjet; // si besoin
    // ... tu peux ajouter colDateDebut, colDateFin ?

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
        // On pourrait lier des colonnes : colNomProjet.setCellValueFactory(...)
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

    @FXML
    public void composerTaches() {
        Projet selected = tableProjets.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Ouvre un Alert ou un second FXML
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Composer Tâches");
        alert.setHeaderText("Tâches du projet : " + selected.getNom());
        StringBuilder sb = new StringBuilder();
        sb.append("Tâches existantes dans ce projet : \n");
        selected.getListeTaches().forEach(t ->
                sb.append(" - ").append(t.getTitre()).append(" (").append(t.getStatut()).append(")\n")
        );
        sb.append("\n(À implémenter : Ajouter / Retirer des Tâches)");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    @FXML
    public void placerMembres() {
        Projet selected = tableProjets.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/membres-projet-view.fxml"));
            AnchorPane root = loader.load();

            MembresProjetController controller = loader.getController();
            controller.setApplicationManager(this.applicationManager);
            controller.setProjet(selected); // pour savoir sur quel projet ajouter

            Stage stage = new Stage();
            stage.setTitle("Placer Membres sur le projet : " + selected.getNom());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Après fermeture, on rafraîchit la table
            rafraichirTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rafraichirTable() {
        tableProjets.getItems().setAll(applicationManager.getListeProjets());
    }
}
