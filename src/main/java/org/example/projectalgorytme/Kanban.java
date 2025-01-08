package org.example.projectalgorytme;

import java.util.ArrayList;
import java.util.List;

public class Kanban {
    private List<Tache> aFaire;
    private List<Tache> enCours;
    private List<Tache> termine;

    public Kanban() {
        aFaire = new ArrayList<>();
        enCours = new ArrayList<>();
        termine = new ArrayList<>();
    }
    // Ajouter une tâche dans la colonne correspondante
    public void ajouterTache(Tache tache) {
        switch (tache.getStatut()) {
            case "À faire":
                aFaire.add(tache);
                break;
            case "En cours":
                enCours.add(tache);
                break;
            case "Terminé":
                termine.add(tache);
                break;
            default:
                System.out.println("Statut non valide pour la tâche: " + tache.getNom());
        }
    }

    // Déplacer une tâche d'une colonne à une autre
    public boolean deplacerTache(int id, String nouveauStatut) {
        Tache tache = Tache.ChercherUneTache(id);
        if (tache != null) {
            // Retirer la tâche de la colonne actuelle
            if (tache.getStatut().equals("À faire")) {
                aFaire.remove(tache);
            } else if (tache.getStatut().equals("En cours")) {
                enCours.remove(tache);
            } else if (tache.getStatut().equals("Terminé")) {
                termine.remove(tache);
            }

            // Mettre à jour le statut et ajouter à la nouvelle colonne
            tache.setStatut(nouveauStatut);
            ajouterTache(tache);
            return true;
        }
        return false;
    }

    // Afficher toutes les tâches dans le Kanban
    public void afficherKanban() {
        System.out.println("### À faire ###");
        for (Tache t : aFaire) {
            System.out.println(t);
        }

        System.out.println("\n### En cours ###");
        for (Tache t : enCours) {
            System.out.println(t);
        }

        System.out.println("\n### Terminé ###");
        for (Tache t : termine) {
            System.out.println(t);
        }
    }
}
