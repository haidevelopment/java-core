package com.ticket.model;

import java.sql.Timestamp;

public class Trip {
    private int id;
    private String tripName;
    private String startLocation;
    private String endLocation;
    private Timestamp departureTime;
    private double basePrice;
    private int totalSeats;
    private int availableSeats;

    public Trip(int id, String tripName, String startLocation, String endLocation, Timestamp departureTime, double basePrice, int totalSeats, int availableSeats) {
        this.id = id;
        this.tripName = tripName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.departureTime = departureTime;
        this.basePrice = basePrice;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getTripName() { return tripName; }
    public String getStartLocation() { return startLocation; }
    public String getEndLocation() { return endLocation; }
    public Timestamp getDepartureTime() { return departureTime; }
    public double getBasePrice() { return basePrice; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
}
