package com.monapp.dao;
import com.monapp.model.Employe;
import com.monapp.database.DatabaseConnection;
import com.monapp.model.Projet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeDAO {

    public List<Employe> getAllEmployes() {
        String query = "SELECT * FROM Employe";
        List<Employe> employes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Employe employe = new Employe();
                employe.setId(rs.getInt("id"));
                employe.setNom(rs.getString("nom"));
                employe.setPrenom(rs.getString("prenom"));
                employe.setRole(rs.getString("role"));
                employes.add(employe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employes;
    }

    public List<Employe> getEmployesPourUnProjet(int projetId) {
        String query = "SELECT e.id, e.nom, e.prenom, e.role FROM Employe e INNER JOIN Employe_Projet ep ON e.id = ep.employe_id WHERE ep.projet_id = ?";
        List<Employe> employes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Affecter l'ID du projet à la requête
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
            // Gérer l'erreur SQL
            System.err.println("Erreur SQL lors du chargement des employés pour le projet ID : " + projetId);
            e.printStackTrace();
        }
        return employes;
    }


    public void addEmploye(Employe employe) {
        String query = "INSERT INTO Employe (nom, prenom, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, employe.getNom());
            pstmt.setString(2, employe.getPrenom());
            pstmt.setString(3, employe.getRole());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmploye(Employe employe) {
        String query = "UPDATE Employe SET nom = ?, prenom = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, employe.getNom());
            pstmt.setString(2, employe.getPrenom());
            pstmt.setString(3, employe.getRole());
            pstmt.setInt(4, employe.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmploye(int id) {
        String query = "DELETE FROM Employe WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Projet> getProjetsParEmploye(int employeId) {
        List<Projet> projets = new ArrayList<>();
        String query = "SELECT p.* FROM Projet p "
                + "INNER JOIN Employe_Projet ep ON p.id = ep.projet_id "
                + "WHERE ep.employe_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                projets.add(new Projet(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate()

                        ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }

}

