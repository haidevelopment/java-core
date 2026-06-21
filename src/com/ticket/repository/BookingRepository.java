package com.ticket.repository;

import com.ticket.model.Booking;
import com.ticket.model.TicketDetail;
import com.ticket.model.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {

    public List<Booking> getRecentBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.ID, b.BOOKING_CODE, " +
                "c.FULL_NAME AS CUSTOMER_NAME, c.PHONE_NUMBER AS CUSTOMER_PHONE, " +
                "cr.FULL_NAME AS CREATED_BY_NAME, " +
                "t.TRIP_NAME, b.BOOKING_DATE, b.TOTAL_SEATS, b.TOTAL_AMOUNT, b.STATUS, b.PAYMENT_METHOD " +
                "FROM BOOKINGS b " +
                "LEFT JOIN CUSTOMERS c ON b.CUSTOMER_ID = c.ID " +
                "LEFT JOIN ACCOUNTS cr ON b.CREATED_BY = cr.ID " +
                "JOIN TRIPS t ON b.TRIP_ID = t.ID " +
                "ORDER BY b.BOOKING_DATE DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("ID"),
                        rs.getString("BOOKING_CODE"),
                        rs.getString("CUSTOMER_NAME"),
                        rs.getString("CUSTOMER_PHONE"),
                        rs.getString("CREATED_BY_NAME"),
                        rs.getString("TRIP_NAME"),
                        rs.getTimestamp("BOOKING_DATE"),
                        rs.getInt("TOTAL_SEATS"),
                        rs.getDouble("TOTAL_AMOUNT"),
                        rs.getString("STATUS"),
                        rs.getString("PAYMENT_METHOD")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Thêm booking mới, trả về ID vừa được sinh ra (để gen booking code).
     */
    public int addBookingReturnId(int createdById, int customerId, int tripId,
                                  int totalSeats, double amount, String status, String payment) {
        String sql = "INSERT INTO BOOKINGS (BOOKING_CODE, CREATED_BY, CUSTOMER_ID, TRIP_ID, TOTAL_SEATS, TOTAL_AMOUNT, STATUS, PAYMENT_METHOD) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String tempCode = "BK-TMP-" + System.currentTimeMillis();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"ID"})) {
            pstmt.setString(1, tempCode);
            pstmt.setInt(2, createdById);
            pstmt.setInt(3, customerId);
            pstmt.setInt(4, tripId);
            pstmt.setInt(5, totalSeats);
            pstmt.setDouble(6, amount);
            pstmt.setString(7, status);
            pstmt.setString(8, payment);
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    updateBookingCode(conn, newId, "BK-" + newId);
                    return newId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void updateBookingCode(Connection conn, int id, String code) throws SQLException {
        String sql = "UPDATE BOOKINGS SET BOOKING_CODE = ? WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    public boolean updateBookingStatus(int id, String status) {
        String sql = "UPDATE BOOKINGS SET STATUS = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBooking(int id) {
        String sql = "DELETE FROM BOOKINGS WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(TOTAL_AMOUNT) FROM BOOKINGS WHERE STATUS = 'CONFIRMED'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }



    public List<TicketDetail> findTicketsByPhone(String phone) {
        List<TicketDetail> tickets = new ArrayList<>();
        String sql = "SELECT b.ID, b.BOOKING_CODE, c.FULL_NAME, c.PHONE_NUMBER, t.TRIP_NAME, " +
                "t.START_LOCATION, t.END_LOCATION, t.DEPARTURE_TIME, b.TOTAL_SEATS, b.BOOKING_DATE, " +
                "b.TOTAL_AMOUNT, b.STATUS, b.PAYMENT_METHOD " +
                "FROM BOOKINGS b " +
                "JOIN CUSTOMERS c ON b.CUSTOMER_ID = c.ID " +
                "JOIN TRIPS t ON b.TRIP_ID = t.ID " +
                "WHERE c.PHONE_NUMBER = ? " +
                "ORDER BY b.BOOKING_DATE DESC " +
                "FETCH FIRST 20 ROWS ONLY";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapTicketDetail(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    private TicketDetail mapTicketDetail(ResultSet rs) throws SQLException {
        return new TicketDetail(
                rs.getInt("ID"),
                rs.getString("BOOKING_CODE"),
                rs.getString("FULL_NAME"),
                rs.getString("PHONE_NUMBER"),
                rs.getString("TRIP_NAME"),
                rs.getString("START_LOCATION"),
                rs.getString("END_LOCATION"),
                rs.getTimestamp("DEPARTURE_TIME"),
                rs.getInt("TOTAL_SEATS"),
                rs.getTimestamp("BOOKING_DATE"),
                rs.getDouble("TOTAL_AMOUNT"),
                rs.getString("STATUS"),
                rs.getString("PAYMENT_METHOD"));
    }

    public TicketDetail findTicketDetailByCode(String bookingCode) {
        String sql = "SELECT b.ID, b.BOOKING_CODE, c.FULL_NAME, c.PHONE_NUMBER, t.TRIP_NAME, " +
                "t.START_LOCATION, t.END_LOCATION, t.DEPARTURE_TIME, b.TOTAL_SEATS, b.BOOKING_DATE, " +
                "b.TOTAL_AMOUNT, b.STATUS, b.PAYMENT_METHOD " +
                "FROM BOOKINGS b " +
                "LEFT JOIN CUSTOMERS c ON b.CUSTOMER_ID = c.ID " +
                "JOIN TRIPS t ON b.TRIP_ID = t.ID " +
                "WHERE UPPER(b.BOOKING_CODE) = UPPER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapTicketDetail(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
