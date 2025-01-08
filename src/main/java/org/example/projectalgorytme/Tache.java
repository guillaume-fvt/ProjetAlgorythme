package org.example.projectalgorytme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tache {
    private int id;
    private String nom;
    private int priorite;
    private Date dateLimite;
    private String categorie;
    private String statut;
    private String commentaires;
    private String description;
    private static List<Tache> listeTaches = new ArrayList<>();

    public Tache(int id, String nom, int priorite, Date dateLimite, String categorie, String statut,String commentaires,String description) {
        this.id = id;
        this.nom = nom;
        this.priorite = priorite;
        this.dateLimite = dateLimite;
        this.categorie = categorie;
        this.statut = statut;
        this.commentaires=commentaires;
        this.description=description;
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

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

    public Date getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(Date dateLimite) {
        this.dateLimite = dateLimite;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getCommentaires(){
        return commentaires;
    }

    public void setCommentaires(String commentaires){
        this.commentaires=commentaires;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public static void ajouterTache(Tache tache) {
        listeTaches.add(tache);
    }

    public static List<Tache> AfficherToutesLesTaches() {
        return new ArrayList<>(listeTaches);
    }

    public static Tache ChercherUneTache(int id) {
        return listeTaches.stream().filter(t -> t.id == id).findFirst().orElse(null);
    }

    public static boolean mettreAJourTache(int id, String nouveauNom, int nouvellePriorite, Date nouvelleDateLimite,
                                           String nouvelleCategorie, String nouveauStatut , String nouveauCommentaire, String nouvelleDescription) {
        Tache tache = ChercherUneTache(id);
        if (tache != null) {
            tache.nom = nouveauNom;
            tache.priorite = nouvellePriorite;
            tache.dateLimite = nouvelleDateLimite;
            tache.categorie = nouvelleCategorie;
            tache.statut = nouveauStatut;
            tache.description=nouvelleDescription;
            tache.commentaires=nouveauCommentaire;
            return true;
        }
        return false;
    }

    public static boolean supprimerTache(int id) {
        return listeTaches.removeIf(t -> t.id == id);
    }
    @Override
    public String toString() {
        return "Tache [id=" + id + ", nom=" + nom + ", priorite=" + priorite + ", date limite = " + getDateLimite() + ", categorie = "+ categorie + ", statut = " +statut + ", description = " + description + ", commentaires = " + commentaires + "]";
    }
}

