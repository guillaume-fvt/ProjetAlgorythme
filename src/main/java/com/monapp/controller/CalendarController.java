package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Tache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarController {

    @FXML
    private GridPane calendarGrid;

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
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setApplicationManager(ApplicationManager am) {
        this.applicationManager = am;
        afficherCalendrier(LocalDate.now());
    }

    /**
     * Construit un calendrier pour le mois de "dateCourante" (par ex. LocalDate.now()).
     */
    private void afficherCalendrier(LocalDate dateCourante) {
        // On nettoie le grid
        calendarGrid.getChildren().clear();
        calendarGrid.setGridLinesVisible(true);

        // Noms des jours en en-tête
        String[] jours = {"Lun","Mar","Mer","Jeu","Ven","Sam","Dim"};
        for (int col = 0; col < 7; col++) {
            Label lblJour = new Label(jours[col]);
            lblJour.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
            calendarGrid.add(lblJour, col, 0);
        }

        YearMonth ym = YearMonth.from(dateCourante);
        LocalDate premierJour = ym.atDay(1);

        // Jour de la semaine (1 = lundi, 7=dimanche)
        int premierJourSemaine = premierJour.getDayOfWeek().getValue();
        // En Java, Lundi=1 ... Dimanche=7

        // Index dans la grille : la ligne 1 (row=1) correspond à la 1ère semaine
        int dayCounter = 1;
        int totalDays = ym.lengthOfMonth();

        // On fait un maximum de 6 lignes
        int row = 1;
        while (dayCounter <= totalDays) {
            for (int col = 0; col < 7; col++) {
                // Calcul si on est avant le "premierJourSemaine"
                if ((row == 1 && col+1 < premierJourSemaine) || (dayCounter > totalDays)) {
                    // Case vide
                    calendarGrid.add(new Label(""), col, row);
                } else {
                    LocalDate currentDate = LocalDate.of(ym.getYear(), ym.getMonth(), dayCounter);

                    // Récupérer les tâches pour ce jour
                    List<Tache> tachesDuJour = applicationManager.getListeTaches().stream()
                            .filter(t -> currentDate.equals(t.getDateLimite()))
                            .collect(Collectors.toList());

                    // Construire un label
                    VBox boxJour = new VBox();
                    boxJour.setPadding(new Insets(5));
                    Label lblDate = new Label(String.valueOf(dayCounter));
                    lblDate.setStyle("-fx-font-weight: bold;");
                    boxJour.getChildren().add(lblDate);

                    // Ajouter un label par tâche
                    for (Tache t : tachesDuJour) {
                        Label lblTache = new Label(" • " + t.getTitre() + " (" + t.getStatut() + ")");
                        lblTache.setStyle("-fx-font-size: 10;");
                        boxJour.getChildren().add(lblTache);
                    }

                    calendarGrid.add(boxJour, col, row);
                    dayCounter++;
                }
            }
            row++;
            if (row > 6) break; // sécurité
        }
    }
}
