package com.ticket.view;

import com.ticket.model.Booking;
import com.ticket.model.Customer;
import com.ticket.model.Account;
import com.ticket.repository.BookingRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BookingManagementPanel extends JPanel {
    private BookingRepository bookingRepo;
    private JTable table;
    private DefaultTableModel tableModel;
    private Account currentUser;

    private JTextField txtKeyword;
    private JComboBox<String> cbStatus, cbTrip;
    private JLabel lblStats;
    private List<Booking> allBookingsCache;

    public BookingManagementPanel(Account currentUser) {
        this.currentUser = currentUser;
        this.bookingRepo = new BookingRepository();
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

        // ── Header ──
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel lblTitle = new JLabel("Ticket Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = createStyledButton("Book New Ticket", new Color(34, 197, 94));
        JButton btnDetail = createStyledButton("View Details", new Color(168, 85, 247));
        JButton btnDelete = createStyledButton("Cancel Ticket", new Color(239, 68, 68));
        JButton btnExport = createStyledButton("Export Excel", new Color(30, 64, 175));

        btnPanel.add(btnAdd);
        btnPanel.add(btnDetail);
        btnPanel.add(btnDelete);
        btnPanel.add(btnExport);

        headerPanel.add(btnPanel, BorderLayout.EAST);
        mainContent.add(headerPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Filter Bar ──
        mainContent.add(createFilterBar());
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Table ──
        String[] columns = {"CODE", "Customer", "Created By", "Trip", "Date", "Status", "Amount"};
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
        btnDelete.addActionListener(e -> deleteTicket());
        btnDetail.addActionListener(e -> viewDetail());
        btnExport.addActionListener(e -> exportExcel());

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

    // ═══════════════════════════════════════════
    //  FILTER BAR
    // ═══════════════════════════════════════════
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

        // Keyword (customer name or booking code)
        JLabel lblKw = new JLabel("🔍");
        lblKw.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterGroup.add(lblKw);

        txtKeyword = new JTextField(14);
        txtKeyword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtKeyword.setPreferredSize(new Dimension(160, 36));
        txtKeyword.putClientProperty("JTextField.placeholderText", "Mã đơn hoặc tên KH...");
        filterGroup.add(txtKeyword);

        // Status
        filterGroup.add(new JLabel("TT:"));
        cbStatus = new JComboBox<>(new String[]{"[Tất cả]", "PENDING", "CONFIRMED", "CANCELLED"});
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbStatus.setPreferredSize(new Dimension(130, 36));
        cbStatus.setBackground(Color.WHITE);
        filterGroup.add(cbStatus);

        // Trip
        filterGroup.add(new JLabel("Chuyến:"));
        cbTrip = new JComboBox<>();
        cbTrip.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbTrip.setPreferredSize(new Dimension(160, 36));
        cbTrip.setBackground(Color.WHITE);
        filterGroup.add(cbTrip);

        bar.add(filterGroup, BorderLayout.WEST);

        // Right: buttons + stats
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
        String status = (String) cbStatus.getSelectedItem();
        String trip = (String) cbTrip.getSelectedItem();

        List<Booking> filtered = bookingRepo.searchBookings(kw, status, trip);
        renderTable(filtered);
        lblStats.setText(filtered.size() + " / " + (allBookingsCache != null ? allBookingsCache.size() : 0) + " đơn");
    }

    private void clearFilter() {
        txtKeyword.setText("");
        cbStatus.setSelectedItem("[Tất cả]");
        cbTrip.setSelectedItem("[Tất cả]");
        applyFilter();
    }

    private void refreshFilterDropdowns() {
        String prevTrip = (String) cbTrip.getSelectedItem();
        cbTrip.removeAllItems();
        cbTrip.addItem("[Tất cả]");
        for (String t : bookingRepo.getDistinctTripNames()) cbTrip.addItem(t);
        if (prevTrip != null) cbTrip.setSelectedItem(prevTrip);
        else cbTrip.setSelectedItem("[Tất cả]");
    }

    // ═══════════════════════════════════════════
    //  CRUD
    // ═══════════════════════════════════════════
    private void addNewBooking() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        BookingDialog dialog = new BookingDialog((Frame) owner, "New Booking", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Customer customer = dialog.getSelectedCustomer();
            if (customer != null) {
                int newId = bookingRepo.addBookingReturnId(
                        currentUser.getId(),
                        customer.getId(),
                        dialog.getSelectedTrip().getId(),
                        dialog.getTotalSeats(),
                        dialog.getAmount(),
                        dialog.getStatus(),
                        dialog.getPayment(),
                        dialog.getCouponCode()
                );
                if (newId > 0) {
                    com.ticket.model.Trip trip = dialog.getSelectedTrip();
                    trip.setAvailableSeats(trip.getAvailableSeats() - dialog.getTotalSeats());
                    new com.ticket.repository.TripRepository().updateTrip(trip);

                    loadData();
                    JOptionPane.showMessageDialog(this, "Ticket booked successfully! Code: BK-" + newId);

                    String reactPort = System.getProperty("REACT_PORT", "5173");
                    System.out.println("========== SMS SIMULATION ==========");
                    System.out.println("To: " + customer.getPhoneNumber());
                    System.out.println("Message: Đặt vé thành công! Xem vé tại: http://localhost:" + reactPort + "/ticket/BK-" + newId);
                    System.out.println("====================================");
                }
            }
        }
    }

    private void viewDetail() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vé để xem!");
            return;
        }
        String code = (String) table.getValueAt(row, 0);
        Booking selected = bookingRepo.getRecentBookings().stream()
                .filter(b -> b.getBookingCode().equals(code)).findFirst().orElse(null);
        if (selected != null) {
            Window owner = SwingUtilities.getWindowAncestor(this);
            new BookingDetailDialog((Frame) owner, selected).setVisible(true);
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
            BookingDialog dialog = new BookingDialog((Frame) owner, "Edit Ticket Status", selected);
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
        allBookingsCache = bookingRepo.getRecentBookings();
        refreshFilterDropdowns();
        applyFilter();
    }

    private void renderTable(List<Booking> bookings) {
        tableModel.setRowCount(0);
        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                    b.getBookingCode(),
                    b.getCustomerName() != null ? b.getCustomerName() + " - " + b.getCustomerPhone() : "—",
                    b.getCreatedByName() != null ? b.getCreatedByName() : "—",
                    b.getTripName(),
                    b.getBookingDate() != null ? b.getBookingDate().toString().substring(0, 16) : "",
                    b.getStatus(),
                    String.format("%,.0f VNĐ", b.getTotalAmount())
            });
        }
        tableModel.fireTableDataChanged();
    }

    private void deleteTicket() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vé cần hủy!");
            return;
        }

        String code = (String) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hủy vé " + code + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Booking selected = bookingRepo.getRecentBookings().stream()
                    .filter(b -> b.getBookingCode().equals(code)).findFirst().orElse(null);

            if (selected != null && bookingRepo.deleteBooking(selected.getId())) {
                com.ticket.model.Trip trip = new com.ticket.repository.TripRepository().getAllTrips().stream()
                        .filter(t -> t.getTripName().equals(selected.getTripName())).findFirst().orElse(null);
                if (trip != null) {
                    trip.setAvailableSeats(trip.getAvailableSeats() + selected.getTotalSeats());
                    new com.ticket.repository.TripRepository().updateTrip(trip);
                }
                loadData();
                JOptionPane.showMessageDialog(this, "Đã hủy vé.");
            }
        }
    }

    private void exportExcel() {
        List<Booking> bookings = bookingRepo.getRecentBookings();
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Tickets to Excel");
        chooser.setSelectedFile(new File("tickets_export.xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String path = chooser.getSelectedFile().getAbsolutePath();
        if (!path.endsWith(".xlsx")) path += ".xlsx";

        try {
            com.ticket.repository.ExcelExporter.exportBookings(bookings, path);
            JOptionPane.showMessageDialog(this, "Đã xuất " + bookings.size() + " bản ghi.\n" + path);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(JLabel.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String status = (String) value;
                if ("CONFIRMED".equals(status)) {
                    c.setText("✅ Đã XN");
                    c.setForeground(new Color(21, 128, 61));
                } else if ("PENDING".equals(status)) {
                    c.setText("⏳ Chờ XL");
                    c.setForeground(new Color(180, 83, 9));
                } else if ("CANCELLED".equals(status)) {
                    c.setText("❌ Đã hủy");
                    c.setForeground(new Color(185, 28, 28));
                }
                return c;
            }
        });
    }
}