package com.ticket.repository;

import com.ticket.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {

    public Customer findByPhone(String phone) {
        String sql = "SELECT * FROM CUSTOMERS WHERE PHONE_NUMBER = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("ID"),
                            rs.getString("FULL_NAME"),
                            rs.getString("PHONE_NUMBER"),
                            rs.getString("EMAIL"),
                            rs.getTimestamp("CREATED_AT")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addCustomer(String fullName, String phone, String email) {
        String sql = "INSERT INTO CUSTOMERS (FULL_NAME, PHONE_NUMBER, EMAIL) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"ID"})) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM CUSTOMERS ORDER BY CREATED_AT DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("ID"),
                        rs.getString("FULL_NAME"),
                        rs.getString("PHONE_NUMBER"),
                        rs.getString("EMAIL"),
                        rs.getTimestamp("CREATED_AT")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public List<Customer> searchCustomers(String keyword) {
        List<Customer> customers = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM CUSTOMERS WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (UPPER(FULL_NAME) LIKE UPPER(?) OR UPPER(PHONE_NUMBER) LIKE UPPER(?) OR UPPER(EMAIL) LIKE UPPER(?))");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY CREATED_AT DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(new Customer(
                            rs.getInt("ID"),
                            rs.getString("FULL_NAME"),
                            rs.getString("PHONE_NUMBER"),
                            rs.getString("EMAIL"),
                            rs.getTimestamp("CREATED_AT")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return customers;
    }
}
