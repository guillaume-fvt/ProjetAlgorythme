package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Employe;
import com.monapp.model.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

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

    @FXML
    public void initialize() {
        // Configurer les colonnes de la TableView
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Ajouter un listener pour détecter la sélection dans la table
        tableEmployes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                afficherEmploye(newSelection);
            }
        });

        // Ajouter un listener pour les clics répétés
        tableEmployes.setOnMouseClicked(event -> {
            Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherEmploye(selected); // Remet à jour les champs à chaque clic
            }
        });
    }


    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
        rafraichirTable();
    }

    private void viderChamps() {
        tfNom.clear();      // Vider le champ du nom
        tfPrenom.clear();   // Vider le champ du prénom
        tfRole.clear();     // Vider le champ du rôle
    }

    private void afficherEmploye(Employe employe) {
        tfNom.setText(employe.getNom());
        tfPrenom.setText(employe.getPrenom());
        tfRole.setText(employe.getRole());
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
        viderChamps();
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
            viderChamps();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun employé sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé dans la table pour le modifier.");
            alert.showAndWait();
        }
    }

    @FXML
    public void supprimerEmploye() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            applicationManager.supprimerEmploye(selected.getId());
            rafraichirTable();
            viderChamps();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun employé sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé dans la table pour le supprimer.");
            alert.showAndWait();
        }
    }

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

    @FXML
    public void afficherHistoriqueEmploye() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Récupérer tous les projets auxquels participe cet employé
            List<Projet> projetsEmploye = applicationManager.getProjetsByEmployee(selected);
            StringBuilder sb = new StringBuilder();
            sb.append("Projets de ").append(selected.getNom()).append(" :\n");
            for (Projet p : projetsEmploye) {
                sb.append("- ").append(p.getNom()).append(" (ID=").append(p.getId()).append(")\n");
            }
            if (projetsEmploye.isEmpty()) {
                sb.append("Aucun projet trouvé.\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Historique Projets");
            alert.setHeaderText("Projets de " + selected.getNom());
            alert.setContentText(sb.toString());
            alert.showAndWait();
        }
    }

    private void rafraichirTable() {
        tableEmployes.getItems().setAll(applicationManager.getListeEmployes());
    }
}
