package com.ticket.view;

import com.ticket.model.Trip;
import com.ticket.repository.TripRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class TripManagementPanel extends JPanel {
    private TripRepository tripRepo;
    private JTable table;
    private DefaultTableModel tableModel;
    private boolean isAdmin;

    private JTextField txtKeyword;
    private JComboBox<String> cbStart, cbEnd;
    private JLabel lblStats;

    private List<Trip> allTripsCache;

    public TripManagementPanel(boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.tripRepo = new TripRepository();
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

        JLabel lblTitle = new JLabel("Trip Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = createStyledButton("Add New Trip", new Color(34, 197, 94));
        JButton btnDetail = createStyledButton("View Details", new Color(168, 85, 247));
        JButton btnDelete = createStyledButton("Delete", new Color(239, 68, 68));

        if (isAdmin) {
            btnPanel.add(btnAdd);
            btnPanel.add(btnDetail);
            btnPanel.add(btnDelete);
        }
        headerPanel.add(btnPanel, BorderLayout.EAST);
        mainContent.add(headerPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Filter Bar ──
        mainContent.add(createFilterBar());
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Table ──
        String[] columns = { "ID", "Trip Name", "From", "To", "Departure", "Price", "Seats", "Available" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        styleTable(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editSelectedTrip();
            }
        });

        btnAdd.addActionListener(e -> addNewTrip());
        btnDelete.addActionListener(e -> deleteSelectedTrip());
        btnDetail.addActionListener(e -> viewDetail());

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

        // Left side: filters
        JPanel filterGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterGroup.setOpaque(false);

        // Keyword
        JLabel lblKw = new JLabel("🔍");
        lblKw.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterGroup.add(lblKw);

        txtKeyword = new JTextField(14);
        txtKeyword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtKeyword.setPreferredSize(new Dimension(160, 36));
        txtKeyword.putClientProperty("JTextField.placeholderText", "Search trip name...");
        filterGroup.add(txtKeyword);

        // Start location
        cbStart = new JComboBox<>();
        cbStart.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbStart.setPreferredSize(new Dimension(140, 36));
        cbStart.setBackground(Color.WHITE);
        filterGroup.add(new JLabel("Từ:"));
        filterGroup.add(cbStart);

        // End location
        cbEnd = new JComboBox<>();
        cbEnd.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbEnd.setPreferredSize(new Dimension(140, 36));
        cbEnd.setBackground(Color.WHITE);
        filterGroup.add(new JLabel("Đến:"));
        filterGroup.add(cbEnd);

        bar.add(filterGroup, BorderLayout.WEST);

        // Right side: buttons + stats
        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionGroup.setOpaque(false);

        lblStats = new JLabel("");
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStats.setForeground(new Color(100, 116, 139));
        actionGroup.add(lblStats);

        JButton btnSearch = new JButton("Lọc") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = new Color(59, 130, 246);
                if (getModel().isPressed()) bg = bg.darker();
                else if (getModel().isRollover()) bg = bg.brighter();
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSearch.setPreferredSize(new Dimension(90, 36));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> applyFilter());

        JButton btnClear = new JButton("Xóa lọc") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = new Color(148, 163, 184);
                if (getModel().isPressed()) bg = bg.darker();
                else if (getModel().isRollover()) bg = bg.brighter();
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClear.setPreferredSize(new Dimension(90, 36));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setContentAreaFilled(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> clearFilter());

        actionGroup.add(btnSearch);
        actionGroup.add(btnClear);
        bar.add(actionGroup, BorderLayout.EAST);

        // Enter key triggers search
        txtKeyword.addActionListener(e -> applyFilter());

        return bar;
    }

    private void applyFilter() {
        String kw = txtKeyword.getText().trim();
        String start = (String) cbStart.getSelectedItem();
        String end = (String) cbEnd.getSelectedItem();
        if ("[Tất cả]".equals(start)) start = null;
        if ("[Tất cả]".equals(end)) end = null;

        List<Trip> filtered = tripRepo.searchTrips(kw, start, end);
        renderTable(filtered);
        lblStats.setText(filtered.size() + " / " + (allTripsCache != null ? allTripsCache.size() : 0) + " chuyến");
    }

    private void clearFilter() {
        txtKeyword.setText("");
        cbStart.setSelectedItem("[Tất cả]");
        cbEnd.setSelectedItem("[Tất cả]");
        applyFilter();
    }

    private void refreshFilterDropdowns() {
        String prevStart = (String) cbStart.getSelectedItem();
        String prevEnd = (String) cbEnd.getSelectedItem();

        cbStart.removeAllItems();
        cbStart.addItem("[Tất cả]");
        for (String s : tripRepo.getDistinctStartLocations()) cbStart.addItem(s);
        cbStart.setSelectedItem(prevStart != null && cbStart.getItemCount() > 0 ? prevStart : "[Tất cả]");

        cbEnd.removeAllItems();
        cbEnd.addItem("[Tất cả]");
        for (String s : tripRepo.getDistinctEndLocations()) cbEnd.addItem(s);
        cbEnd.setSelectedItem(prevEnd != null && cbEnd.getItemCount() > 0 ? prevEnd : "[Tất cả]");
    }

    // ═══════════════════════════════════════════
    //  CRUD
    // ═══════════════════════════════════════════
    private void viewDetail() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chuyến đi!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        Trip selected = tripRepo.searchTrips(null, null, null).stream()
                .filter(t -> t.getId() == id).findFirst().orElse(null);
        if (selected != null) {
            Window owner = SwingUtilities.getWindowAncestor(this);
            new TripDetailDialog((Frame) owner, selected).setVisible(true);
        }
    }

    private void addNewTrip() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        TripDialog dialog = new TripDialog((Frame) owner, "Add New Trip", null);
        dialog.setVisible(true);

        Trip newTrip = dialog.getTripData();
        if (newTrip != null) {
            if (tripRepo.addTrip(newTrip)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm chuyến đi thành công!");
            }
        }
    }

    private void editSelectedTrip() {
        if (!this.isAdmin) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền chỉnh sửa.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) table.getValueAt(row, 0);
        Trip selected = tripRepo.getAllTrips().stream()
                .filter(t -> t.getId() == id).findFirst().orElse(null);
        if (selected != null) {
            Window owner = SwingUtilities.getWindowAncestor(this);
            TripDialog dialog = new TripDialog((Frame) owner, "Edit Trip", selected);
            dialog.setVisible(true);
            Trip updated = dialog.getTripData();
            if (updated != null && tripRepo.updateTrip(updated)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            }
        }
    }

    private void deleteSelectedTrip() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến đi cần xóa!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa chuyến đi ID: " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (tripRepo.deleteTrip(id)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Đã xóa!");
            }
        }
    }

    public void loadData() {
        allTripsCache = tripRepo.getAllTrips();
        refreshFilterDropdowns();
        applyFilter();
    }

    private void renderTable(List<Trip> trips) {
        tableModel.setRowCount(0);
        for (Trip t : trips) {
            String departure = (t.getDepartureTime() != null)
                    ? t.getDepartureTime().toString().substring(0, 16)
                    : "N/A";
            tableModel.addRow(new Object[] {
                    t.getId(),
                    t.getTripName(),
                    t.getStartLocation(),
                    t.getEndLocation(),
                    departure,
                    String.format("%,.0f VNĐ", t.getBasePrice()),
                    t.getTotalSeats(),
                    t.getAvailableSeats()
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
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(235, 245, 251));
    }
}