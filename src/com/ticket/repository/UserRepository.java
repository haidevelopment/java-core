package com.ticket.repository;

import com.ticket.model.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public Account login(String username, String password) {
        String sql = "SELECT * FROM ACCOUNTS WHERE USERNAME = ? AND PASSWORD = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getInt("ID"),
                            rs.getString("USERNAME"),
                            rs.getString("FULL_NAME"),
                            rs.getString("ROLE"),
                            rs.getString("PHONE_NUMBER"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addUser(String username, String password, String fullName, String role) {
        String sql = "INSERT INTO ACCOUNTS (USERNAME, PASSWORD, FULL_NAME, ROLE) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, role);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(int id, String fullName, String role) {
        String sql = "UPDATE ACCOUNTS SET FULL_NAME = ?, ROLE = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, role);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Account> getAllUsers() {
        List<Account> users = new ArrayList<>();
        String sql = "SELECT * FROM ACCOUNTS ORDER BY CREATED_AT DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new Account(
                        rs.getInt("ID"),
                        rs.getString("USERNAME"),
                        rs.getString("FULL_NAME"),
                        rs.getString("ROLE"),
                        rs.getString("PHONE_NUMBER")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean verifyPassword(int userId, String password) {
        String sql = "SELECT 1 FROM ACCOUNTS WHERE ID = ? AND PASSWORD = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Account> searchUsers(String keyword, String role) {
        List<Account> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM ACCOUNTS WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (UPPER(USERNAME) LIKE UPPER(?) OR UPPER(FULL_NAME) LIKE UPPER(?))");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }
        if (role != null && !role.isBlank() && !"[Tất cả]".equals(role)) {
            sql.append(" AND ROLE = ?");
            params.add(role);
        }
        sql.append(" ORDER BY CREATED_AT DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new Account(
                            rs.getInt("ID"),
                            rs.getString("USERNAME"),
                            rs.getString("FULL_NAME"),
                            rs.getString("ROLE"),
                            rs.getString("PHONE_NUMBER")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE ACCOUNTS SET PASSWORD = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
