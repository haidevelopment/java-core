package com.ticket.model;

import java.sql.Timestamp;

public class Customer {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Timestamp createdAt;

    public Customer(int id, String fullName, String phoneNumber, String email, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
}
