package com.monapp.controller;

import com.monapp.dao.TacheDAO;
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
import java.util.List;

import java.io.IOException;
import java.util.stream.Collectors;

public class KanbanController {

    @FXML
    private ListView<Tache> listAFaire;
    @FXML
    private ListView<Tache> listEnCours;
    @FXML
    private ListView<Tache> listTermine;

    private TacheDAO tacheDAO;

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
        this.tacheDAO = new TacheDAO(); // Assurez-vous que TacheDAO est correctement implémenté
        rafraichirKanban(); // Charger les tâches au démarrage
    }

    private void rafraichirKanban() {
        if (tacheDAO == null) {
            System.out.println("Erreur : tacheDAO est null !");
            return;
        }

        List<Tache> taches = tacheDAO.getToutesLesTaches();
        if (taches == null || taches.isEmpty()) {
            System.out.println("Aucune tâche trouvée dans la base de données !");
            return;
        }

        listAFaire.getItems().clear();
        listEnCours.getItems().clear();
        listTermine.getItems().clear();

        for (Tache t : taches) {
            switch (t.getStatut()) {
                case A_FAIRE:
                    listAFaire.getItems().add(t);
                    break;
                case EN_COURS:
                    listEnCours.getItems().add(t);
                    break;
                case TERMINE:
                    listTermine.getItems().add(t);
                    break;
            }
        }
    }


}
