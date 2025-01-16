package com.monapp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Projet {
    private int id;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int palierPrecedent;
    private List<Tache> listeTaches = new ArrayList<>();
    private List<Employe> membres = new ArrayList<>();

    public Projet(){}
    public Projet(int id, String nom, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.palierPrecedent = 0;
    }

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public List<Tache> getListeTaches() { return listeTaches; }
    public void setListeTaches(List<Tache> listeTaches) { this.listeTaches = listeTaches; }

    public List<Employe> getMembres() { return membres; }
    public void setMembres(List<Employe> membres) { this.membres = membres; }

    public void ajouterTache(Tache t) {
        listeTaches.add(t);
    }
    public void retirerTache(Tache t) {
        listeTaches.remove(t);
    }

    public void ajouterEmploye(Employe e) {
        if (!membres.contains(e)) {
            membres.add(e);
        }
    }
    public int getPalierPrecedent() {
        return palierPrecedent;
    }

    public void setPalierPrecedent(int palierPrecedent) {
        this.palierPrecedent = palierPrecedent;
    }
    public void retirerEmploye(Employe e) {
        membres.remove(e);
    }

    @Override
    public String toString() {
        return "Projet{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", nbTaches=" + listeTaches.size() +
                ", nbMembres=" + membres.size() +
                '}';
    }
}
