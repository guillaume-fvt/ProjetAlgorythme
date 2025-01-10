package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Tache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class CalendarController {

    @FXML
    private ListView<String> listDates;

    private ApplicationManager applicationManager;

    public static void ouvrirCalendarScene(ApplicationManager am, Window parentWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(CalendarController.class.getResource("/com/monapp/calendar-view.fxml"));
            AnchorPane root = loader.load();

            CalendarController controller = loader.getController();
            controller.setApplicationManager(am);

            Stage stage = new Stage();
            stage.setTitle("Calendrier");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentWindow);
            stage.setScene(new Scene(root, 400, 400));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setApplicationManager(ApplicationManager am) {
        this.applicationManager = am;
        afficherDates();
    }

    @FXML
    public void initialize() {
    }

    private void afficherDates() {
        listDates.getItems().clear();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        applicationManager.getListeTaches().stream()
                .sorted(Comparator.comparing(t -> t.getDateLimite(), Comparator.nullsLast(Comparator.naturalOrder())))
                .forEach(t -> {
                    String dateStr = (t.getDateLimite() != null) ? t.getDateLimite().format(fmt) : "Sans date";
                    listDates.getItems().add(t.getTitre() + " - " + dateStr + " (" + t.getStatut() + ")");
                });
    }
}
