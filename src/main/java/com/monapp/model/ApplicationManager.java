package com.monapp.model;

import com.monapp.dao.EmployeDAO;
import com.monapp.dao.ProjetDAO;
import com.monapp.dao.TacheDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationManager {

    private List<Employe> listeEmployes;
    private final List<Projet> listeProjets;
    private final List<Tache> listeTaches;
    TacheDAO tacheDAO = new TacheDAO();

    public ApplicationManager() {
        listeEmployes = new ArrayList<>();
        listeProjets = new ArrayList<>();
        listeTaches = new ArrayList<>();
    }

    // EMPLOYES
    public void ajouterEmploye(Employe e) {
        if (listeEmployes.stream().noneMatch(emp -> emp.getId() == e.getId())) {
            try {
                EmployeDAO employeDAO = new EmployeDAO();
                employeDAO.addEmploye(e);
                chargerEmployes();
            } catch (Exception ex) {
                System.err.println("Erreur lors de l'ajout de l'employé : " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            System.out.println("Employé déjà existant : " + e.getNom());
        }
    }

    public void supprimerEmploye(int id) {
        try {
            EmployeDAO employeDAO = new EmployeDAO();
            employeDAO.deleteEmploye(id);
            chargerEmployes();
        } catch (Exception ex) {
            System.err.println("Erreur lors de la suppression de l'employé : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void modifierEmploye(Employe e) {}

    public List<Employe> getListeEmployes() {
        return listeEmployes;
    }

    public void chargerEmployes() {
        try {
            EmployeDAO employeDAO = new EmployeDAO();
            listeEmployes = employeDAO.getAllEmployes();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des employés : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // PROJETS
    public void chargerDonnees() {
        try {
            chargerEmployes();
            ProjetDAO projetDAO = new ProjetDAO();
            listeProjets.clear();
            listeProjets.addAll(projetDAO.getTousLesProjets());
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void ajouterProjet(Projet p) {
        if (listeProjets.stream().noneMatch(prj -> prj.getId() == p.getId())) {
            listeProjets.add(p);
        }
    }

    public void modifierProjet(Projet p) {}
    public void supprimerProjet(int id) {
        listeProjets.removeIf(prj -> prj.getId() == id);
    }

    public List<Projet> getListeProjets() {
        return listeProjets;
    }

    // TACHES
    public void ajouterTache(Tache t) { listeTaches.add(t); }
    public void modifierTache(Tache t) {}
    public void supprimerTache(int id) {
        listeTaches.removeIf(t -> t.getId() == id);
    }
    public List<Tache> getListeTaches() {
        return listeTaches;
    }
    public void ajouterTacheAuProjet(int tacheId, int projetId) {
            tacheDAO.assignerTacheAuProjet(tacheId, projetId);

    }
    public void supprimerTacheAuProjet(int tacheId, int projetId) {
            tacheDAO.supprimerTacheAuProjet(tacheId, projetId);
    }

}
