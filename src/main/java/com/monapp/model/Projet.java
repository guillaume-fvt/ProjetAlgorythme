package com.monapp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Projet {
    private int id;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private List<Tache> listeTaches = new ArrayList<>();
    private List<Employe> membres = new ArrayList<>();

    public Projet() {
    }

    public Projet(int id, String nom, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // Getters, Setters, etc.
    // ...
}
