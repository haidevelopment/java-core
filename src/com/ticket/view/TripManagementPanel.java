package com.ticket.view;

import com.ticket.model.Trip;
import com.ticket.repository.TripRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Lớp này quản lý giao diện Danh sách Chuyến đi (Trips)
 * Kế thừa từ JPanel để có thể nhúng vào MainFrame
 */
public class TripManagementPanel extends JPanel {
    private TripRepository tripRepo;
    private JTable table;
    private DefaultTableModel tableModel;

    public TripManagementPanel() {
        this.tripRepo = new TripRepository();
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 10, 25, 10));

        JLabel lblTitle = new JLabel("Trip Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(52, 73, 94));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRefresh = createStyledButton("Refresh", new Color(52, 152, 219)); // Nút màu xanh dương
        JButton btnAdd = createStyledButton("Add New Trip", new Color(46, 204, 113));
        JButton btnDelete = createStyledButton("Delete Selected", new Color(231, 76, 60));
        
        btnPanel.add(btnDelete);
        btnPanel.add(btnAdd);
        btnPanel.add(btnRefresh);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        btnRefresh.addActionListener(e -> loadData());

        String[] columns = { "ID", "Trip Name", "From", "To", "Departure", "Price", "Total Seats", "Available" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedTrip();
                }
            }
        });

        btnAdd.addActionListener(e -> addNewTrip());
        btnDelete.addActionListener(e -> deleteSelectedTrip());

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

    private void addNewTrip() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        TripDialog dialog = new TripDialog((Frame) owner, "Add New Trip", null);
        dialog.setVisible(true);

        Trip newTrip = dialog.getTripData();
        if (newTrip != null) {
            if (tripRepo.addTrip(newTrip)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Trip added successfully!");
            }
        }
    }

    private void editSelectedTrip() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;

        int id = (int) table.getValueAt(row, 0);
        Trip selectedTrip = tripRepo.getAllTrips().stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);

        if (selectedTrip != null) {
            Window owner = SwingUtilities.getWindowAncestor(this);
            TripDialog dialog = new TripDialog((Frame) owner, "Edit Trip", selectedTrip);
            dialog.setVisible(true);

            Trip updatedTrip = dialog.getTripData();
            if (updatedTrip != null) {
                if (tripRepo.updateTrip(updatedTrip)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Trip updated successfully!");
                }
            }
        }
    }

    private void deleteSelectedTrip() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a trip to delete!");
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete trip ID: " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (tripRepo.deleteTrip(id)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Trip deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not delete trip.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Trip> trips = tripRepo.getAllTrips();
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
                    "$" + t.getBasePrice(),
                    t.getTotalSeats(),
                    t.getAvailableSeats()
            });
        }
        tableModel.fireTableDataChanged();
    }

    /**
     * Hàm tiện ích để tạo nút bấm đẹp (bo góc, màu sắc)
     */
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
