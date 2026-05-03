package com.ticket.view;

import com.ticket.model.Trip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TripDetailDialog extends JDialog {

    public TripDetailDialog(Frame owner, Trip trip) {
        super(owner, "Trip Details", true);
        setSize(500, 560);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("TRIP DETAILS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblRoute = new JLabel(trip.getStartLocation() + "  →  " + trip.getEndLocation());
        lblRoute.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRoute.setForeground(new Color(174, 214, 241));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblRoute, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        contentPanel.add(createSection("TRIP INFORMATION"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createRow("Trip ID", String.valueOf(trip.getId())));
        contentPanel.add(createRow("Trip Name", trip.getTripName()));
        contentPanel.add(createRow("Departure Time",
                trip.getDepartureTime() != null
                        ? trip.getDepartureTime().toString().substring(0, 16)
                        : "N/A"));

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createSection("ROUTE"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createRow("From", trip.getStartLocation()));
        contentPanel.add(createRow("To", trip.getEndLocation()));

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createSection("SEAT & PRICE"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createRow("Base Price", String.format("$%.2f", trip.getBasePrice())));
        contentPanel.add(createRow("Total Seats", String.valueOf(trip.getTotalSeats())));

        int booked = trip.getTotalSeats() - trip.getAvailableSeats();
        contentPanel.add(createRow("Booked Seats", String.valueOf(booked)));
        contentPanel.add(createRow("Available Seats", String.valueOf(trip.getAvailableSeats()),
                trip.getAvailableSeats() > 0 ? new Color(39, 174, 96) : new Color(231, 76, 60)));

        // Seat progress bar
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(createSeatBar(trip.getTotalSeats(), trip.getAvailableSeats()));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        footerPanel.setBackground(new Color(248, 249, 250));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(41, 128, 185));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 36));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        footerPanel.add(btnClose);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createSection(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(149, 165, 166));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(236, 240, 241));

        panel.add(lbl, BorderLayout.WEST);
        panel.add(sep, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRow(String label, String value) {
        return createRow(label, value, new Color(52, 73, 94));
    }

    private JPanel createRow(String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblKey = new JLabel(label);
        lblKey.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblKey.setForeground(new Color(127, 140, 141));
        lblKey.setPreferredSize(new Dimension(140, 20));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVal.setForeground(valueColor);

        row.add(lblKey, BorderLayout.WEST);
        row.add(lblVal, BorderLayout.CENTER);
        return row;
    }

    private JPanel createSeatBar(int total, int available) {
        int booked = total - available;
        int percent = (total > 0) ? (int) ((booked * 100.0) / total) : 0;

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel lbl = new JLabel("Occupancy: " + percent + "%");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(127, 140, 141));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(percent);
        bar.setStringPainted(false);
        bar.setPreferredSize(new Dimension(0, 10));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        bar.setBackground(new Color(236, 240, 241));
        bar.setForeground(percent >= 90 ? new Color(231, 76, 60)
                : percent >= 60 ? new Color(241, 196, 15)
                : new Color(39, 174, 96));
        bar.setBorderPainted(false);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(lbl);
        wrapper.add(Box.createVerticalStrut(5));
        wrapper.add(bar);
        return wrapper;
    }
}
