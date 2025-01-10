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
    private TextField tfTitre;
    @FXML
    private TextArea taDescription;
    @FXML
    private ComboBox<StatutTache> cbStatut;
    @FXML
    private DatePicker dpDateLimite;
    @FXML
    private CheckBox cbPrioritaire; // au lieu d'un TextField

    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
        cbStatut.getItems().setAll(StatutTache.values());
        rafraichirTable();
    }

    @FXML
    public void initialize() {
        // On ne fait pas de rafraîchissement ici
    }

    @FXML
    public void ajouterTache() {
        // Si la CheckBox est cochée => priorite = 1, sinon 0
        int priorite = cbPrioritaire.isSelected() ? 1 : 0;

        Tache t = new Tache(
                applicationManager.getListeTaches().size() + 1,
                tfTitre.getText(),
                taDescription.getText(),
                cbStatut.getValue(),
                priorite,
                dpDateLimite.getValue(),
                null
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
            selected.setPriorite(cbPrioritaire.isSelected() ? 1 : 0);
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

    @FXML
    public void ouvrirKanban() {
        // On ouvre une nouvelle fenêtre "kanban-view.fxml"
        KanbanController.ouvrirKanbanScene(applicationManager, tableTaches.getScene().getWindow());
    }

    @FXML
    public void ouvrirCalendrier() {
        // On ouvre une nouvelle fenêtre "calendar-view.fxml"
        CalendarController.ouvrirCalendarScene(applicationManager, tableTaches.getScene().getWindow());
    }

    private void rafraichirTable() {
        tableTaches.getItems().setAll(applicationManager.getListeTaches());
    }
}
