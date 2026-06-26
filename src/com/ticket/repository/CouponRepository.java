package com.ticket.repository;

import com.ticket.model.Coupon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouponRepository {

    public List<Coupon> getAllCoupons() {
        List<Coupon> list = new ArrayList<>();
        String sql = "SELECT * FROM COUPONS";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Coupon c = new Coupon();
                c.setId(rs.getInt("ID"));
                c.setCode(rs.getString("CODE"));
                c.setDiscountPercent(rs.getDouble("DISCOUNT_PERCENT"));
                c.setDiscountAmount(rs.getDouble("DISCOUNT_AMOUNT"));
                c.setExpiredDate(rs.getDate("EXPIRED_DATE"));
                c.setActive(rs.getInt("IS_ACTIVE") == 1);
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void addCoupon(Coupon c) {
        String sql = "INSERT INTO COUPONS (CODE, DISCOUNT_PERCENT, DISCOUNT_AMOUNT, EXPIRED_DATE) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setDouble(2, c.getDiscountPercent());
            ps.setDouble(3, c.getDiscountAmount());
            ps.setDate(4, new java.sql.Date(c.getExpiredDate().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateCoupon(Coupon c) {
        String sql = "UPDATE COUPONS SET CODE=?, DISCOUNT_PERCENT=?, DISCOUNT_AMOUNT=?, EXPIRED_DATE=?, IS_ACTIVE=? WHERE ID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setDouble(2, c.getDiscountPercent());
            ps.setDouble(3, c.getDiscountAmount());
            ps.setDate(4, new java.sql.Date(c.getExpiredDate().getTime()));
            ps.setInt(5, c.isActive() ? 1 : 0);
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteCoupon(int id) {
        String sql = "DELETE FROM COUPONS WHERE ID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Coupon findByCode(String code) {
        String sql = "SELECT * FROM COUPONS WHERE CODE=? AND IS_ACTIVE=1 AND (EXPIRED_DATE IS NULL OR EXPIRED_DATE >= TRUNC(CURRENT_DATE))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Coupon c = new Coupon();
                c.setId(rs.getInt("ID"));
                c.setCode(rs.getString("CODE"));
                c.setDiscountPercent(rs.getDouble("DISCOUNT_PERCENT"));
                c.setDiscountAmount(rs.getDouble("DISCOUNT_AMOUNT"));
                c.setExpiredDate(rs.getDate("EXPIRED_DATE"));
                c.setActive(true);
                return c;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}