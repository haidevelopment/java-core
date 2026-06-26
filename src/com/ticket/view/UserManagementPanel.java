package com.ticket.view;

import com.ticket.model.Account;
import com.ticket.repository.UserRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private UserRepository userRepo;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtKeyword;
    private JComboBox<String> cbRole;
    private JLabel lblStats;
    private List<Account> allUsersCache;

    public UserManagementPanel() {
        this.userRepo = new UserRepository();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(28, 32, 28, 32));

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel lblTitle = new JLabel("User Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnAdd = createStyledButton("Add User", new Color(34, 197, 94));
        btnAdd.addActionListener(e -> addNewUser());
        headerPanel.add(btnAdd, BorderLayout.EAST);

        mainContent.add(headerPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Filter bar
        mainContent.add(createFilterBar());
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // Table
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(tableContainer);

        JScrollPane outerScroll = new JScrollPane(mainContent);
        outerScroll.setBorder(null);
        outerScroll.getVerticalScrollBar().setUnitIncrement(16);
        outerScroll.setBackground(new Color(245, 247, 250));
        outerScroll.getViewport().setBackground(new Color(245, 247, 250));
        add(outerScroll, BorderLayout.CENTER);
    }

    private JPanel createFilterBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(0, 0, 0, 8));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(16, 20, 16, 20));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel filterGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterGroup.setOpaque(false);

        JLabel lblKw = new JLabel("🔍");
        lblKw.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterGroup.add(lblKw);

        txtKeyword = new JTextField(16);
        txtKeyword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtKeyword.setPreferredSize(new Dimension(180, 36));
        txtKeyword.putClientProperty("JTextField.placeholderText", "Username hoặc tên...");
        filterGroup.add(txtKeyword);

        filterGroup.add(new JLabel("Role:"));
        cbRole = new JComboBox<>(new String[]{"[Tất cả]", "ADMIN", "STAFF"});
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbRole.setPreferredSize(new Dimension(120, 36));
        cbRole.setBackground(Color.WHITE);
        filterGroup.add(cbRole);

        bar.add(filterGroup, BorderLayout.WEST);

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionGroup.setOpaque(false);

        lblStats = new JLabel("");
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStats.setForeground(new Color(100, 116, 139));
        actionGroup.add(lblStats);

        JButton btnSearch = createFilterButton("Lọc", new Color(59, 130, 246));
        btnSearch.addActionListener(e -> applyFilter());
        JButton btnClear = createFilterButton("Xóa lọc", new Color(148, 163, 184));
        btnClear.addActionListener(e -> clearFilter());

        actionGroup.add(btnSearch);
        actionGroup.add(btnClear);
        bar.add(actionGroup, BorderLayout.EAST);

        txtKeyword.addActionListener(e -> applyFilter());
        return bar;
    }

    private JButton createFilterButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = color;
                if (getModel().isPressed()) bg = color.darker();
                else if (getModel().isRollover()) bg = color.brighter();
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(90, 36));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void applyFilter() {
        String kw = txtKeyword.getText().trim();
        String role = (String) cbRole.getSelectedItem();
        List<Account> filtered = userRepo.searchUsers(kw, role);
        renderTable(filtered);
        lblStats.setText(filtered.size() + " / " + (allUsersCache != null ? allUsersCache.size() : 0) + " users");
    }

    private void clearFilter() {
        txtKeyword.setText("");
        cbRole.setSelectedItem("[Tất cả]");
        applyFilter();
    }

    private void addNewUser() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        UserDialog dialog = new UserDialog((Frame) owner, "Add New User", null);
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
        Account selected = userRepo.getAllUsers().stream().filter(u -> u.getId() == id).findFirst().orElse(null);
        if (selected != null) {
            Window owner = SwingUtilities.getWindowAncestor(this);
            UserDialog dialog = new UserDialog((Frame) owner, "Edit User", selected);
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
        allUsersCache = userRepo.getAllUsers();
        applyFilter();
    }

    private void renderTable(List<Account> users) {
        tableModel.setRowCount(0);
        for (Account u : users) {
            tableModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getFullName(), u.getRole()});
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
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(46);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(235, 245, 251));
    }
}