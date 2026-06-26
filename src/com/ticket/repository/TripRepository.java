package com.ticket.repository;

import com.ticket.model.Trip;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripRepository {
    public List<Trip> getAllTrips() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM TRIPS ORDER BY DEPARTURE_TIME DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                trips.add(new Trip(
                        rs.getInt("ID"),
                        rs.getString("TRIP_NAME"),
                        rs.getString("START_LOCATION"),
                        rs.getString("END_LOCATION"),
                        rs.getTimestamp("DEPARTURE_TIME"),
                        rs.getDouble("BASE_PRICE"),
                        rs.getInt("TOTAL_SEATS"),
                        rs.getInt("AVAILABLE_SEATS")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    public boolean addTrip(Trip trip) {
        String sql = "INSERT INTO TRIPS (TRIP_NAME, START_LOCATION, END_LOCATION, DEPARTURE_TIME, BASE_PRICE, TOTAL_SEATS, AVAILABLE_SEATS) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trip.getTripName());
            pstmt.setString(2, trip.getStartLocation());
            pstmt.setString(3, trip.getEndLocation());
            pstmt.setTimestamp(4, trip.getDepartureTime());
            pstmt.setDouble(5, trip.getBasePrice());
            pstmt.setInt(6, trip.getTotalSeats());
            pstmt.setInt(7, trip.getAvailableSeats());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrip(Trip trip) {
        String sql = "UPDATE TRIPS SET TRIP_NAME=?, START_LOCATION=?, END_LOCATION=?, DEPARTURE_TIME=?, BASE_PRICE=?, TOTAL_SEATS=?, AVAILABLE_SEATS=? WHERE ID=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trip.getTripName());
            pstmt.setString(2, trip.getStartLocation());
            pstmt.setString(3, trip.getEndLocation());
            pstmt.setTimestamp(4, trip.getDepartureTime());
            pstmt.setDouble(5, trip.getBasePrice());
            pstmt.setInt(6, trip.getTotalSeats());
            pstmt.setInt(7, trip.getAvailableSeats());
            pstmt.setInt(8, trip.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Trip> searchTrips(String keyword, String startLocation, String endLocation) {
        List<Trip> trips = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM TRIPS WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND UPPER(TRIP_NAME) LIKE UPPER(?)");
            params.add("%" + keyword.trim() + "%");
        }
        if (startLocation != null && !startLocation.isBlank()) {
            sql.append(" AND UPPER(START_LOCATION) = UPPER(?)");
            params.add(startLocation.trim());
        }
        if (endLocation != null && !endLocation.isBlank()) {
            sql.append(" AND UPPER(END_LOCATION) = UPPER(?)");
            params.add(endLocation.trim());
        }
        sql.append(" ORDER BY DEPARTURE_TIME DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    trips.add(new Trip(
                            rs.getInt("ID"),
                            rs.getString("TRIP_NAME"),
                            rs.getString("START_LOCATION"),
                            rs.getString("END_LOCATION"),
                            rs.getTimestamp("DEPARTURE_TIME"),
                            rs.getDouble("BASE_PRICE"),
                            rs.getInt("TOTAL_SEATS"),
                            rs.getInt("AVAILABLE_SEATS")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    public List<String> getDistinctStartLocations() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT START_LOCATION FROM TRIPS ORDER BY START_LOCATION";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String> getDistinctEndLocations() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT END_LOCATION FROM TRIPS ORDER BY END_LOCATION";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteTrip(int id) {
        String sql = "DELETE FROM TRIPS WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
