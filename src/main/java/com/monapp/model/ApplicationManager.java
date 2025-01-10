package com.monapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationManager {

    private final List<Employe> listeEmployes;
    private final List<Projet> listeProjets;
    private final List<Tache> listeTaches;

    public ApplicationManager() {
        listeEmployes = new ArrayList<>();
        listeProjets = new ArrayList<>();
        listeTaches = new ArrayList<>();
    }

    // --- EMPLOYES ---
    public void ajouterEmploye(Employe e) {
        listeEmployes.add(e);
    }
    public void modifierEmploye(Employe e) {
        // On suppose qu'il est déjà dans la liste
        // Rien de spécial, on a juste mis à jour ses champs
    }
    public void supprimerEmploye(int id) {
        listeEmployes.removeIf(emp -> emp.getId() == id);
    }
    public List<Employe> getListeEmployes() {
        return listeEmployes;
    }

    // --- PROJETS ---
    public void ajouterProjet(Projet p) {
        listeProjets.add(p);
    }
    public void modifierProjet(Projet p) {
        // Idem
    }
    public void supprimerProjet(int id) {
        listeProjets.removeIf(prj -> prj.getId() == id);
    }
    public List<Projet> getListeProjets() {
        return listeProjets;
    }

    // --- TACHES ---
    public void ajouterTache(Tache t) {
        listeTaches.add(t);
    }
    public void modifierTache(Tache t) {
        // ...
    }
    public void supprimerTache(int id) {
        listeTaches.removeIf(tache -> tache.getId() == id);
    }
    public List<Tache> getListeTaches() {
        return listeTaches;
    }

    /**
     * Retourne la liste des projets où l'employé figure dans la liste "membres".
     */
    public List<Projet> getProjetsByEmployee(Employe e) {
        return listeProjets.stream()
                .filter(prj -> prj.getMembres().contains(e))
                .collect(Collectors.toList());
    }

    // Optionnel : persistance
    public void sauvegarderDonnees() {
    }
    public void chargerDonnees() {
    }
}
