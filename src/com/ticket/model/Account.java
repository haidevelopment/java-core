package com.ticket.model;

public class Account {
    private int id;
    private String username;
    private String fullName;
    private String role;
    private String phoneNumber;

    public Account(int id, String username, String fullName, String role, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getPhoneNumber() { return phoneNumber; }
}
