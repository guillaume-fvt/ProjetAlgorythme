package com.monapp.controller;

import com.monapp.dao.TacheDAO;
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

    @FXML
    private Label monthLabel;

    private ApplicationManager applicationManager;
    private TacheDAO tacheDAO;
    private List<Tache> listeTaches;
    private YearMonth moisCourant;

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

    @FXML
    public void initialize() {
        this.tacheDAO = new TacheDAO();
        this.listeTaches = tacheDAO.getToutesLesTaches(); // Charger les tâches depuis la base
        this.moisCourant = YearMonth.now();
        afficherCalendrierDuMois();
    }

    /**
     * Affiche le calendrier pour le mois actuel stocké dans "moisCourant".
     */
    private void afficherCalendrierDuMois() {
        afficherCalendrier(moisCourant.atDay(1));
        monthLabel.setText(moisCourant.getMonth().toString() + " " + moisCourant.getYear());
    }

    /**
     * Construit un calendrier pour le mois de la date fournie.
     */
    private void afficherCalendrier(LocalDate dateCourante) {
        // On nettoie le grid
        calendarGrid.getChildren().clear();
        calendarGrid.setGridLinesVisible(true);

        // Noms des jours en en-tête
        String[] jours = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int col = 0; col < 7; col++) {
            Label lblJour = new Label(jours[col]);
            lblJour.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
            calendarGrid.add(lblJour, col, 0);
        }

        YearMonth ym = YearMonth.from(dateCourante);
        LocalDate premierJour = ym.atDay(1);

        int premierJourSemaine = premierJour.getDayOfWeek().getValue();
        int dayCounter = 1;
        int totalDays = ym.lengthOfMonth();

        int row = 1;
        while (dayCounter <= totalDays) {
            for (int col = 0; col < 7; col++) {
                if ((row == 1 && col + 1 < premierJourSemaine) || (dayCounter > totalDays)) {
                    calendarGrid.add(new Label(""), col, row);
                } else {
                    LocalDate currentDate = LocalDate.of(ym.getYear(), ym.getMonth(), dayCounter);

                    List<Tache> tachesDuJour = listeTaches.stream()
                            .filter(t -> currentDate.equals(t.getDateLimite()))
                            .collect(Collectors.toList());

                    VBox boxJour = new VBox();
                    boxJour.setPadding(new Insets(5));
                    Label lblDate = new Label(String.valueOf(dayCounter));
                    lblDate.setStyle("-fx-font-weight: bold;");
                    boxJour.getChildren().add(lblDate);

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
            if (row > 6) break;
        }
    }

    @FXML
    private void moisPrecedent() {
        this.moisCourant = this.moisCourant.minusMonths(1);
        afficherCalendrierDuMois();
    }

    @FXML
    private void moisSuivant() {
        this.moisCourant = this.moisCourant.plusMonths(1);
        afficherCalendrierDuMois();
    }
}
