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
    private Integer projetId; // ID du projet associé (peut être null)

    public Tache() {
    }

    public Tache(int id, String titre, String description,
                 StatutTache statut, int priorite, LocalDate dateLimite,
                 Employe employeAssigne, Integer projetId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.priorite = priorite;
        this.dateLimite = dateLimite;
        this.employeAssigne = employeAssigne;
        this.projetId = projetId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutTache getStatut() {
        return statut;
    }

    public void setStatut(StatutTache statut) {
        this.statut = statut;
    }

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

    public LocalDate getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(LocalDate dateLimite) {
        this.dateLimite = dateLimite;
    }

    public Employe getEmployeAssigne() {
        return employeAssigne;
    }

    public void setEmployeAssigne(Employe employeAssigne) {
        this.employeAssigne = employeAssigne;
    }

    public Integer getProjetId() {
        return projetId;
    }

    public void setProjetId(Integer projetId) {
        this.projetId = projetId;
    }

    @Override
    public String toString() {
        return "Tache{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", statut=" + statut +
                ", priorite=" + priorite +
                ", dateLimite=" + dateLimite +
                ", employeAssigne=" + (employeAssigne != null ? employeAssigne.getNom() : "Aucun") +
                ", projetId=" + (projetId != null ? projetId : "Aucun") +
                '}';
    }
}
