package com.ticket.model;

import java.util.Date;

public class Coupon {
    private int id;
    private String code;
    private Double discountPercent;  // nullable: 1-100 hoac null
    private Double discountAmount;   // nullable: >0 hoac null
    private Date expiredDate;
    private boolean isActive;

    public Coupon() {}

    public Coupon(String code, Double discountPercent, Double discountAmount, Date expiredDate) {
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
    public Double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Double d) { this.discountPercent = d; }
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double d) { this.discountAmount = d; }
    public Date getExpiredDate() { return expiredDate; }
    public void setExpiredDate(Date expiredDate) { this.expiredDate = expiredDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}