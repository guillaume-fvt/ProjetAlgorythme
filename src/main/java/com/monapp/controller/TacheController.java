package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.StatutTache;
import com.monapp.model.Tache;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;

public class TacheController {

    private ApplicationManager applicationManager;

    @FXML
    private TableView<Tache> tableTaches;
    @FXML
    private TableColumn<Tache, String> colTitre;
    @FXML
    private TableColumn<Tache, String> colStatut;
    @FXML
    private TableColumn<Tache, String> colDateLimite;
    @FXML
    private TableColumn<Tache, String> colPrioritaire;

    @FXML
    private TextField tfTitre;
    @FXML
    private TextArea taDescription;
    @FXML
    private ComboBox<StatutTache> cbStatut;
    @FXML
    private DatePicker dpDateLimite;
    @FXML
    private CheckBox cbPrioritaire;

    @FXML
    public void initialize() {
        // Paramétrer les colonnes
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut().name()));

        colDateLimite.setCellValueFactory(c -> {
            Tache t = c.getValue();
            if (t.getDateLimite() == null) return new SimpleStringProperty("");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return new SimpleStringProperty(t.getDateLimite().format(fmt));
        });

        colPrioritaire.setCellValueFactory(c -> {
            Tache t = c.getValue();
            String prior = (t.getPriorite() > 0) ? "Oui" : "Non";
            return new SimpleStringProperty(prior);
        });
    }

    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
        cbStatut.getItems().setAll(StatutTache.values());
        rafraichirTable();
    }

    @FXML
    public void ajouterTache() {
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
        KanbanController.ouvrirKanbanScene(applicationManager, tableTaches.getScene().getWindow());
    }

    @FXML
    public void ouvrirCalendrier() {
        // Ouvre la vue calendrier
        CalendarController.ouvrirCalendarScene(applicationManager, tableTaches.getScene().getWindow());
    }

    private void rafraichirTable() {
        tableTaches.getItems().setAll(applicationManager.getListeTaches());
        tableTaches.refresh();
    }
}
