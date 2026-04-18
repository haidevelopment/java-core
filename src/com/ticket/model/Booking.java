package com.ticket.model;

import java.sql.Timestamp;

public class Booking {
    private int id;
    private String bookingCode;
    private String customerName;
    private String tripName;
    private Timestamp bookingDate;
    private double totalAmount;
    private String status;

    public Booking(int id, String bookingCode, String customerName, String tripName, Timestamp bookingDate, double totalAmount, String status) {
        this.id = id;
        this.bookingCode = bookingCode;
        this.customerName = customerName;
        this.tripName = tripName;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getBookingCode() { return bookingCode; }
    public String getCustomerName() { return customerName; }
    public String getTripName() { return tripName; }
    public Timestamp getBookingDate() { return bookingDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
}
