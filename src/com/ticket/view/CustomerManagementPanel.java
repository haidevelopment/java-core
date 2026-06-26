package com.ticket.view;

import com.ticket.model.Customer;
import com.ticket.repository.CustomerRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class CustomerManagementPanel extends JPanel {
    private CustomerRepository customerRepo;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtKeyword;
    private JLabel lblStats;
    private List<Customer> allCustomersCache;

    public CustomerManagementPanel() {
        this.customerRepo = new CustomerRepository();
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

        JLabel lblTitle = new JLabel("Customer Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnAdd = createStyledButton("Add Customer", new Color(34, 197, 94));
        btnAdd.addActionListener(e -> addNewCustomer());
        headerPanel.add(btnAdd, BorderLayout.EAST);

        mainContent.add(headerPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Filter bar
        mainContent.add(createFilterBar());
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // Table
        String[] columns = {"ID", "Full Name", "Phone Number", "Email", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        styleTable(table);

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

        txtKeyword = new JTextField(20);
        txtKeyword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtKeyword.setPreferredSize(new Dimension(240, 36));
        txtKeyword.putClientProperty("JTextField.placeholderText", "Tên, SĐT hoặc Email...");
        filterGroup.add(txtKeyword);

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
        List<Customer> filtered = customerRepo.searchCustomers(kw);
        renderTable(filtered);
        lblStats.setText(filtered.size() + " / " + (allCustomersCache != null ? allCustomersCache.size() : 0) + " KH");
    }

    private void clearFilter() {
        txtKeyword.setText("");
        applyFilter();
    }

    private void addNewCustomer() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        CustomerDialog dialog = new CustomerDialog((Frame) owner, "Add Customer", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            int newId = customerRepo.addCustomer(dialog.getFullName(), dialog.getPhoneNumber(), dialog.getEmail());
            if (newId > 0) {
                loadData();
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error adding customer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadData() {
        allCustomersCache = customerRepo.getAllCustomers();
        applyFilter();
    }

    private void renderTable(List<Customer> customers) {
        tableModel.setRowCount(0);
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getFullName(),
                    c.getPhoneNumber(),
                    c.getEmail() != null ? c.getEmail() : "",
                    c.getCreatedAt() != null ? c.getCreatedAt().toString().substring(0, 16) : ""
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
        btn.setPreferredSize(new Dimension(160, 40));
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