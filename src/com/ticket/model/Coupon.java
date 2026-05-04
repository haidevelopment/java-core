package com.ticket.model;

import java.util.Date;

public class Coupon {
    private int id;
    private String code;
    private double discountPercent;
    private double discountAmount;
    private Date expiredDate;
    private boolean isActive;

    public Coupon() {}

    public Coupon(String code, double discountPercent, double discountAmount, Date expiredDate) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.expiredDate = expiredDate;
        this.isActive = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double d) { this.discountPercent = d; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double d) { this.discountAmount = d; }
    public Date getExpiredDate() { return expiredDate; }
    public void setExpiredDate(Date expiredDate) { this.expiredDate = expiredDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}