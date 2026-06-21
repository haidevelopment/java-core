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
        if (!"ADMIN".equals(role)) {
            sql += " WHERE CREATED_BY = ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!"ADMIN".equals(role)) {
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
        if (!"ADMIN".equals(role)) {
            sql += " AND CREATED_BY = ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!"ADMIN".equals(role)) {
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
        if (!"ADMIN".equals(role)) {
            sql += " WHERE CREATED_BY = ?";
        }
        sql += " GROUP BY STATUS";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!"ADMIN".equals(role)) {
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
        String sql = "SELECT b.ID, b.BOOKING_CODE, u.FULL_NAME, u.PHONE_NUMBER, cr.FULL_NAME as CREATED_BY_NAME, t.TRIP_NAME, b.BOOKING_DATE, b.TOTAL_SEATS, b.TOTAL_AMOUNT, b.STATUS, b.PAYMENT_METHOD " +
                     "FROM BOOKINGS b " +
                     "LEFT JOIN CUSTOMERS u ON b.CUSTOMER_ID = u.ID " +
                     "LEFT JOIN ACCOUNTS cr ON b.CREATED_BY = cr.ID " +
                     "JOIN TRIPS t ON b.TRIP_ID = t.ID ";
        
        if (!"ADMIN".equals(role)) {
            sql += " WHERE b.CREATED_BY = ?";
        }
        
        sql += " ORDER BY b.BOOKING_DATE DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (!"ADMIN".equals(role)) {
                pstmt.setInt(paramIndex++, userId);
            }
            pstmt.setInt(paramIndex, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(new Booking(
                            rs.getInt("ID"),
                            rs.getString("BOOKING_CODE"),
                            rs.getString("FULL_NAME"),
                            rs.getString("PHONE_NUMBER"),
                            rs.getString("CREATED_BY_NAME"),
                            rs.getString("TRIP_NAME"),
                            rs.getTimestamp("BOOKING_DATE"),
                            rs.getInt("TOTAL_SEATS"),
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
        String sql = "SELECT COUNT(*) FROM ACCOUNTS";
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
