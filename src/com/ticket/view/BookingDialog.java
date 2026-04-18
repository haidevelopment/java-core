package com.ticket.view;

import com.ticket.model.Booking;
import com.ticket.model.Trip;
import com.ticket.repository.TripRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class BookingDialog extends JDialog {
    private JComboBox<Trip> cbTrips;
    private JTextField txtCode, txtAmount;
    private JComboBox<String> cbStatus, cbPayment;
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private Booking booking;
    private TripRepository tripRepo = new TripRepository();

    public BookingDialog(Frame owner, String title, Booking booking) {
        super(owner, title, true);
        this.booking = booking;
        
        setSize(450, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        
        initComponents();
        loadTrips();
        if (booking != null) {
            populateFields();
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;

        mainPanel.add(createLabel("Select Trip:"), gbc);
        cbTrips = new JComboBox<>();
        styleComponent(cbTrips);
        cbTrips.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Trip) {
                    value = ((Trip) value).getTripName() + " ($" + ((Trip) value).getBasePrice() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(cbTrips, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Booking Code:"), gbc);
        txtCode = new JTextField();
        styleComponent(txtCode);
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(txtCode, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Total Amount ($):"), gbc);
        txtAmount = new JTextField();
        styleComponent(txtAmount);
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(txtAmount, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Status:"), gbc);
        cbStatus = new JComboBox<>(new String[]{"PENDING", "CONFIRMED", "CANCELLED"});
        styleComponent(cbStatus);
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(cbStatus, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Payment Method:"), gbc);
        cbPayment = new JComboBox<>(new String[]{"CASH", "CREDIT_CARD", "E-WALLET"});
        styleComponent(cbPayment);
        mainPanel.add(cbPayment, gbc);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        actionPanel.setBackground(new Color(248, 249, 250));
        btnSave = new JButton("Confirm Booking");
        btnCancel = new JButton("Cancel");
        actionPanel.add(btnCancel);
        actionPanel.add(btnSave);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        btnCancel.addActionListener(e -> dispose());
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private void styleComponent(JComponent comp) {
        comp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 38));
    }

    private void loadTrips() {
        List<Trip> trips = tripRepo.getAllTrips();
        for (Trip t : trips) cbTrips.addItem(t);
    }

    private void populateFields() {
        txtCode.setText(booking.getBookingCode());
        txtAmount.setText(String.valueOf(booking.getTotalAmount()));
        cbStatus.setSelectedItem(booking.getStatus());
    }

    public boolean isConfirmed() { return confirmed; }
    public Trip getSelectedTrip() { return (Trip) cbTrips.getSelectedItem(); }
    public String getCode() { return txtCode.getText(); }
    public double getAmount() { return Double.parseDouble(txtAmount.getText()); }
    public String getStatus() { return (String) cbStatus.getSelectedItem(); }
    public String getPayment() { return (String) cbPayment.getSelectedItem(); }
}
