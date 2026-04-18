package com.ticket.repository;

import com.ticket.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public User login(String username, String password) {
        String sql = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("ID"),
                        rs.getString("USERNAME"),
                        rs.getString("FULL_NAME"),
                        rs.getString("ROLE")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addUser(String username, String password, String fullName, String role) {
        String sql = "INSERT INTO USERS (USERNAME, PASSWORD, FULL_NAME, ROLE) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, role);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateUser(int id, String fullName, String role) {
        String sql = "UPDATE USERS SET FULL_NAME = ?, ROLE = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, role);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS ORDER BY CREATED_AT DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("ID"),
                    rs.getString("USERNAME"),
                    rs.getString("FULL_NAME"),
                    rs.getString("ROLE")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }
}
