package com.ticket.view;

import com.ticket.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserDialog extends JDialog {
    private JTextField txtUsername, txtFullName;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private User user;

    public UserDialog(Frame owner, String title, User user) {
        super(owner, title, true);
        this.user = user;
        
        setSize(400, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        
        initComponents();
        if (user != null) {
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

        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Username:"), gbc);
        txtUsername = new JTextField();
        styleComponent(txtUsername);
        if (user != null) txtUsername.setEditable(false); // Không cho sửa username
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(txtUsername, gbc);

        if (user == null) {
            gbc.insets = new Insets(0, 0, 5, 0);
            mainPanel.add(createLabel("Password:"), gbc);
            txtPassword = new JPasswordField();
            styleComponent(txtPassword);
            gbc.insets = new Insets(0, 0, 15, 0);
            mainPanel.add(txtPassword, gbc);
        }

        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Full Name:"), gbc);
        txtFullName = new JTextField();
        styleComponent(txtFullName);
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(txtFullName, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(createLabel("Role:"), gbc);
        cbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
        styleComponent(cbRole);
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(cbRole, gbc);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        actionPanel.setBackground(new Color(248, 249, 250));
        btnSave = new JButton("Save User");
        btnCancel = new JButton("Cancel");
        actionPanel.add(btnCancel);
        actionPanel.add(btnSave);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });
        btnCancel.addActionListener(e -> dispose());
    }

    private void populateFields() {
        txtUsername.setText(user.getUsername());
        txtFullName.setText(user.getFullName());
        cbRole.setSelectedItem(user.getRole());
    }

    private boolean validateInput() {
        if (txtUsername.getText().trim().isEmpty()) return false;
        if (user == null && new String(txtPassword.getPassword()).isEmpty()) return false;
        if (txtFullName.getText().trim().isEmpty()) return false;
        return true;
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

    public boolean isConfirmed() { return confirmed; }
    public String getUsername() { return txtUsername.getText(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getFullName() { return txtFullName.getText(); }
    public String getRole() { return (String) cbRole.getSelectedItem(); }
}
