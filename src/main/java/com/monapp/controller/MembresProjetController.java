package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Employe;
import com.monapp.model.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class MembresProjetController {

    private ApplicationManager applicationManager;
    private Projet projet;

    @FXML
    private ListView<Employe> listViewEmployes;
    @FXML
    private Button btnAjouterMembre, btnFermer;

    public void setApplicationManager(ApplicationManager am) {
        this.applicationManager = am;
        // On remplit la liste par tous les employés existants
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
        btnFermer.getScene().getWindow().hide();
    }
}
