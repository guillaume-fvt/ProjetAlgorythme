package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import com.monapp.model.Tache;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TachesProjetController {

    private ApplicationManager applicationManager;
    private Projet projet;

    @FXML
    private ListView<Tache> listViewToutesTaches; // toutes les tâches existantes
    @FXML
    private ListView<Tache> listViewTachesProjet; // tâches du projet

    @FXML
    private Button btnAjouterTache, btnRetirerTache, btnFermer;

    public void setApplicationManager(ApplicationManager am) {
        this.applicationManager = am;
        // On remplit la liste "toutesTaches"
        listViewToutesTaches.getItems().setAll(applicationManager.getListeTaches());
    }

    public void setProjet(Projet p) {
        this.projet = p;
        listViewTachesProjet.getItems().setAll(projet.getListeTaches());
    }

    @FXML
    public void ajouterTacheAuProjet() {
        Tache selected = listViewToutesTaches.getSelectionModel().getSelectedItem();
        if (selected != null && projet != null) {
            if (!projet.getListeTaches().contains(selected)) {
                projet.ajouterTache(selected);
                listViewTachesProjet.getItems().setAll(projet.getListeTaches());
            }
        }
    }

    @FXML
    public void retirerTacheDuProjet() {
        Tache selected = listViewTachesProjet.getSelectionModel().getSelectedItem();
        if (selected != null && projet != null) {
            projet.retirerTache(selected);
            listViewTachesProjet.getItems().setAll(projet.getListeTaches());
        }
    }

    @FXML
    public void fermerFenetre() {
        btnFermer.getScene().getWindow().hide();
    }
}
