package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Employe;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class EmployeController {

    private ApplicationManager applicationManager;

    @FXML
    private TableView<Employe> tableEmployes;
    @FXML
    private TableColumn<Employe, String> colNom;
    @FXML
    private TableColumn<Employe, String> colPrenom;
    @FXML
    private TableColumn<Employe, String> colRole;

    @FXML
    private TextField tfNom, tfPrenom, tfRole;

    // Boutons supplémentaires (si tu veux des ID pour eux)
    @FXML
    private Button btnAfficherInfos, btnAfficherHistorique;

    @FXML
    public void initialize() {
        // Ne plus rafraichir ici
        // On se contente de lier les colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    /**
     * Appelé après la création du contrôleur,
     * juste après le loader.getController().
     */
    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
        rafraichirTable();
    }

    @FXML
    public void ajouterEmploye() {
        Employe e = new Employe(
                applicationManager.getListeEmployes().size() + 1,
                tfNom.getText(),
                tfPrenom.getText(),
                tfRole.getText()
        );
        applicationManager.ajouterEmploye(e);
        rafraichirTable();
    }

    @FXML
    public void modifierEmploye() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setNom(tfNom.getText());
            selected.setPrenom(tfPrenom.getText());
            selected.setRole(tfRole.getText());
            applicationManager.modifierEmploye(selected);
            rafraichirTable();
        }
    }

    @FXML
    public void supprimerEmploye() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            applicationManager.supprimerEmploye(selected.getId());
            rafraichirTable();
        }
    }

    /**
     * Afficher infos d'un employé (ID, Nom, Prénom, Rôle)
     */
    @FXML
    public void afficherInfosEmploye() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informations Employé");
            alert.setHeaderText("Détails de l'employé");
            alert.setContentText(
                    "ID : " + selected.getId() +
                            "\nNom : " + selected.getNom() +
                            "\nPrénom : " + selected.getPrenom() +
                            "\nRôle : " + selected.getRole()
            );
            alert.showAndWait();
        }
    }

    /**
     * Afficher l'historique d'un employé (projets déjà réalisés)
     */
    @FXML
    public void afficherHistoriqueEmploye() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // ICI : Selon ta logique, si tu as des projets passés reliés
            // ou un attribut "historiqueProjets" dans Employe, etc.
            // Ex : List<Projet> histo = applicationManager.getProjetsByEmploye(selected)
            //  -> On simule :
            String historique = "Historique de " + selected.getNom() + " :\n";
            historique += "(à implémenter : liste des projets passés)\n";

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Historique Employé");
            alert.setHeaderText("Projets passés de " + selected.getNom());
            alert.setContentText(historique);
            alert.showAndWait();
        }
    }

    private void rafraichirTable() {
        tableEmployes.getItems().setAll(applicationManager.getListeEmployes());
    }
}
