package com.ticket.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private final Color primaryBlue = new Color(41, 128, 185);
    private final Color lightBlue = new Color(235, 245, 251);
    private final Color backgroundColor = Color.WHITE;

    public LoginFrame() {
        setTitle("Booking System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(40, 45, 40, 45));

        // Header Section
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(backgroundColor);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel lblTitle = new JLabel("Welcome Back");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(primaryBlue);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Please enter your details to login");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(127, 140, 141));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(lblSubtitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 45)));

        // Form Section
        JPanel formPanel = new JPanel();
        formPanel.setBackground(backgroundColor);
        formPanel.setLayout(new GridLayout(4, 1, 0, 5));

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(new Color(52, 73, 94));
        
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.setBackground(lightBlue);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(lightBlue, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setForeground(new Color(52, 73, 94));
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setBackground(lightBlue);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(lightBlue, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        formPanel.add(lblUsername);
        formPanel.add(txtUsername);
        formPanel.add(lblPassword);
        formPanel.add(txtPassword);

        // Button Section
        btnLogin = new JButton("LOGIN NOW");
        btnLogin.setBackground(primaryBlue);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 50));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(backgroundColor);
        footerPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        footerPanel.add(btnLogin, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    
    public String getUsername() { return txtUsername.getText(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public JButton getLoginButton() { return btnLogin; }
}

