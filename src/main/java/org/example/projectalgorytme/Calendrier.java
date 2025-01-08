package org.example.projectalgorytme;

import java.text.SimpleDateFormat;
import java.util.*;

public class Calendrier {
    private List<Tache> taches;

    public Calendrier(List<Tache> taches) {
        this.taches = taches;
    }

    // Affiche un mois sous forme de calendrier avec les tâches
    public void afficherCalendrier(int mois, int annee) {
        // Créer une instance de calendrier
        Calendar cal = Calendar.getInstance();
        cal.set(annee, mois - 1, 1);

        // Nombre de jours dans le mois
        int joursDansMois = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Affichage de l'en-tête du calendrier
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        System.out.println(sdf.format(cal.getTime()));
        System.out.println("Lun Mar Mer Jeu Ven Sam Dim");

        // Décalage du premier jour du mois
        int premierJour = cal.get(Calendar.DAY_OF_WEEK);
        int decallage = premierJour == 1 ? 6 : premierJour - 2;

        // Afficher les jours vides avant le premier jour du mois
        for (int i = 0; i < decallage; i++) {
            System.out.print("   ");
        }

        // Afficher les jours du mois
        for (int jour = 1; jour <= joursDansMois; jour++) {
            // Afficher le jour de la semaine
            System.out.print(String.format("%2d ", jour));

            // Vérifier si des tâches sont prévues pour ce jour
            afficherTachesDuJour(jour, mois, annee);

            // Passer à la ligne suivante après dimanche
            if ((decallage + jour) % 7 == 0) {
                System.out.println();
            }
        }

        System.out.println(); // Saut de ligne après le calendrier
    }

    // Affiche les tâches d'un jour donné
    private void afficherTachesDuJour(int jour, int mois, int annee) {
        for (Tache t : taches) {
            Calendar calTache = Calendar.getInstance();
            calTache.setTime(t.getDateLimite());

            // Si la date de la tâche correspond au jour affiché
            if (calTache.get(Calendar.DAY_OF_MONTH) == jour &&
                    calTache.get(Calendar.MONTH) == mois - 1 &&
                    calTache.get(Calendar.YEAR) == annee) {

                System.out.print("* "); // Marque la présence d'une tâche
            }
        }
    }
}
