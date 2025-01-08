package org.example.projectalgorytme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Projet {
    private int id;
    private String nom;
    private Date dateDebut;
    private Date dateFin;
    private List<Tache> listeTaches;
    private List<MembreProjet> membres;
    private static List<Projet> listeProjets = new ArrayList<>();

    public Projet(int id, String nom, Date dateDebut, Date dateFin) {
        this.id = id;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.listeTaches = new ArrayList<>();
        this.membres = new ArrayList<>();
    }

    // Méthodes CRUD
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public List<Tache> getListeTaches() {
        return listeTaches;
    }

    public static void ajouterProjet(Projet projet) {
        listeProjets.add(projet);
    }

    public static List<Projet> AfficherTousLesProjets() {
        return new ArrayList<>(listeProjets);
    }

    public static Projet ChercherProjet(int id) {
        return listeProjets.stream().filter(p -> p.id == id).findFirst().orElse(null);
    }

    public static boolean ModifierProjet(/*id du projet à modifier*/int id, String nouveauNom, Date nouvelleDateDebut, Date nouvelleDateFin) {
        Projet projet = ChercherProjet(id);
        if (projet != null) {
            projet.nom = nouveauNom;
            projet.dateDebut = nouvelleDateDebut;
            projet.dateFin = nouvelleDateFin;
            return true;
        }
        return false;
    }

    public static boolean supprimerProjet(int id) {
        return listeProjets.removeIf(p -> p.id == id);
    }

    public void ajouterTache(Tache tache) {
        listeTaches.add(tache);
    }

    public void supprimerTache(Tache tache) {
        listeTaches.remove(tache);
    }

    public List<MembreProjet> AfficherMembres() {
        return new ArrayList<>(this.membres);
    }

    public boolean assignerEmploye(Employe employe,String role) {
        if(Employe.ChercherEmploye(employe.getId())!=null) {
            MembreProjet membre = new MembreProjet(employe.getId(), employe.getNom(), role);
            this.membres.add(membre);
            employe.ajouterHistorique(this.nom);
            return true;
        }
        return false;
    }

    public boolean modifierRole(int employeId, String nouveauRole) {
        for (MembreProjet membre : membres) {
            if (membre.getEmployeId() == employeId) {
                membre.setRole(nouveauRole);
                return true;
            }
        }
        return false;
    }

    public boolean supprimerMembre(int employeId) {
        Employe.ChercherEmploye(employeId).supprimerHistorique(this.nom);
        return this.membres.removeIf(membre -> membre.getEmployeId() == employeId);
    }

    @Override
    public String toString() {
        return "Projet [id=" + id + ", nom=" + nom + membres+"]";
    }
    static class MembreProjet {
        private int employeId;
        private String employeNom;
        private String role;

        public MembreProjet(int employeId, String employeNom, String role) {
            this.employeId = employeId;
            this.employeNom = employeNom;
            this.role = role;
        }

        public int getEmployeId() {
            return employeId;
        }

        public String getEmployeNom() {
            return employeNom;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}

