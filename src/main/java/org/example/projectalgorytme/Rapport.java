package org.example.projectalgorytme;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Date;

public class Rapport {
    public static void genererRapportAvancementCSV(List<Projet> projets, String cheminFichier) {
        try (FileWriter writer = new FileWriter(cheminFichier)) {
            writer.append("ID,Nom,Total Taches,Taches Terminées,Avancement (%)\n");
            for (Projet projet : projets) {
                int totalTaches = projet.getListeTaches().size();
                int tachesTerminees = (int) projet.getListeTaches().stream()
                        .filter(t -> t.getStatut().equals("Terminé"))
                        .count();
                double avancement = totalTaches > 0 ? (tachesTerminees * 100.0) / totalTaches : 0.0;

                writer.append(projet.getId() + ",")
                        .append(projet.getNom() + ",")
                        .append(totalTaches + ",")
                        .append(tachesTerminees + ",")
                        .append(avancement + "%\n");
            }
            System.out.println("Rapport d'avancement des projets généré avec succès !");
        } catch (IOException e) {
            System.out.println("Erreur lors de la génération du rapport d'avancement : " + e.getMessage());
        }
    }
    public static void genererRapportRetardsCSV(List<Projet> projets, String cheminFichier) {
        try (FileWriter writer = new FileWriter(cheminFichier)) {
            writer.append("ID Tache,Nom de la tache,Date Limite,Statut,Retard (jours)\n");
            for (Projet projet : projets) {
                for (Tache tache : projet.getListeTaches()) {
                    if (!tache.getStatut().equals("Terminé") && tache.getDateLimite().before(new Date())) {
                        long retard = (new Date().getTime() - tache.getDateLimite().getTime()) / (1000 * 60 * 60 * 24); // Retard en jours
                        writer.append(tache.getId() + ",")
                                .append(tache.getNom() + ",")
                                .append(tache.getDateLimite().toString() + ",")
                                .append(tache.getStatut() + ",")
                                .append(retard + " jours\n");
                    }

                }
            }
            System.out.println("Rapport des retards généré avec succès !");
        } catch (IOException e) {
            System.out.println("Erreur lors de la génération du rapport des retards : " + e.getMessage());
        }
    }
    public static void genererRapportBudgetCSV(List<Projet> projets, String cheminFichier) {
        try (FileWriter writer = new FileWriter(cheminFichier)) {
            writer.append("ID,Nom,Budget Total,Depenses,Solde\n");
            for (Projet projet : projets) {
                double depenses = projet.getListeTaches().stream()
                        .mapToDouble(t -> t.getPriorite() * 100) // Exemple : Priorité * 100 = coût de la tâche
                        .sum();
                double solde = projet.getBudget() - depenses;
                writer.append(projet.getId() + ",")
                        .append(projet.getNom() + ",")
                        .append(projet.getBudget() + ",")
                        .append(depenses + ",")
                        .append(solde + "\n");
            }
            System.out.println("Rapport de budget généré avec succès !");
        } catch (IOException e) {
            System.out.println("Erreur lors de la génération du rapport de budget : " + e.getMessage());
        }
    }


}

