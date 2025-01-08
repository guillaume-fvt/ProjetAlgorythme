package com.monapp.controller;

import com.monapp.model.ApplicationManager;
import com.monapp.model.Employe;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EmployeController {

    private ApplicationManager applicationManager;

    @FXML
    private TableView<Employe> tableEmployes;
    @FXML
    private TableColumn<Employe, String> colNom;
    @FXML
    private TableColumn<Employe, String> colPrenom;
    @FXML
    private TableColumn<Employe, String> colRole;

    @FXML
    private TextField tfNom, tfPrenom, tfRole;

    public void setApplicationManager(ApplicationManager manager) {
        this.applicationManager = manager;
    }

    @FXML
    public void initialize() {
        // Si tu utilises PropertyValueFactory, importe javafx.scene.control.cell.PropertyValueFactory
        // colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        // colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        // colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        rafraichirTable();
    }

    @FXML
    public void ajouterEmploye() {
        Employe e = new Employe(
                // un id auto ou un id = taille liste + 1
                applicationManager.getListeEmployes().size() + 1,
                tfNom.getText(),
                tfPrenom.getText(),
                tfRole.getText()
        );
        applicationManager.ajouterEmploye(e);
        rafraichirTable();
    }

    @FXML
    public void modifierEmploye() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setNom(tfNom.getText());
            selected.setPrenom(tfPrenom.getText());
            selected.setRole(tfRole.getText());
            applicationManager.modifierEmploye(selected);
            rafraichirTable();
        }
    }

    @FXML
    public void supprimerEmploye() {
        Employe selected = tableEmployes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            applicationManager.supprimerEmploye(selected.getId());
            rafraichirTable();
        }
    }

    private void rafraichirTable() {
        tableEmployes.getItems().setAll(applicationManager.getListeEmployes());
    }
}
