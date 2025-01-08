package com.monapp.model;

import java.time.LocalDate;

public class Tache {
    private int id;
    private String titre;
    private String description;
    private StatutTache statut;
    private int priorite;
    private LocalDate dateLimite;
    private Employe employeAssigne; // optionnel

    public Tache() {
    }

    public Tache(int id, String titre, String description,
                 StatutTache statut, int priorite, LocalDate dateLimite, Employe employeAssigne) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.priorite = priorite;
        this.dateLimite = dateLimite;
        this.employeAssigne = employeAssigne;
    }

    // Getters, Setters, etc.
    // ...
}
