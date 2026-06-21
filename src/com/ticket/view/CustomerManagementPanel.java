package com.ticket.view;

import com.ticket.model.Customer;
import com.ticket.repository.CustomerRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerManagementPanel extends JPanel {
    private CustomerRepository customerRepo;
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerManagementPanel() {
        this.customerRepo = new CustomerRepository();
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 10, 25, 10));
        
        JLabel lblTitle = new JLabel("Customer Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(52, 73, 94));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btnPanel.setOpaque(false);
        
        JButton btnAdd = createStyledButton("Add Customer", new Color(46, 204, 113));
        JButton btnRefresh = createStyledButton("Refresh", new Color(52, 152, 219));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnRefresh);
        
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(btnPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Full Name", "Phone Number", "Email", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel);
        styleTable(table);

        btnAdd.addActionListener(e -> addNewCustomer());
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

    private void addNewCustomer() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        CustomerDialog dialog = new CustomerDialog((Frame)owner, "Add Customer", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            int newId = customerRepo.addCustomer(
                dialog.getFullName(),
                dialog.getPhoneNumber(),
                dialog.getEmail()
            );
            if (newId > 0) {
                loadData();
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error adding customer. Phone number might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerRepo.getAllCustomers();
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
