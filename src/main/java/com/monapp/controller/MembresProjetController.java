package com.monapp.controller;

import com.monapp.dao.EmployeDAO;
import com.monapp.dao.ProjetDAO;
import com.monapp.model.ApplicationManager;
import com.monapp.model.Employe;
import com.monapp.model.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.stream.Collectors;

public class MembresProjetController {

    private ApplicationManager applicationManager;
    private final ProjetDAO projetDAO = new ProjetDAO();
    private Projet projet;

    @FXML
    private ListView<Employe> listViewEmployes;
    @FXML
    private ListView<Employe> listViewEmployesParProjet;
    @FXML
    private Button btnAjouterMembre, btnFermer;

    public void setApplicationManager(ApplicationManager am) {
        this.applicationManager = am;
    }

    public void setProjet(Projet p) {
        this.projet = p;
        rafraichirListeEmployes();
        chargerEmployesDisponibles(); // Charge les employés disponibles
    }

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur.");
        if (listViewEmployes == null) {
            System.err.println("Erreur : listViewEmployes n'est pas initialisé !");
        }
        if (listViewEmployesParProjet == null) {
            System.err.println("Erreur : listViewEmployesParProjet n'est pas initialisé !");
        }
    }

    // Rafraîchit la liste des membres du projet
    private void rafraichirListeEmployes() {
        projet.setMembres(projetDAO.getEmployesByProjetId(projet.getId()));
    }

    // Charge les employés disponibles (non membres du projet)
    public void chargerEmployesDisponibles() {
        if (listViewEmployes == null) {
            System.err.println("Erreur : listViewEmployes n'est pas initialisé !");
            return;
        }
        if (projet == null) {
            System.err.println("Erreur : le projet n'est pas initialisé !");
            return;
        }
        if (projet.getMembres() == null) {
            System.err.println("Erreur : les membres du projet ne sont pas initialisés !");
            return;
        }

        try {
            EmployeDAO employeDAO = new EmployeDAO();
            List<Employe> employes = employeDAO.getAllEmployes(); // Charger tous les employés depuis la base

            // Filtrer les employés qui ne sont pas déjà membres du projet
            List<Employe> employesDisponibles = employes.stream()
                    .filter(employe -> projet.getMembres().stream()
                            .noneMatch(membre -> membre.getId() == employe.getId()))
                    .collect(Collectors.toList());

            // Mettre à jour la ListView avec les employés disponibles
            listViewEmployes.getItems().setAll(employesDisponibles);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des employés disponibles : " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void chargerEmployesPourUnProjet(int id) {
        EmployeDAO employeDAO = new EmployeDAO();
        List<Employe> employes = employeDAO.getEmployesPourUnProjet(id);

        if (listViewEmployesParProjet != null) {
            listViewEmployesParProjet.getItems().setAll(employes);
        } else {
            System.err.println("listViewEmployesParProjet n'est pas initialisé !");
        }
    }

    @FXML
    public void ajouterMembreAuProjet() {
        // Récupérer l'employé sélectionné
        Employe employeSelectionne = listViewEmployes.getSelectionModel().getSelectedItem();

        if (employeSelectionne == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun employé sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé à associer au projet.");
            alert.showAndWait();
            return;
        }

        // Ajouter l'employé au projet
        try {
            projetDAO.ajouterEmployeAuProjet(employeSelectionne.getId(), projet.getId());
            projet.setMembres(projetDAO.getEmployesByProjetId(projet.getId()));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Employé " + employeSelectionne.getNom() + " ajouté au projet " + projet.getNom() + ".");
            alert.showAndWait();

            // Mettre à jour la liste des employés disponibles
            chargerEmployesDisponibles();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ajout");
            alert.setContentText("Une erreur s'est produite lors de l'ajout de l'employé au projet.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void DissocierMembreAuProjet() {
        // Vérifier si le ListView est initialisé
        if (listViewEmployesParProjet == null) {
            System.err.println("Erreur : listViewEmployes n'est pas initialisé !");
            return;
        }

        // Récupérer l'employé sélectionné
        Employe employeSelectionne = listViewEmployesParProjet.getSelectionModel().getSelectedItem();

        if (employeSelectionne == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun employé sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé à dissocier du projet.");
            alert.showAndWait();
            return;
        }

        // Vérifier si le projet et DAO sont initialisés
        if (projet == null || projetDAO == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Données manquantes");
            alert.setContentText("Le projet ou le DAO n'est pas initialisé.");
            alert.showAndWait();
            return;
        }

        try {
            // Supprimer l'employé du projet dans la base de données
            projetDAO.supprimerEmployeAuProjet(employeSelectionne.getId());

            // Retirer l'employé de la liste en mémoire
            projet.retirerEmploye(employeSelectionne);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Employé " + employeSelectionne.getNom() + " supprimé du projet " + projet.getNom() + ".");
            alert.showAndWait();

            // Mettre à jour la liste des employés disponibles
            chargerEmployesPourUnProjet(projet.getId());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Une erreur s'est produite");
            alert.setContentText("Détails : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }



    @FXML
    public void fermerFenetre() {
        btnFermer.getScene().getWindow().hide();
    }
}
