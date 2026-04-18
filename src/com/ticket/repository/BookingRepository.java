package com.ticket.repository;

import com.ticket.model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    public List<Booking> getRecentBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.ID, b.BOOKING_CODE, u.FULL_NAME, t.TRIP_NAME, b.BOOKING_DATE, b.TOTAL_AMOUNT, b.STATUS " +
                     "FROM BOOKINGS b " +
                     "JOIN USERS u ON b.USER_ID = u.ID " +
                     "JOIN TRIPS t ON b.TRIP_ID = t.ID " +
                     "ORDER BY b.BOOKING_DATE DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("ID"),
                    rs.getString("BOOKING_CODE"),
                    rs.getString("FULL_NAME"),
                    rs.getString("TRIP_NAME"),
                    rs.getTimestamp("BOOKING_DATE"),
                    rs.getDouble("TOTAL_AMOUNT"),
                    rs.getString("STATUS")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean addBooking(int userId, int tripId, String code, double amount, String status, String payment) {
        String sql = "INSERT INTO BOOKINGS (BOOKING_CODE, USER_ID, TRIP_ID, TOTAL_AMOUNT, STATUS, PAYMENT_METHOD) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, tripId);
            pstmt.setDouble(4, amount);
            pstmt.setString(5, status);
            pstmt.setString(6, payment);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateBookingStatus(int id, String status) {
        String sql = "UPDATE BOOKINGS SET STATUS = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteBooking(int id) {
        String sql = "DELETE FROM BOOKINGS WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(TOTAL_AMOUNT) FROM BOOKINGS WHERE STATUS = 'CONFIRMED'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
