package com.ticket.view;

import com.ticket.model.Booking;
import com.ticket.model.Trip;
import com.ticket.model.Customer;
import com.ticket.repository.TripRepository;
import com.ticket.repository.BookingRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class BookingDialog extends JDialog {
    private JComboBox<Trip> cbTrips;
    private JTextField txtPhone;
    private JLabel lblCustomerInfo;
    private JButton btnSearchPhone, btnCreateCustomer;
    private JTextField txtAmount;
    private JComboBox<String> cbStatus, cbPayment;
    private JSpinner spinSeats;
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private Booking booking;
    private TripRepository tripRepo = new TripRepository();
    private BookingRepository bookingRepo = new BookingRepository();
    private JTextField txtCoupon;
    private JLabel lblDiscount;
    private com.ticket.repository.CouponRepository couponRepo = new com.ticket.repository.CouponRepository();
    private com.ticket.repository.CustomerRepository customerRepo = new com.ticket.repository.CustomerRepository();

    private Customer selectedCustomer = null;

    public BookingDialog(Frame owner, String title, Booking booking) {
        super(owner, title, true);
        this.booking = booking;

        setSize(550, 650);
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

        // Trip Selection
        mainPanel.add(createLabel("Select Trip:"), gbc);
        cbTrips = new JComboBox<>();
        styleComponent(cbTrips);
        cbTrips.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Trip) {
                    value = ((Trip) value).getTripName() + " (" + String.format("%,.0f VNĐ", ((Trip) value).getBasePrice()) + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(cbTrips, gbc);

        cbTrips.addActionListener(e -> updateAmount());

        // Customer Section
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Customer (Search by Phone):"), gbc);
        JPanel customerPanel = new JPanel(new BorderLayout(5, 0));
        customerPanel.setBackground(Color.WHITE);
        txtPhone = new JTextField();
        styleComponent(txtPhone);
        btnSearchPhone = new JButton("Search");
        btnSearchPhone.setBackground(new Color(52, 152, 219));
        btnSearchPhone.setForeground(Color.WHITE);
        btnSearchPhone.setFocusPainted(false);
        customerPanel.add(txtPhone, BorderLayout.CENTER);
        customerPanel.add(btnSearchPhone, BorderLayout.EAST);
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(customerPanel, gbc);

        JPanel customerInfoPanel = new JPanel(new BorderLayout(5, 0));
        customerInfoPanel.setBackground(Color.WHITE);
        lblCustomerInfo = new JLabel("No customer selected");
        lblCustomerInfo.setForeground(Color.RED);
        btnCreateCustomer = new JButton("+ Create New");
        btnCreateCustomer.setVisible(false);
        customerInfoPanel.add(lblCustomerInfo, BorderLayout.CENTER);
        customerInfoPanel.add(btnCreateCustomer, BorderLayout.EAST);
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(customerInfoPanel, gbc);

        // Search action
        btnSearchPhone.addActionListener(e -> {
            String phone = txtPhone.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter phone number!");
                return;
            }
            Customer user = customerRepo.findByPhone(phone);
            if (user != null) {
                selectedCustomer = user;
                lblCustomerInfo.setText("✅ " + user.getFullName() + " - " + user.getPhoneNumber());
                lblCustomerInfo.setForeground(new Color(46, 204, 113));
                btnCreateCustomer.setVisible(false);
            } else {
                selectedCustomer = null;
                lblCustomerInfo.setText("❌ Not found!");
                lblCustomerInfo.setForeground(Color.RED);
                btnCreateCustomer.setVisible(true);
            }
        });

        // Create new customer action
        btnCreateCustomer.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            CustomerDialog cd = new CustomerDialog((Frame)owner, "Add New Customer", null);
            cd.setVisible(true);
            
            if (cd.isConfirmed()) {
                int newId = customerRepo.addCustomer(cd.getFullName(), cd.getPhoneNumber(), cd.getEmail());
                if (newId > 0) {
                    JOptionPane.showMessageDialog(this, "Customer created successfully!");
                    txtPhone.setText(cd.getPhoneNumber());
                    btnSearchPhone.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Error creating customer! Phone number might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // Total Seats
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Seats:"), gbc);
        spinSeats = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        styleComponent(spinSeats);
        spinSeats.addChangeListener(e -> updateAmount());
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(spinSeats, gbc);

        // Amount
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Total Amount (VNĐ):"), gbc);
        txtAmount = new JTextField();
        styleComponent(txtAmount);
        txtAmount.setEditable(false);
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(txtAmount, gbc);

        // Coupon
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Coupon Code:"), gbc);
        JPanel couponPanel = new JPanel(new BorderLayout(5, 0));
        couponPanel.setBackground(Color.WHITE);
        txtCoupon = new JTextField();
        styleComponent(txtCoupon);
        JButton btnApply = new JButton("Apply");
        btnApply.setBackground(new Color(46, 204, 113));
        btnApply.setForeground(Color.WHITE);
        btnApply.setFocusPainted(false);
        couponPanel.add(txtCoupon, BorderLayout.CENTER);
        couponPanel.add(btnApply, BorderLayout.EAST);
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(couponPanel, gbc);

        lblDiscount = new JLabel("No coupon applied");
        lblDiscount.setForeground(Color.GRAY);
        lblDiscount.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(lblDiscount, gbc);

        btnApply.addActionListener(e -> {
            String code = txtCoupon.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter coupon code!");
                return;
            }
            com.ticket.model.Coupon coupon = couponRepo.findByCode(code);
            if (coupon == null) {
                lblDiscount.setForeground(Color.RED);
                lblDiscount.setText("❌ Invalid or expired coupon!");
                return;
            }
            updateAmount();
        });

        // Status
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Status:"), gbc);
        cbStatus = new JComboBox<>(new String[]{"PENDING", "CONFIRMED", "CANCELLED"});
        styleComponent(cbStatus);
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(cbStatus, gbc);

        // Payment
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
            if (selectedCustomer == null && booking == null) {
                JOptionPane.showMessageDialog(this, "Please search and select a customer!");
                return;
            }
            Trip selectedTrip = (Trip) cbTrips.getSelectedItem();
            int seats = (Integer) spinSeats.getValue();
            if (selectedTrip != null && booking == null && seats > selectedTrip.getAvailableSeats()) {
                JOptionPane.showMessageDialog(this, "Number of seats booked exceeds available seats (" + selectedTrip.getAvailableSeats() + ")!");
                return;
            }
            confirmed = true;
            dispose();
        });
        btnCancel.addActionListener(e -> dispose());
    }

    private void updateAmount() {
        Trip selected = (Trip) cbTrips.getSelectedItem();
        if (selected != null) {
            int seats = (Integer) spinSeats.getValue();
            double amount = selected.getBasePrice() * seats;
            
            String code = txtCoupon.getText().trim();
            if (!code.isEmpty()) {
                com.ticket.model.Coupon coupon = couponRepo.findByCode(code);
                if (coupon != null) {
                    double discount = 0;
                    if (coupon.getDiscountPercent() > 0) {
                        discount = amount * coupon.getDiscountPercent() / 100;
                    } else {
                        discount = coupon.getDiscountAmount();
                    }
                    double newAmount = Math.max(0, amount - discount);
                    txtAmount.setText(String.format("%.0f", newAmount));
                    lblDiscount.setForeground(new Color(46, 204, 113));
                    lblDiscount.setText("✅ Discount " + String.format("%,.0f VNĐ", discount) + " → Final: " + String.format("%,.0f VNĐ", newAmount));
                    return;
                }
            }
            
            txtAmount.setText(String.format("%.0f", amount));
            lblDiscount.setText("No coupon applied");
            lblDiscount.setForeground(Color.GRAY);
        }
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
        // If editing an existing booking, disable some fields
        txtPhone.setEditable(false);
        btnSearchPhone.setEnabled(false);
        cbTrips.setEnabled(false);
        spinSeats.setValue(booking.getTotalSeats());
        spinSeats.setEnabled(false);
        
        txtAmount.setText(String.format("%.0f", booking.getTotalAmount()));
        cbStatus.setSelectedItem(booking.getStatus());
        
        lblCustomerInfo.setText("✅ " + booking.getCustomerName() + " (" + booking.getCustomerPhone() + ")");
        lblCustomerInfo.setForeground(new Color(46, 204, 113));
        
        // Find and select the trip in the combo box
        for (int i = 0; i < cbTrips.getItemCount(); i++) {
            if (cbTrips.getItemAt(i).getTripName().equals(booking.getTripName())) {
                cbTrips.setSelectedIndex(i);
                break;
            }
        }
    }

    public boolean isConfirmed() { return confirmed; }
    public Trip getSelectedTrip() { return (Trip) cbTrips.getSelectedItem(); }
    public Customer getSelectedCustomer() { return selectedCustomer; }
    public int getTotalSeats() { return (Integer) spinSeats.getValue(); }
    public double getAmount() { return Double.parseDouble(txtAmount.getText()); }
    public String getStatus() { return (String) cbStatus.getSelectedItem(); }
    public String getPayment() { return (String) cbPayment.getSelectedItem(); }
}
