package com.ticket.view;

import com.ticket.model.Customer;
import com.ticket.repository.CustomerRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CustomerDialog extends JDialog {
    private JTextField txtFullName, txtPhoneNumber, txtEmail;
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private Customer customer;

    public CustomerDialog(Frame owner, String title, Customer customer) {
        super(owner, title, true);
        this.customer = customer;
        
        setSize(400, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        
        initComponents();
        if (customer != null) {
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
        gbc.gridx = 0;

        // Full Name
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Full Name:"), gbc);
        txtFullName = new JTextField();
        styleComponent(txtFullName);
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(txtFullName, gbc);

        // Phone Number
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Phone Number:"), gbc);
        txtPhoneNumber = new JTextField();
        styleComponent(txtPhoneNumber);
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(txtPhoneNumber, gbc);

        // Email
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Email:"), gbc);
        txtEmail = new JTextField();
        styleComponent(txtEmail);
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(txtEmail, gbc);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        actionPanel.setBackground(new Color(248, 249, 250));
        
        btnSave = new JButton("Save");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        
        btnCancel = new JButton("Cancel");
        btnCancel.setFocusPainted(false);
        
        actionPanel.add(btnCancel);
        actionPanel.add(btnSave);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please enter both Name and Phone number!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
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
        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 35));
    }

    private void populateFields() {
        txtFullName.setText(customer.getFullName());
        txtPhoneNumber.setText(customer.getPhoneNumber());
        txtEmail.setText(customer.getEmail() != null ? customer.getEmail() : "");
    }

    private boolean validateInput() {
        return !txtFullName.getText().trim().isEmpty() && !txtPhoneNumber.getText().trim().isEmpty();
    }

    public boolean isConfirmed() { return confirmed; }
    public String getFullName() { return txtFullName.getText().trim(); }
    public String getPhoneNumber() { return txtPhoneNumber.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
}
