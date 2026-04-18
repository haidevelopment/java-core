package com.ticket.view;

import com.ticket.model.Booking;
import com.ticket.repository.BookingRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingManagementPanel extends JPanel {
    private BookingRepository bookingRepo;
    private JTable table;
    private DefaultTableModel tableModel;

    public BookingManagementPanel() {
        this.bookingRepo = new BookingRepository();
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
        
        JLabel lblTitle = new JLabel("Ticket Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(52, 73, 94));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);
        
        JButton btnAdd = createStyledButton("Book New Ticket", new Color(46, 204, 113));
        JButton btnRefresh = createStyledButton("Refresh", new Color(52, 152, 219));
        JButton btnDelete = createStyledButton("Cancel Ticket", new Color(231, 76, 60));
        
        btnPanel.add(btnDelete);
        btnPanel.add(btnAdd);
        btnPanel.add(btnRefresh);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Table
        String[] columns = {"CODE", "Customer", "Trip", "Date", "Status", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel);
        styleTable(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editBooking();
            }
        });

        btnAdd.addActionListener(e -> addNewBooking());
        btnRefresh.addActionListener(e -> loadData());
        btnDelete.addActionListener(e -> deleteTicket());

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

    private void addNewBooking() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        BookingDialog dialog = new BookingDialog((Frame)owner, "New Booking", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (bookingRepo.addBooking(1, dialog.getSelectedTrip().getId(), 
                    dialog.getCode(), dialog.getAmount(), dialog.getStatus(), dialog.getPayment())) {
                loadData();
                JOptionPane.showMessageDialog(this, "Ticket booked successfully!");
            }
        }
    }

    private void editBooking() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String code = (String) table.getValueAt(row, 0);
        Booking selected = bookingRepo.getRecentBookings().stream()
                .filter(b -> b.getBookingCode().equals(code)).findFirst().orElse(null);

        if (selected != null) {
            Window owner = SwingUtilities.getWindowAncestor(this);
            BookingDialog dialog = new BookingDialog((Frame)owner, "Edit Ticket Status", selected);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                if (bookingRepo.updateBookingStatus(selected.getId(), dialog.getStatus())) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Status updated!");
                }
            }
        }
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingRepo.getRecentBookings();
        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                b.getBookingCode(),
                b.getCustomerName(),
                b.getTripName(),
                b.getBookingDate().toString().substring(0, 16),
                b.getStatus(),
                "$" + b.getTotalAmount()
            });
        }
        tableModel.fireTableDataChanged();
    }

    private void deleteTicket() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to cancel!");
            return;
        }
        
        String code = (String) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel ticket " + code + "?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Booking selected = bookingRepo.getRecentBookings().stream()
                    .filter(b -> b.getBookingCode().equals(code)).findFirst().orElse(null);
            
            if (selected != null && bookingRepo.deleteBooking(selected.getId())) {
                loadData();
                JOptionPane.showMessageDialog(this, "Ticket cancelled and removed from system.");
            }
        }
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
        
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(JLabel.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String status = (String) value;
                if ("CONFIRMED".equals(status)) c.setForeground(new Color(46, 204, 113));
                else if ("PENDING".equals(status)) c.setForeground(new Color(241, 194, 50));
                else if ("CANCELLED".equals(status)) c.setForeground(new Color(231, 76, 60));
                return c;
            }
        });
    }
}
