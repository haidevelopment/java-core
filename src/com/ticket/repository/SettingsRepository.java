package com.ticket.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SettingsRepository {

    public Map<String, String> getAllSettings() {
        Map<String, String> settings = new HashMap<>();
        String sql = "SELECT KEY_NAME, VALUE FROM SETTINGS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                settings.put(rs.getString("KEY_NAME"), rs.getString("VALUE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return settings;
    }

    public boolean saveSetting(String key, String value) {
        String checkSql = "SELECT COUNT(*) FROM SETTINGS WHERE KEY_NAME = ?";
        String updateSql = "UPDATE SETTINGS SET VALUE = ? WHERE KEY_NAME = ?";
        String insertSql = "INSERT INTO SETTINGS (KEY_NAME, VALUE) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, key);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
            }

            if (exists) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, value);
                    updateStmt.setString(2, key);
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, key);
                    insertStmt.setString(2, value);
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveAllSettings(Map<String, String> settings) {
        boolean success = true;
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            if (!saveSetting(entry.getKey(), entry.getValue())) {
                success = false;
            }
        }
        return success;
    }
}
