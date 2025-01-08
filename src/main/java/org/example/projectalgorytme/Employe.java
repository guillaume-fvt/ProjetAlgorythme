package org.example.projectalgorytme;

import java.util.ArrayList;
import java.util.List;

public class Employe {
    private int id;
    private String nom;
    private String email;
    private List<String> historiqueProjets;
    private static List<Employe> listeEmployes = new ArrayList<>();
    public Employe(int id, String nom, String email) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.historiqueProjets = new ArrayList<>();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getHistoriqueProjets() {
        return historiqueProjets;
    }

    public static void ajouterEmploye(Employe employe) {
        listeEmployes.add(employe);
    }

    public static List<Employe> AfficherTousLesEmployes() {
        return new ArrayList<>(listeEmployes);
    }

    public static Employe ChercherEmploye(int id) {
        return listeEmployes.stream().filter(e -> e.id == id).findFirst().orElse(null);
    }

    public static boolean ModifierEmploye(/*id de l'employé à modifier*/int id, String nouveauNom, String nouvelEmail) {
        Employe employe = ChercherEmploye(id);
        if (employe != null) {
            employe.nom = nouveauNom;
            employe.email = nouvelEmail;
            return true;
        }
        return false;
    }

    public static boolean supprimerEmploye(int id) {
        return listeEmployes.removeIf(e -> e.id == id);
    }

    public void ajouterHistorique(String projet) {
        historiqueProjets.add(projet);
    }

    public void supprimerHistorique(String projet) {
        historiqueProjets.remove(projet);
    }

    @Override
    public String toString() {
        return "Employe [id=" + id + ", nom=" + nom + ", email=" + email + "]";
    }
}

