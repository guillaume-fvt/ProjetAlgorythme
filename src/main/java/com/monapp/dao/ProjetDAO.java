package com.monapp.dao;

import com.monapp.database.DatabaseConnection;
import com.monapp.model.Projet;
import com.monapp.model.Employe;
import com.monapp.model.Tache;
import com.monapp.model.StatutTache;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAO {

    // Ajouter un projet dans la base de données
    public void ajouterProjet(Projet projet) {
        String query = "INSERT INTO Projet (nom, date_debut, date_fin) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, projet.getNom());
            pstmt.setDate(2, Date.valueOf(projet.getDateDebut()));
            pstmt.setDate(3, Date.valueOf(projet.getDateFin()));
            pstmt.executeUpdate();

            // Récupérer l'ID généré automatiquement
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    projet.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer tous les projets de la base de données
    public List<Projet> getTousLesProjets() {
        String query = "SELECT * FROM Projet";
        List<Projet> projets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Projet projet = new Projet();
                projet.setId(rs.getInt("id"));
                projet.setNom(rs.getString("nom"));
                projet.setDateDebut(rs.getDate("date_debut").toLocalDate());
                projet.setDateFin(rs.getDate("date_fin").toLocalDate());

                // Charger les tâches associées au projet
                projet.setListeTaches(getTachesByProjetId(projet.getId()));

                // Charger les employés associés au projet
                projet.setMembres(getEmployesByProjetId(projet.getId()));

                projets.add(projet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projets;
    }


    // Mettre à jour un projet existant dans la base de données
    public void modifierProjet(Projet projet) {
        String query = "UPDATE Projet SET nom = ?, date_debut = ?, date_fin = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, projet.getNom());
            pstmt.setDate(2, Date.valueOf(projet.getDateDebut()));
            pstmt.setDate(3, Date.valueOf(projet.getDateFin()));
            pstmt.setInt(4, projet.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le projet avec ID " + projet.getId() + " a été mis à jour avec succès.");
            } else {
                System.out.println("Aucun projet trouvé avec ID " + projet.getId() + " pour mise à jour.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du projet avec ID " + projet.getId());
            e.printStackTrace();
        }
    }


    // Récupérer toutes les tâches d'un projet
    public List<Tache> getTachesByProjetId(int projetId) {
        String query = "SELECT * FROM Tache WHERE projet_id = ?";
        List<Tache> taches = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, projetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tache tache = new Tache();
                    tache.setId(rs.getInt("id"));
                    tache.setTitre(rs.getString("titre"));
                    tache.setDescription(rs.getString("description"));
                    tache.setStatut(StatutTache.valueOf(rs.getString("statut")));
                    tache.setPriorite(rs.getBoolean("priorite") ? 1 : 0);
                    tache.setDateLimite(rs.getDate("date_limite").toLocalDate());
                    taches.add(tache);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return taches;
    }

    // Récupérer tous les employés affectés à un projet
    public List<Employe> getEmployesByProjetId(int projetId) {
        String query = "SELECT e.* FROM Employe e " +
                "JOIN Employe_Projet ep ON e.id = ep.employe_id " +
                "WHERE ep.projet_id = ?";
        List<Employe> employes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, projetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Employe employe = new Employe();
                    employe.setId(rs.getInt("id"));
                    employe.setNom(rs.getString("nom"));
                    employe.setPrenom(rs.getString("prenom"));
                    employe.setRole(rs.getString("role"));
                    employes.add(employe);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employes;
    }

    public void ajouterEmployeAuProjet(int employeId, int projetId) {
        String query = "INSERT INTO Employe_Projet (employe_id, projet_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeId);
            pstmt.setInt(2, projetId);
            pstmt.executeUpdate();

            System.out.println("Employé " + employeId + " associé au projet " + projetId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void supprimerEmployeAuProjet(int employeId) {
        String query = "DELETE FROM Employe_Projet WHERE (employe_Id=?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeId);
            pstmt.executeUpdate();

            System.out.println("Employé " + employeId + " supprimé au projet ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un projet et ses relations
    public void supprimerProjet(int id) {
        String query = "DELETE FROM Projet WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Mettre à jour le palier précédent pour un projet
    public boolean mettreAJourPalier(int projetId, int palierActuel) {
        String query = "UPDATE Projet SET palier_precedent = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, palierActuel);
            pstmt.setInt(2, projetId);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0; // Retourne true si au moins une ligne a été mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Retourne false en cas d'erreur
        }
    }


}
