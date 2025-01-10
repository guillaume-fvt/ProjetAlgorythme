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

    // EMPLOYES
    public void ajouterEmploye(Employe e) {
        listeEmployes.add(e);
    }
    public void modifierEmploye(Employe e) {}
    public void supprimerEmploye(int id) {
        listeEmployes.removeIf(emp -> emp.getId() == id);
    }
    public List<Employe> getListeEmployes() { return listeEmployes; }

    // PROJETS
    public void ajouterProjet(Projet p) {
        listeProjets.add(p);
    }
    public void modifierProjet(Projet p) {}
    public void supprimerProjet(int id) {
        listeProjets.removeIf(prj -> prj.getId() == id);
    }
    public List<Projet> getListeProjets() { return listeProjets; }

    // TACHES
    public void ajouterTache(Tache t) { listeTaches.add(t); }
    public void modifierTache(Tache t) {}
    public void supprimerTache(int id) {
        listeTaches.removeIf(t -> t.getId() == id);
    }
    public List<Tache> getListeTaches() { return listeTaches; }

    // Obtenir la liste des projets d'un employé
    public List<Projet> getProjetsByEmployee(Employe e) {
        return listeProjets.stream()
                .filter(prj -> prj.getMembres().contains(e))
                .collect(Collectors.toList());
    }

    // Sauvegarde / chargement si besoin
    public void sauvegarderDonnees() {}
    public void chargerDonnees() {}
}
