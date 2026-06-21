package com.ticket.model;

import java.sql.Timestamp;

public class TicketDetail {
    private int id;
    private String bookingCode;
    private String customerName;
    private String phoneNumber;
    private String tripName;
    private String startLocation;
    private String endLocation;
    private Timestamp departureTime;
    private int totalSeats;
    private Timestamp bookingDate;
    private double totalAmount;
    private String status;
    private String paymentMethod;

    public TicketDetail(int id, String bookingCode, String customerName, String phoneNumber,
                        String tripName, String startLocation, String endLocation,
                        Timestamp departureTime, int totalSeats, Timestamp bookingDate,
                        double totalAmount, String status, String paymentMethod) {
        this.id = id;
        this.bookingCode = bookingCode;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.tripName = tripName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.departureTime = departureTime;
        this.totalSeats = totalSeats;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public int getId() { return id; }
    public String getBookingCode() { return bookingCode; }
    public String getCustomerName() { return customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getTripName() { return tripName; }
    public String getStartLocation() { return startLocation; }
    public String getEndLocation() { return endLocation; }
    public Timestamp getDepartureTime() { return departureTime; }
    public int getTotalSeats() { return totalSeats; }
    public Timestamp getBookingDate() { return bookingDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
}
