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

    @FXML
    private Button btnKanban, btnCalendrier;

    @FXML
    public void initialize() {
        // Configure if you have any TableColumn here
        cbStatut.getItems().setAll(StatutTache.values());
    }

    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
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

    /**
     * Vue Kanban (3 colonnes) - à implémenter
     */
    @FXML
    public void ouvrirKanban() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Vue Kanban");
        alert.setHeaderText("Kanban (À faire / En cours / Terminé)");
        alert.setContentText("Ici, tu peux implémenter un Drag & Drop pour déplacer les Tâches.");
        alert.showAndWait();
    }

    /**
     * Vue Calendrier - à implémenter
     */
    @FXML
    public void ouvrirCalendrier() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Calendrier");
        alert.setHeaderText("Visualisation des échéances");
        alert.setContentText("Ici, tu peux afficher un calendrier pour les dates-limite des Tâches.");
        alert.showAndWait();
    }

    private void rafraichirTable() {
        tableTaches.getItems().setAll(applicationManager.getListeTaches());
    }
}
