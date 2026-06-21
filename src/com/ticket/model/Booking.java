package com.ticket.model;

import java.sql.Timestamp;

public class Booking {
    private int id;
    private String bookingCode;
    private String customerName;
    private String customerPhone;
    private String createdByName;
    private String tripName;
    private Timestamp bookingDate;
    private int totalSeats;
    private double totalAmount;
    private String status;
    private String paymentMethod;

    public Booking(int id, String bookingCode, String customerName, String customerPhone,
                   String createdByName, String tripName, Timestamp bookingDate,
                   int totalSeats, double totalAmount, String status, String paymentMethod) {
        this.id = id;
        this.bookingCode = bookingCode;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.createdByName = createdByName;
        this.tripName = tripName;
        this.bookingDate = bookingDate;
        this.totalSeats = totalSeats;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    // Getters
    public int getId() { return id; }
    public String getBookingCode() { return bookingCode; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCreatedByName() { return createdByName; }
    public String getTripName() { return tripName; }
    public Timestamp getBookingDate() { return bookingDate; }
    public int getTotalSeats() { return totalSeats; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
}
