package com.monapp.controller;

import com.monapp.dao.EmployeDAO;
import com.monapp.dao.TacheDAO;
import com.monapp.model.ApplicationManager;
import com.monapp.model.Employe;
import com.monapp.model.Projet;
import com.monapp.model.Tache;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class TachesEmployeController {
    private ApplicationManager applicationManager;
    private Employe employe;
    private TacheDAO tacheDAO=new TacheDAO();
    @FXML
    private ListView<Tache> listViewTachesNonAffectees;

    @FXML
    private ListView<Tache> listViewTachesAffectees;

    @FXML
    private Button btnFermer;

    public void setApplicationManager(ApplicationManager am) {
        this.applicationManager = am;
    }

    public void setEmploye(Employe employe){
        this.employe=employe;
    }

    public void chargerTacheDisponiblePourUnEmploye(int employeId){
        EmployeDAO employeDAO = new EmployeDAO();
        List<Projet> projets = employeDAO.getProjetsParEmploye(employeId);

        if (projets.isEmpty()) {
            System.out.println("Aucun projet associé à l'employé ID " + employeId);
            listViewTachesNonAffectees.getItems().clear();
            return;
        }

        System.out.println("Projets associés à l'employé ID " + employeId + ": " + projets.size());

        // Étape 2 : Récupérer les tâches liées aux projets
        List<Integer> projetIds = projets.stream().map(Projet::getId).collect(Collectors.toList());
        TacheDAO tacheDAO = new TacheDAO();
        List<Tache> taches = tacheDAO.getTachesParProjets(projetIds);

        if (taches.isEmpty()) {
            System.out.println("Aucune tâche associée aux projets de l'employé ID " + employeId);
        } else {
            System.out.println("Tâches récupérées pour l'employé ID " + employeId + ": " + taches.size());
        }

        // Étape 3 : Charger les tâches dans la ListView
        if (listViewTachesAffectees != null) {
            listViewTachesNonAffectees.getItems().setAll(taches);
        } else {
            System.err.println("Erreur : ListView listViewTachesAffectees n'est pas initialisée !");
        }
    }
    public void chargerTachePourUnEmploye(int id) {
            List<Tache> taches = tacheDAO.recupererTachesAffectees(id);
        if (listViewTachesAffectees != null) {
            listViewTachesAffectees.getItems().setAll(taches);
        } else {
            System.err.println("listViewTachesAffectees n'est pas initialisé !");
        }
    }


    public void ajouterTacheAUnEmploye() {
        // Récupérer la tâche sélectionnée
        Tache tacheSelectionne = listViewTachesNonAffectees.getSelectionModel().getSelectedItem();

        if (tacheSelectionne == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune tâche sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une tâche à associer à un employé.");
            alert.showAndWait();
            return;
        }

        // Vérifier que l'employé est sélectionné
        if (employe == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Aucun employé sélectionné");
            alert.setContentText("Veuillez sélectionner un employé avant d'ajouter une tâche.");
            alert.showAndWait();
            return;
        }

        // Ajouter l'employé à la tâche
        try {
            tacheDAO.ajouterEmployeAUneTache(tacheSelectionne.getId(), employe.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Tâche " + tacheSelectionne.getId() + " assignée à l'employé " + employe.getNom() + ".");
            alert.showAndWait();

            // Mettre à jour la liste des tâches disponibles
            chargerTacheDisponiblePourUnEmploye(employe.getId());

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'affectation");
            alert.setContentText("Une erreur s'est produite lors de l'affectation de l'employé à une tâche.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void DissocierTacheAUnEmploye(){
        // Récupérer l'employé sélectionné
        Tache tacheSelectionne = listViewTachesAffectees.getSelectionModel().getSelectedItem();

        if (tacheSelectionne == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune tache sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une tache à dissocier à un employe.");
            alert.showAndWait();
            return;
        }

        // dissocier l'employé au projet
        try {
            tacheDAO.supprimerEmployeAUneTache(tacheSelectionne.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Tâche " + tacheSelectionne.getId() + " dissocié à l'employé " + employe.getNom() + ".");
            alert.showAndWait();

            // Mettre à jour la liste des taches disponibles
            chargerTachePourUnEmploye(employe.getId());

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la dissociation");
            alert.setContentText("Une erreur s'est produite lors de la dissociation de l'employé à une tâche.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    @FXML
    public void fermerFenetre() {
        btnFermer.getScene().getWindow().hide();
    }
}
