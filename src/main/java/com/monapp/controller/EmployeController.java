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
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

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
