package com.ticket.view;

import com.ticket.model.Trip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;

public class TripDialog extends JDialog {
    private JTextField txtName, txtFrom, txtTo, txtPrice, txtTotalSeats, txtAvailableSeats;
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private Trip trip;

    public TripDialog(Frame owner, String title, Trip trip) {
        super(owner, title, true);
        this.trip = trip;
        
        setSize(400, 600); 
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        
        initComponents();
        if (trip != null) {
            populateFields();
        }
    }

    private void initComponents() {
        JPanel mainFormPanel = new JPanel();
        mainFormPanel.setLayout(new BoxLayout(mainFormPanel, BoxLayout.Y_AXIS));
        mainFormPanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        mainFormPanel.setBackground(Color.WHITE);

        txtName = createField(mainFormPanel, "Trip Name:");
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        txtFrom = createField(mainFormPanel, "From:");
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        txtTo = createField(mainFormPanel, "To:");
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        txtPrice = createField(mainFormPanel, "Price ($):");
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        txtTotalSeats = createField(mainFormPanel, "Total Seats:");
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        txtAvailableSeats = createField(mainFormPanel, "Available Seats:");

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        actionPanel.setBackground(new Color(248, 249, 250));
        
        btnCancel = new JButton("Cancel");
        btnSave = new JButton("Save Data");
        
        actionPanel.add(btnCancel);
        actionPanel.add(btnSave);

        add(new JScrollPane(mainFormPanel), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Events
        btnSave.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });
        btnCancel.addActionListener(e -> dispose());
    }

    private boolean validateInput() {
        try {
            if (txtName.getText().trim().isEmpty()) throw new Exception("Name required");
            Double.parseDouble(txtPrice.getText());
            Integer.parseInt(txtTotalSeats.getText());
            Integer.parseInt(txtAvailableSeats.getText());
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please check your input data!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private JTextField createField(JPanel panel, String labelText) {
        JPanel fieldGroup = new JPanel(new BorderLayout(5, 5));
        fieldGroup.setOpaque(false);
        fieldGroup.setMaximumSize(new Dimension(1000, 65));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 35));
        
        fieldGroup.add(label, BorderLayout.NORTH);
        fieldGroup.add(field, BorderLayout.CENTER);
        panel.add(fieldGroup);
        return field;
    }

    private void populateFields() {
        txtName.setText(trip.getTripName());
        txtFrom.setText(trip.getStartLocation());
        txtTo.setText(trip.getEndLocation());
        txtPrice.setText(String.valueOf(trip.getBasePrice()));
        txtTotalSeats.setText(String.valueOf(trip.getTotalSeats()));
        txtAvailableSeats.setText(String.valueOf(trip.getAvailableSeats()));
    }

    public Trip getTripData() {
        if (!confirmed) return null;
        
        int id = (trip != null) ? trip.getId() : 0;
        String name = txtName.getText();
        String from = txtFrom.getText();
        String to = txtTo.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int total = Integer.parseInt(txtTotalSeats.getText());
        int avail = Integer.parseInt(txtAvailableSeats.getText());
        
        return new Trip(id, name, from, to, (trip != null ? trip.getDepartureTime() : new Timestamp(System.currentTimeMillis())), price, total, avail);
    }
}
