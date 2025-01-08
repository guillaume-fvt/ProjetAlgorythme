package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ProjetController {

    private ApplicationManager applicationManager;

    @FXML
    private TableView<Projet> tableProjets;
    @FXML
    private TextField tfNomProjet;
    @FXML
    private DatePicker dpDateDebut, dpDateFin;

    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
    }

    @FXML
    public void initialize() {
        rafraichirTable();
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

    private void rafraichirTable() {
        tableProjets.getItems().setAll(applicationManager.getListeProjets());
    }
}
