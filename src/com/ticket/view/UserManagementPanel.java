package com.ticket.view;

import com.ticket.model.User;
import com.ticket.repository.UserRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private UserRepository userRepo;
    private JTable table;
    private DefaultTableModel tableModel;

    public UserManagementPanel() {
        this.userRepo = new UserRepository();
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 10, 25, 10));
        
        JLabel lblTitle = new JLabel("User Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(52, 73, 94));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = createStyledButton("Add User", new Color(46, 204, 113));
        JButton btnRefresh = createStyledButton("Refresh", new Color(52, 152, 219));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnRefresh);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Table
        String[] columns = {"ID", "Username", "Full Name", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel);
        styleTable(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editUser();
            }
        });

        btnAdd.addActionListener(e -> addNewUser());
        btnRefresh.addActionListener(e -> loadData());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel tableContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        add(tableContainer, BorderLayout.CENTER);
    }

    private void addNewUser() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        UserDialog dialog = new UserDialog((Frame)owner, "Add New User", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (userRepo.addUser(dialog.getUsername(), dialog.getPassword(), dialog.getFullName(), dialog.getRole())) {
                loadData();
                JOptionPane.showMessageDialog(this, "User added successfully!");
            }
        }
    }

    private void editUser() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) table.getValueAt(row, 0);
        User selected = userRepo.getAllUsers().stream().filter(u -> u.getId() == id).findFirst().orElse(null);

        if (selected != null) {
            Window owner = SwingUtilities.getWindowAncestor(this);
            UserDialog dialog = new UserDialog((Frame)owner, "Edit User", selected);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                if (userRepo.updateUser(selected.getId(), dialog.getFullName(), dialog.getRole())) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "User updated successfully!");
                }
            }
        }
    }


    public void loadData() {
        tableModel.setRowCount(0);
        List<User> users = userRepo.getAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getId(),
                u.getUsername(),
                u.getFullName(),
                u.getRole()
            });
        }
        tableModel.fireTableDataChanged();
    }

    private JButton createStyledButton(String text, Color color) {
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
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
    }
}
