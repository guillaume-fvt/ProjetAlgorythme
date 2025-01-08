package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.StatutTache;
import com.monapp.model.Tache;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TacheController {

    private ApplicationManager applicationManager;

    @FXML
    private TableView<Tache> tableTaches;
    @FXML
    private TextField tfTitre, tfPriorite;
    @FXML
    private TextArea taDescription;
    @FXML
    private ComboBox<StatutTache> cbStatut;
    @FXML
    private DatePicker dpDateLimite;

    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
    }

    @FXML
    public void initialize() {
        // Charger les valeurs de l'enum dans la ComboBox
        cbStatut.getItems().setAll(StatutTache.values());
        rafraichirTable();
    }

    @FXML
    public void ajouterTache() {
        Tache t = new Tache(
                applicationManager.getListeTaches().size() + 1,
                tfTitre.getText(),
                taDescription.getText(),
                cbStatut.getValue(),
                Integer.parseInt(tfPriorite.getText()),
                dpDateLimite.getValue(),
                null  // pas d'employé assigné pour l'instant
        );
        applicationManager.ajouterTache(t);
        rafraichirTable();
    }

    @FXML
    public void modifierTache() {
        Tache selected = tableTaches.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setTitre(tfTitre.getText());
            selected.setDescription(taDescription.getText());
            selected.setStatut(cbStatut.getValue());
            selected.setPriorite(Integer.parseInt(tfPriorite.getText()));
            selected.setDateLimite(dpDateLimite.getValue());
            applicationManager.modifierTache(selected);
            rafraichirTable();
        }
    }

    @FXML
    public void supprimerTache() {
        Tache selected = tableTaches.getSelectionModel().getSelectedItem();
        if (selected != null) {
            applicationManager.supprimerTache(selected.getId());
            rafraichirTable();
        }
    }

    private void rafraichirTable() {
        tableTaches.getItems().setAll(applicationManager.getListeTaches());
    }
}
