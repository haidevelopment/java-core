package com.ticket.repository;

import com.ticket.model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardRepository {

    public int getTotalBookings(int userId, String role) {
        String sql = "SELECT COUNT(*) FROM BOOKINGS";
        if (!"ADMIN".equalsIgnoreCase(role)) {
            sql += " WHERE USER_ID = ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                pstmt.setInt(1, userId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalRevenue(int userId, String role) {
        String sql = "SELECT SUM(TOTAL_AMOUNT) FROM BOOKINGS WHERE STATUS = 'CONFIRMED'";
        if (!"ADMIN".equalsIgnoreCase(role)) {
            sql += " AND USER_ID = ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                pstmt.setInt(1, userId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> getBookingStatsByStatus(int userId, String role) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT STATUS, COUNT(*) FROM BOOKINGS";
        if (!"ADMIN".equalsIgnoreCase(role)) {
            sql += " WHERE USER_ID = ?";
        }
        sql += " GROUP BY STATUS";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                pstmt.setInt(1, userId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<Booking> getRecentBookings(int userId, String role, int limit) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.ID, b.BOOKING_CODE, u.FULL_NAME, t.TRIP_NAME, b.BOOKING_DATE, b.TOTAL_AMOUNT, b.STATUS, b.PAYMENT_METHOD " +
                     "FROM BOOKINGS b " +
                     "JOIN USERS u ON b.USER_ID = u.ID " +
                     "JOIN TRIPS t ON b.TRIP_ID = t.ID ";
        
        if (!"ADMIN".equalsIgnoreCase(role)) {
            sql += " WHERE b.USER_ID = ?";
        }
        
        sql += " ORDER BY b.BOOKING_DATE DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (!"ADMIN".equalsIgnoreCase(role)) {
                pstmt.setInt(paramIndex++, userId);
            }
            pstmt.setInt(paramIndex, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(new Booking(
                            rs.getInt("ID"),
                            rs.getString("BOOKING_CODE"),
                            rs.getString("FULL_NAME"),
                            rs.getString("TRIP_NAME"),
                            rs.getTimestamp("BOOKING_DATE"),
                            rs.getDouble("TOTAL_AMOUNT"),
                            rs.getString("STATUS"),
                            rs.getString("PAYMENT_METHOD")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM USERS";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalTrips() {
        String sql = "SELECT COUNT(*) FROM TRIPS";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
