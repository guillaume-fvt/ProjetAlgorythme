package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Tache;
import com.monapp.model.StatutTache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.stream.Collectors;

public class KanbanController {

    @FXML
    private ListView<Tache> listAFaire;
    @FXML
    private ListView<Tache> listEnCours;
    @FXML
    private ListView<Tache> listTermine;

    private ApplicationManager applicationManager;

    public static void ouvrirKanbanScene(ApplicationManager am, Window parentWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(KanbanController.class.getResource("/com/monapp/kanban-view.fxml"));
            AnchorPane root = loader.load();

            KanbanController controller = loader.getController();
            controller.setApplicationManager(am);

            Stage stage = new Stage();
            stage.setTitle("Kanban");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentWindow);
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setApplicationManager(ApplicationManager am) {
        this.applicationManager = am;
        rafraichirKanban();
    }

    @FXML
    public void initialize() {
        // On peut plus tard ajouter du drag & drop
    }

    private void rafraichirKanban() {
        // Filtrer les tâches selon le statut
        listAFaire.getItems().setAll(
                applicationManager.getListeTaches().stream()
                        .filter(t -> t.getStatut() == StatutTache.A_FAIRE)
                        .collect(Collectors.toList())
        );
        listEnCours.getItems().setAll(
                applicationManager.getListeTaches().stream()
                        .filter(t -> t.getStatut() == StatutTache.EN_COURS)
                        .collect(Collectors.toList())
        );
        listTermine.getItems().setAll(
                applicationManager.getListeTaches().stream()
                        .filter(t -> t.getStatut() == StatutTache.TERMINE)
                        .collect(Collectors.toList())
        );
    }
}
