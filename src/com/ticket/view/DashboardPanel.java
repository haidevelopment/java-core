package com.ticket.view;

import com.ticket.model.Booking;
import com.ticket.model.User;
import com.ticket.repository.DashboardRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private User currentUser;
    private DashboardRepository dashboardRepo;
    private final Color backgroundColor = new Color(240, 242, 245);
    private final Color primaryBlue = new Color(41, 128, 185);
    private final Color successGreen = new Color(46, 204, 113);
    private final Color warningOrange = new Color(241, 194, 50);
    private final Color dangerRed = new Color(231, 76, 60);
    private final Color purple = new Color(155, 89, 182);

    private JPanel statsGrid;
    private DefaultTableModel tableModel;

    public DashboardPanel(User user) {
        this.currentUser = user;
        this.dashboardRepo = new DashboardRepository();
        
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(backgroundColor);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setMaximumSize(new Dimension(2000, 60));
        
        JLabel lblTitle = new JLabel("Performance Overview");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Refresh Data") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 2, w - 2, h - 2, 15, 15);

                // Background Gradient
                Color color1 = primaryBlue;
                Color color2 = primaryBlue.darker();
                
                if (getModel().isPressed()) {
                    color1 = color1.darker();
                    color2 = color2.darker();
                } else if (getModel().isRollover()) {
                    color1 = color1.brighter();
                }
                
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w - 2, h - 2, 15, 15);
                
                // Border
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, w - 3, h - 3, 15, 15);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnRefresh.setPreferredSize(new Dimension(130, 38));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshData());
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        mainContent.add(headerPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        statsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        statsGrid.setBackground(backgroundColor);
        statsGrid.setMaximumSize(new Dimension(2000, 130));
        mainContent.add(statsGrid);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));

        mainContent.add(createTablePanel());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshData() {
        statsGrid.removeAll();
        
        int totalBookings = dashboardRepo.getTotalBookings(currentUser.getId(), currentUser.getRole());
        double totalRevenue = dashboardRepo.getTotalRevenue(currentUser.getId(), currentUser.getRole());
        Map<String, Integer> statusStats = dashboardRepo.getBookingStatsByStatus(currentUser.getId(), currentUser.getRole());
        
        statsGrid.add(createCard("Total Bookings", String.valueOf(totalBookings), primaryBlue));
        statsGrid.add(createCard("Confirmed Revenue", "$" + String.format("%.2f", totalRevenue), successGreen));
        
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            int totalUsers = dashboardRepo.getTotalUsers();
            int totalTrips = dashboardRepo.getTotalTrips();
            statsGrid.add(createCard("Active Users", String.valueOf(totalUsers), purple));
            statsGrid.add(createCard("Total Trips", String.valueOf(totalTrips), warningOrange));
        } else {
            int pending = statusStats.getOrDefault("PENDING", 0);
            int cancelled = statusStats.getOrDefault("CANCELLED", 0);
            statsGrid.add(createCard("Pending Requests", String.valueOf(pending), warningOrange));
            statsGrid.add(createCard("Cancelled", String.valueOf(cancelled), dangerRed));
        }

        tableModel.setRowCount(0);
        List<Booking> recent = dashboardRepo.getRecentBookings(currentUser.getId(), currentUser.getRole(), 10);
        for (Booking b : recent) {
            tableModel.addRow(new Object[]{
                    b.getBookingCode(),
                    b.getCustomerName(),
                    b.getTripName(),
                    b.getBookingDate().toString().substring(0, 16),
                    b.getStatus(),
                    "$" + b.getTotalAmount()
            });
        }

        statsGrid.revalidate();
        statsGrid.repaint();
    }

    private JPanel createCard(String title, String value, Color color) {
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(120, 130, 140));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(color);

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblValue);
        return card;
    }

    private JPanel createTablePanel() {
        JPanel container = new JPanel(new BorderLayout()) {
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
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTableTitle = new JLabel("Recent Activity");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setBorder(new EmptyBorder(0, 0, 20, 5));
        container.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"CODE", "Customer", "Trip Detail", "Date", "Status", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(235, 245, 251));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(new Color(100, 100, 100));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(JLabel.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String status = (String) value;
                if ("CONFIRMED".equals(status)) c.setForeground(successGreen);
                else if ("PENDING".equals(status)) c.setForeground(warningOrange);
                else if ("CANCELLED".equals(status)) c.setForeground(dangerRed);

                if (isSelected) c.setBackground(table.getSelectionBackground());
                else c.setBackground(Color.WHITE);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        container.add(scrollPane, BorderLayout.CENTER);
        
        return container;
    }
}
