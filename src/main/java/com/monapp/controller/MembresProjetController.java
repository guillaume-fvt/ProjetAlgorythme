package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Employe;
import com.monapp.model.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class MembresProjetController {

    private ApplicationManager applicationManager;
    private Projet projet; // le projet sur lequel on ajoute des membres

    @FXML
    private ListView<Employe> listViewEmployes;

    @FXML
    private Button btnAjouterMembre, btnFermer;

    public void setApplicationManager(ApplicationManager am) {
        this.applicationManager = am;
        // Remplir la liste avec tous les employés disponibles
        listViewEmployes.getItems().setAll(applicationManager.getListeEmployes());
    }

    public void setProjet(Projet p) {
        this.projet = p;
    }

    @FXML
    public void ajouterMembreAuProjet() {
        Employe selectedEmp = listViewEmployes.getSelectionModel().getSelectedItem();
        if (selectedEmp != null && projet != null) {
            projet.ajouterEmploye(selectedEmp);
        }
    }

    @FXML
    public void fermerFenetre() {
        // On ferme la fenêtre
        btnFermer.getScene().getWindow().hide();
    }
}
