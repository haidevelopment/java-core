package com.ticket.view;

import com.ticket.model.Account;
import com.ticket.repository.UserRepository;
import com.ticket.util.AppSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private final Account currentUser;
    private final UserRepository userRepo = new UserRepository();

    private JTextField txtCompanyName;
    private JTextField txtHotline;
    private JTextArea txtFooter;
    private JCheckBox chkSmsEnabled;
    private JComboBox<String> cbSmsProvider;
    private JTextField txtSmsWebhookUrl;
    private JPasswordField txtSmsApiKey;
    private JPasswordField txtOldPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    public SettingsPanel(Account account) {
        this.currentUser = account;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(25, 30, 25, 30));
        initComponents();
        loadSettings();
    }

    private void initComponents() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(240, 242, 245));

        JLabel lblTitle = new JLabel("System settings");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(lblTitle);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(createCompanyPanel());
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(createWebSmsPanel());
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(createPasswordPanel());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createCompanyPanel() {
        JPanel wrapper = createCardWrapper("Thong tin cong ty tren ve");
        JPanel card = (JPanel) wrapper.getComponent(1);
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        txtCompanyName = new JTextField(30);
        txtHotline = new JTextField(30);
        txtFooter = new JTextArea(3, 30);
        txtFooter.setLineWrap(true);
        txtFooter.setWrapStyleWord(true);

        gbc.gridy = 0;
        card.add(createField("Company Name", txtCompanyName), gbc);
        gbc.gridy = 1;
        card.add(createField("Hotline", txtHotline), gbc);
        gbc.gridy = 2;
        card.add(createField("Ticket Footer Message", new JScrollPane(txtFooter)), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(16, 0, 0, 0);
        JButton btnSave = createButton("Save Setting", new Color(46, 204, 113));
        btnSave.addActionListener(e -> saveCompanySettings());
        card.add(btnSave, gbc);

        return wrapper;
    }

    private JPanel createWebSmsPanel() {
        JPanel wrapper = createCardWrapper("Web e-ticket & SMS");
        JPanel card = (JPanel) wrapper.getComponent(1);
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        chkSmsEnabled = new JCheckBox("Send SMS when booking success");
        cbSmsProvider = new JComboBox<>(new String[]{"console", "webhook"});
        txtSmsWebhookUrl = new JTextField(30);
        txtSmsApiKey = new JPasswordField(30);

        gbc.gridy = 0;
        card.add(chkSmsEnabled, gbc);
        gbc.gridy = 1;
        card.add(createField("SMS Provider (console/webhook)", cbSmsProvider), gbc);
        gbc.gridy = 2;
        card.add(createField("Webhook SMS URL", txtSmsWebhookUrl), gbc);
        gbc.gridy = 3;
        card.add(createField("SMS API Key", txtSmsApiKey), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(16, 0, 0, 0);
        JButton btnSave = createButton("Save web & SMS", new Color(52, 152, 219));
        btnSave.addActionListener(e -> saveWebSmsSettings());
        card.add(btnSave, gbc);

        return wrapper;
    }

    private JPanel createPasswordPanel() {
        JPanel wrapper = createCardWrapper("Change password");
        JPanel card = (JPanel) wrapper.getComponent(1);
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        txtOldPassword = new JPasswordField(30);
        txtNewPassword = new JPasswordField(30);
        txtConfirmPassword = new JPasswordField(30);

        gbc.gridy = 0;
        card.add(createField("Current password", txtOldPassword), gbc);
        gbc.gridy = 1;
        card.add(createField("New password", txtNewPassword), gbc);
        gbc.gridy = 2;
        card.add(createField("Confirm new password", txtConfirmPassword), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(16, 0, 0, 0);
        JButton btnChange = createButton("Change password", new Color(41, 128, 185));
        btnChange.addActionListener(e -> changePassword());
        card.add(btnChange, gbc);
 
        return wrapper;
    }

    private JPanel createCardWrapper(String title) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(2000, 420));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(0, 5, 10, 0));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 25, 25));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(lblTitle);
        wrapper.add(card);
        return wrapper;
    }

    private JPanel createField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 90, 100));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setMaximumSize(new Dimension(160, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadSettings() {
        AppSettings.load();
        txtCompanyName.setText(AppSettings.getCompanyName());
        txtHotline.setText(AppSettings.getCompanyHotline());
        txtFooter.setText(AppSettings.getTicketFooter());
        chkSmsEnabled.setSelected(AppSettings.isSmsEnabled());
        cbSmsProvider.setSelectedItem(AppSettings.getSmsProvider());
        txtSmsWebhookUrl.setText(AppSettings.getSmsWebhookUrl());
        txtSmsApiKey.setText(AppSettings.getSmsApiKey());
    }

    private void saveCompanySettings() {
        if (txtCompanyName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your company name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AppSettings.setCompanyName(txtCompanyName.getText());
        AppSettings.setCompanyHotline(txtHotline.getText());
        AppSettings.setTicketFooter(txtFooter.getText());
        AppSettings.save();
        JOptionPane.showMessageDialog(this, "Saved.");
    }

    private void saveWebSmsSettings() {
        AppSettings.setSmsEnabled(chkSmsEnabled.isSelected());
        AppSettings.setSmsProvider((String) cbSmsProvider.getSelectedItem());
        AppSettings.setSmsWebhookUrl(txtSmsWebhookUrl.getText());
        AppSettings.setSmsApiKey(new String(txtSmsApiKey.getPassword()));
        AppSettings.save();
        JOptionPane.showMessageDialog(this, "Saved SMS settings.");
    }

    private void changePassword() {
        String oldPassword = new String(txtOldPassword.getPassword());
        String newPassword = new String(txtNewPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be 6 or more characters long.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Confirmation password does not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!userRepo.verifyPassword(currentUser.getId(), oldPassword)) {
            JOptionPane.showMessageDialog(this, "Mat khau cu khong dung.", "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userRepo.updatePassword(currentUser.getId(), newPassword)) {
            txtOldPassword.setText("");
            txtNewPassword.setText("");
            txtConfirmPassword.setText("");
            JOptionPane.showMessageDialog(this, "Password changed successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Passworld change failed.", "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
