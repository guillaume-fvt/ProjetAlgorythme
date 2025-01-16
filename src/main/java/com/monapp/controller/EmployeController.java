package com.monapp.controller;

import com.monapp.dao.EmployeDAO;
import com.monapp.database.DatabaseConnection;
import com.monapp.model.ApplicationManager;
import com.monapp.model.Employe;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeController {

    private ApplicationManager applicationManager;
    private EmployeDAO employeDAO = new EmployeDAO();

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
            employeDAO.updateEmploye(selected);
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
            employeDAO.deleteEmploye(selected.getId());
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
        // Récupérer l'employé sélectionné dans une liste ou tableau (selon votre interface)
        Employe employeSelectionne = tableEmployes.getSelectionModel().getSelectedItem();

        if (employeSelectionne == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun employé sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé pour afficher son historique.");
            alert.showAndWait();
            return;
        }

        // Liste pour stocker les projets associés à l'employé
        List<Projet> projetsAssocies = new ArrayList<>();

        // Charger les projets associés à cet employé depuis la base de données
        String query = "SELECT p.id, p.nom, p.date_debut, p.date_fin " +
                "FROM Projet p " +
                "JOIN Employe_Projet ep ON p.id = ep.projet_id " +
                "WHERE ep.employe_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeSelectionne.getId()); // Assurez-vous que l'objet Employe a un ID valide
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Projet projet = new Projet();
                    projet.setId(rs.getInt("id"));
                    projet.setNom(rs.getString("nom"));
                    projet.setDateDebut(rs.getDate("date_debut").toLocalDate());
                    projet.setDateFin(rs.getDate("date_fin").toLocalDate());
                    projetsAssocies.add(projet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la récupération des projets associés.");
            alert.showAndWait();
            return;
        }

        // Vérifier si l'employé a des projets associés
        if (projetsAssocies.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aucun projet trouvé");
            alert.setHeaderText(null);
            alert.setContentText("Aucun projet associé à l'employé sélectionné.");
            alert.showAndWait();
            return;
        }

        // Construire un message pour afficher les projets
        StringBuilder message = new StringBuilder("Historique des projets de l'employé :\n");
        for (Projet projet : projetsAssocies) {
            message.append("- ").append(projet.getNom())
                    .append(" (du ").append(projet.getDateDebut())
                    .append(" au ").append(projet.getDateFin()).append(")\n");
        }

        // Afficher l'historique dans une boîte de dialogue
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Historique Employé");
        alert.setHeaderText("Projets associés à l'employé : " + employeSelectionne.getNom());
        alert.setContentText(message.toString());
        alert.showAndWait();
    }
    @FXML
    public void AffecterTache() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun employé sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé dans la table avant d'ajouter des tâches.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/tache-employe-ajouter-view.fxml"));
            AnchorPane root = loader.load();

            // Récupérer le contrôleur associé à la vue
            TachesEmployeController controller = loader.getController();
            controller.setApplicationManager(this.applicationManager);
            controller.setEmploye(selected);

            // Charger la liste des employés depuis la base
            controller.chargerTacheDisponiblePourUnEmploye(selected.getId());

            // Afficher la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Affecter des tâches à " + selected.getNom());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void DissocierTache() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun employé sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé dans la table avant d'ajouter des tâches.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/taches-employe-supprimer-view.fxml"));
            AnchorPane root = loader.load();

            // Récupérer le contrôleur associé à la vue
            TachesEmployeController controller = loader.getController();
            controller.setApplicationManager(this.applicationManager);
            controller.setEmploye(selected);

            // Charger la liste des employés depuis la base
            controller.chargerTachePourUnEmploye(selected.getId());

            // Afficher la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Dissocier des tâches à " + selected.getNom());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void rafraichirTable() {
        tableEmployes.getItems().setAll(employeDAO.getAllEmployes());
        tableEmployes.refresh();
    }
}
