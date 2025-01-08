package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {

    private ApplicationManager applicationManager;

    // Tu peux aussi injecter un MenuBar ou d'autres éléments du FXML si nécessaire

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    @FXML
    public void initialize() {
        // Appelé après chargement du FXML
    }

    @FXML
    public void handleMenuEmployes() {
        chargerVue("/com/monapp/employe-view.fxml");
    }

    @FXML
    public void handleMenuProjets() {
        chargerVue("/com/monapp/projet-view.fxml");
    }

    @FXML
    public void handleMenuTaches() {
        chargerVue("/com/monapp/tache-view.fxml");
    }

    @FXML
    private BorderPane mainBorderPane;

    private void chargerVue(String cheminFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            AnchorPane vue = loader.load();

            Object controller = loader.getController();
            if (controller instanceof EmployeController) {
                ((EmployeController) controller).setApplicationManager(this.applicationManager);
            } else if (controller instanceof ProjetController) {
                ((ProjetController) controller).setApplicationManager(this.applicationManager);
            } else if (controller instanceof TacheController) {
                ((TacheController) controller).setApplicationManager(this.applicationManager);
            }

            mainBorderPane.setCenter(vue);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
