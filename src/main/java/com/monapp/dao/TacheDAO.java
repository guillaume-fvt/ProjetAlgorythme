package com.monapp.dao;

import com.monapp.database.DatabaseConnection;
import com.monapp.model.Tache;
import com.monapp.model.StatutTache;
import com.monapp.model.Employe;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TacheDAO {

    // Ajouter une nouvelle tâche dans la base de données
    public void ajouterTache(Tache tache) {
        String query = "INSERT INTO Tache (titre, description, statut, priorite, date_limite, employe_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tache.getTitre());
            pstmt.setString(2, tache.getDescription());
            pstmt.setString(3, tache.getStatut().name());
            pstmt.setInt(4, tache.getPriorite());
            pstmt.setDate(5, (tache.getDateLimite() != null) ? Date.valueOf(tache.getDateLimite()) : null);
            pstmt.setObject(6, (tache.getEmployeAssigne() != null) ? tache.getEmployeAssigne().getId() : null);

            pstmt.executeUpdate();

            // Récupérer l'ID généré automatiquement
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tache.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer une tâche par son ID
    public Tache getTacheById(int id) {
        String query = "SELECT t.*, e.nom AS employe_nom, e.prenom AS employe_prenom, e.role AS employe_role " +
                "FROM Tache t " +
                "LEFT JOIN Employe e ON t.employe_id = e.id " +
                "WHERE t.id = ?";
        Tache tache = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tache = mapResultSetToTache(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tache;
    }

    public List<Tache> getToutesLesTaches() {
        String query = "SELECT t.id, t.titre, t.description, t.statut, t.priorite, " +
                "t.date_limite, t.projet_id, e.id AS employe_id, e.nom AS employe_nom " +
                "FROM Tache t " +
                "LEFT JOIN Employe e ON t.employe_id = e.id";

        List<Tache> taches = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Tache tache = new Tache();
                tache.setId(rs.getInt("id"));
                tache.setTitre(rs.getString("titre"));
                tache.setDescription(rs.getString("description"));
                tache.setStatut(StatutTache.valueOf(rs.getString("statut")));
                tache.setPriorite(rs.getBoolean("priorite") ? 1 : 0);
                tache.setDateLimite(rs.getDate("date_limite").toLocalDate());
                tache.setProjetId(rs.getInt("projet_id"));

                int employeId = rs.getInt("employe_id");
                if (employeId != 0) {
                    Employe employe = new Employe();
                    employe.setId(employeId);
                    employe.setNom(rs.getString("employe_nom"));
                    tache.setEmployeAssigne(employe);
                    tache.setProjetId(rs.getInt("projet_id"));
                }

                taches.add(tache);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return taches;
    }

    // Mettre à jour une tâche existante
    public void modifierTache(Tache tache) {
        String query = "UPDATE Tache SET titre = ?, description = ?, statut = ?, priorite = ?, date_limite = ?, employe_id = ? " +
                "WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, tache.getTitre());
            pstmt.setString(2, tache.getDescription());
            pstmt.setString(3, tache.getStatut().name());
            pstmt.setInt(4, tache.getPriorite());
            pstmt.setDate(5, (tache.getDateLimite() != null) ? Date.valueOf(tache.getDateLimite()) : null);
            pstmt.setObject(6, (tache.getEmployeAssigne() != null) ? tache.getEmployeAssigne().getId() : null);
            pstmt.setInt(7, tache.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer une tâche par son ID
    public void supprimerTache(int id) {
        String query = "DELETE FROM Tache WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mapper un ResultSet à une instance de Tache
    private Tache mapResultSetToTache(ResultSet rs) throws SQLException {
        Tache tache = new Tache();
        tache.setId(rs.getInt("id"));
        tache.setTitre(rs.getString("titre"));
        tache.setDescription(rs.getString("description"));
        tache.setStatut(StatutTache.valueOf(rs.getString("statut")));
        tache.setPriorite(rs.getInt("priorite"));
        tache.setDateLimite((rs.getDate("date_limite") != null) ? rs.getDate("date_limite").toLocalDate() : null);

        // Assigner l'employé si présent
        if (rs.getInt("employe_id") != 0) {
            Employe employe = new Employe();
            employe.setId(rs.getInt("employe_id"));
            employe.setNom(rs.getString("employe_nom"));
            employe.setPrenom(rs.getString("employe_prenom"));
            employe.setRole(rs.getString("employe_role"));
            tache.setEmployeAssigne(employe);
        }

        return tache;
    }

    public void assignerTacheAuProjet(int tacheId, int projetId) {
        String query = "UPDATE Tache SET projet_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, projetId);
            pstmt.setInt(2, tacheId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerTacheAuProjet(int tacheId, int projetId) {
        String query = "UPDATE Tache SET projet_id = NULL WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, tacheId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Tache> getTachesParProjets(List<Integer> projetIds) {
        List<Tache> taches = new ArrayList<>();
        if (projetIds.isEmpty()) {
            return taches; // Aucun projet, pas de tâche
        }
        String requete = "SELECT * FROM Tache WHERE projet_id IN ("
                + projetIds.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")AND employe_id IS NULL";

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connexion.prepareStatement(requete)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                taches.add(new Tache(
                        resultSet.getInt("id"),
                        resultSet.getString("titre"),
                        resultSet.getString("description"),
                        StatutTache.valueOf(resultSet.getString("statut")), // Conversion en Enum
                        resultSet.getBoolean("priorite") ? 1 : 0,
                        resultSet.getDate("date_limite").toLocalDate(),
                        null,
                        null
                )); // Ajout de la parenthèse fermante ici
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return taches;
    }

    public List<Tache> recupererTachesAffectees(int employeId) {
        List<Tache> tachesAffectees = new ArrayList<>();
        String requete = " SELECT * FROM tache WHERE employe_id = ?";

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connexion.prepareStatement(requete)) {

            preparedStatement.setInt(1, employeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Tache tache = new Tache();
                    tache.setId(resultSet.getInt("id"));
                    tache.setTitre(resultSet.getString("titre"));
                    tache.setDescription(resultSet.getString("description"));
                    tache.setDateLimite(resultSet.getDate("date_limite").toLocalDate());
                    tache.setStatut(StatutTache.valueOf(resultSet.getString("statut")));
                    tache.setPriorite(resultSet.getBoolean("priorite") ? 1 : 0);
                    tache.setProjetId(resultSet.getInt("projet_id"));

                    tachesAffectees.add(tache);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tachesAffectees;
    }

    public void ajouterEmployeAUneTache(int tacheId, Integer employeId){
        String query = "UPDATE Tache SET employe_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeId);
            pstmt.setInt(2, tacheId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerEmployeAUneTache(int tacheId){
        String query = "UPDATE Tache SET employe_id = NULL WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, tacheId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
