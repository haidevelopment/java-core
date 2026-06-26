package com.ticket.view;

import com.ticket.model.Booking;
import com.ticket.model.Account;
import com.ticket.repository.DashboardRepository;
import com.ticket.repository.DashboardRepository.TripStat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private Account currentUser;
    private DashboardRepository dashboardRepo;

    // Color palette
    private final Color bg = new Color(245, 247, 250);
    private final Color cardBg = Color.WHITE;
    private final Color blue = new Color(59, 130, 246);
    private final Color green = new Color(34, 197, 94);
    private final Color orange = new Color(249, 115, 22);
    private final Color red = new Color(239, 68, 68);
    private final Color purple = new Color(168, 85, 247);
    private final Color teal = new Color(20, 184, 166);
    private final Color textPrimary = new Color(15, 23, 42);
    private final Color textSecondary = new Color(100, 116, 139);

    private JPanel statsGrid;
    private DefaultTableModel tableModel;
    private JPanel chartsPanel;
    private JButton btnRefresh;

    // Data holder for passing results from background thread to UI
    private static class DashboardData {
        int totalBookings, todayBookings, pendingCount, totalUsers, totalTrips;
        double totalRevenue, todayRevenue;
        Map<String, Integer> statusStats;
        List<TripStat> topTrips;
        List<Booking> recentBookings;
    }

    public DashboardPanel(Account account) {
        this.currentUser = account;
        this.dashboardRepo = new DashboardRepository();

        setLayout(new BorderLayout());
        setBackground(bg);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(bg);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(bg);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel lblTitle = new JLabel("Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(textPrimary);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        btnRefresh = createRefreshButton();
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        mainContent.add(headerPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 24)));

        statsGrid = new JPanel(new GridLayout(2, 3, 18, 18));
        statsGrid.setBackground(bg);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        mainContent.add(statsGrid);
        mainContent.add(Box.createRigidArea(new Dimension(0, 28)));

        chartsPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        chartsPanel.setBackground(bg);
        chartsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        mainContent.add(chartsPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 28)));

        mainContent.add(createTablePanel());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bg);
        scrollPane.getViewport().setBackground(bg);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════════
    //  REFRESH — runs DB queries off EDT via SwingWorker
    // ═══════════════════════════════════════════════════
    private void refreshData() {
        btnRefresh.setEnabled(false);
        btnRefresh.setText("⟳  Đang tải...");

        int userId = currentUser.getId();
        String role = currentUser.getRole();
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() {
                DashboardData d = new DashboardData();
                d.totalBookings = dashboardRepo.getTotalBookings(userId, role);
                d.todayBookings = dashboardRepo.getTodayBookings(userId, role);
                d.totalRevenue = dashboardRepo.getTotalRevenue(userId, role);
                d.todayRevenue = dashboardRepo.getTodayRevenue(userId, role);
                d.pendingCount = dashboardRepo.getPendingCount(userId, role);
                d.statusStats = dashboardRepo.getBookingStatsByStatus(userId, role);
                d.topTrips = dashboardRepo.getTopTrips(userId, role, 6);
                d.totalUsers = isAdmin ? dashboardRepo.getTotalUsers() : 0;
                d.totalTrips = isAdmin ? dashboardRepo.getTotalTrips() : 0;
                d.recentBookings = dashboardRepo.getRecentBookings(userId, role, 10);
                return d;
            }

            @Override
            protected void done() {
                try {
                    DashboardData d = get();
                    updateUI(d, isAdmin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                btnRefresh.setEnabled(true);
                btnRefresh.setText("⟳  Làm mới");
            }
        }.execute();
    }

    private void updateUI(DashboardData d, boolean isAdmin) {
        statsGrid.removeAll();
        statsGrid.add(createStatCard("Tổng đơn hàng", String.valueOf(d.totalBookings), "đơn", blue, "📋"));
        statsGrid.add(createStatCard("Hôm nay", String.valueOf(d.todayBookings), "đơn mới", green, "🆕"));
        statsGrid.add(createStatCard("Đơn chờ xử lý", String.valueOf(d.pendingCount), "đơn", orange, "⏳"));

        if (isAdmin) {
            statsGrid.add(createStatCard("Tổng doanh thu", formatMoney(d.totalRevenue), "VNĐ", teal, "💰"));
            statsGrid.add(createStatCard("Tổng nhân viên", String.valueOf(d.totalUsers), "người", purple, "👥"));
            statsGrid.add(createStatCard("Tổng chuyến đi", String.valueOf(d.totalTrips), "chuyến", blue, "🚌"));
        } else {
            statsGrid.add(createStatCard("Doanh thu", formatMoney(d.totalRevenue), "VNĐ", teal, "💰"));
            statsGrid.add(createStatCard("Doanh thu hôm nay", formatMoney(d.todayRevenue), "VNĐ", green, "💵"));
            int cancelled = d.statusStats.getOrDefault("CANCELLED", 0);
            statsGrid.add(createStatCard("Đã hủy", String.valueOf(cancelled), "đơn", red, "❌"));
        }

        chartsPanel.removeAll();
        chartsPanel.add(createBarChart("Trạng thái đơn hàng", d.statusStats, 280));
        chartsPanel.add(createTripChart("Top chuyến đi", d.topTrips, 280));

        tableModel.setRowCount(0);
        for (Booking b : d.recentBookings) {
            tableModel.addRow(new Object[]{
                    b.getBookingCode(),
                    b.getCustomerName() != null ? b.getCustomerName() : "—",
                    b.getTripName(),
                    b.getBookingDate().toString().substring(0, 16),
                    b.getStatus(),
                    formatMoney(b.getTotalAmount())
            });
        }

        statsGrid.revalidate();
        statsGrid.repaint();
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    // ═══════════════════════════════════════════════════
    //  STAT CARD
    // ═══════════════════════════════════════════════════
    private JPanel createStatCard(String title, String value, String subtitle, Color accent, String icon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
                // Top accent bar
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), 4, 18, 18));
                g2.fillRect(0, 0, getWidth(), 4);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 12));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 20, 14, 20));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(textSecondary);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblValue.setForeground(textPrimary);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(textSecondary);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(lblIcon, BorderLayout.EAST);

        JPanel bottomRow = new JPanel(new BorderLayout(4, 0));
        bottomRow.setOpaque(false);
        bottomRow.add(lblValue, BorderLayout.CENTER);
        bottomRow.add(lblSub, BorderLayout.SOUTH);

        card.add(topRow, BorderLayout.NORTH);
        card.add(bottomRow, BorderLayout.CENTER);
        return card;
    }

    // ═══════════════════════════════════════════════════
    //  BAR CHART — Bookings by Status
    // ═══════════════════════════════════════════════════
    private JPanel createBarChart(String title, Map<String, Integer> data, int maxHeight) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int topMargin = 36;
                int bottomMargin = 32;
                int leftMargin = 16;
                int rightMargin = 16;

                // Card background
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 18, 18));
                g2.setColor(new Color(0, 0, 0, 10));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 18, 18));

                if (data == null || data.isEmpty()) return;

                String[] keys = {"PENDING", "CONFIRMED", "CANCELLED"};
                String[] labels = {"Chờ XL", "Đã xác nhận", "Đã hủy"};
                Color[] colors = {orange, green, red};

                int maxVal = 1;
                for (String k : keys) maxVal = Math.max(maxVal, data.getOrDefault(k, 0));
                int chartH = h - topMargin - bottomMargin;
                int barCount = keys.length;
                int barWidth = Math.min(80, (w - leftMargin - rightMargin - 60) / barCount);
                int gap = (w - leftMargin - rightMargin - barWidth * barCount) / (barCount + 1);

                int x = leftMargin + gap;
                for (int i = 0; i < barCount; i++) {
                    int val = data.getOrDefault(keys[i], 0);
                    int barH = maxVal > 0 ? (int) ((double) val / maxVal * chartH) : 0;

                    // Bar
                    GradientPaint grad = new GradientPaint(0, 0, colors[i].brighter(), 0, barH, colors[i]);
                    g2.setPaint(grad);
                    g2.fill(new RoundRectangle2D.Double(x, topMargin + chartH - barH, barWidth, barH, 8, 8));

                    // Value on top
                    g2.setColor(textPrimary);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    String txt = String.valueOf(val);
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(txt);
                    g2.drawString(txt, x + (barWidth - tw) / 2, topMargin + chartH - barH - 6);

                    // Label below
                    g2.setColor(textSecondary);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    fm = g2.getFontMetrics();
                    tw = fm.stringWidth(labels[i]);
                    g2.drawString(labels[i], x + (barWidth - tw) / 2, topMargin + chartH + 18);

                    x += barWidth + gap;
                }

                // Grid lines
                g2.setColor(new Color(0, 0, 0, 10));
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{5, 5}, 0));
                for (int i = 0; i < 4; i++) {
                    int y = topMargin + chartH * i / 3;
                    g2.drawLine(leftMargin, y, w - rightMargin, y);
                }

                g2.dispose();
            }
        };
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(textPrimary);
        lblTitle.setBorder(new EmptyBorder(20, 20, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        panel.setPreferredSize(new Dimension(0, maxHeight));
        return panel;
    }

    // ═══════════════════════════════════════════════════
    //  HORIZONTAL BAR CHART — Top Trips
    // ═══════════════════════════════════════════════════
    private JPanel createTripChart(String title, List<TripStat> data, int maxHeight) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (data == null || data.isEmpty()) return;

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int topMargin = 36;
                int leftMargin = 130;
                int rightMargin = 24;
                int barH = 30;
                int gap = 10;

                // Card bg
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 18, 18));
                g2.setColor(new Color(0, 0, 0, 10));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 18, 18));

                int maxVal = data.stream().mapToInt(t -> t.bookingCount).max().orElse(1);
                int chartW = w - leftMargin - rightMargin;
                int y = topMargin;

                for (int i = 0; i < Math.min(data.size(), 6); i++) {
                    TripStat ts = data.get(i);
                    int bw = (int) ((double) ts.bookingCount / maxVal * chartW);

                    // Trip name
                    g2.setColor(textPrimary);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    String name = ts.tripName.length() > 16 ? ts.tripName.substring(0, 14) + "…" : ts.tripName;
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(name, leftMargin - fm.stringWidth(name) - 10, y + 19);

                    // Bar
                    Color[] palette = {blue, green, orange, teal, purple, red};
                    GradientPaint grad = new GradientPaint(0, 0, palette[i % palette.length].brighter(), bw, 0, palette[i % palette.length]);
                    g2.setPaint(grad);
                    g2.fill(new RoundRectangle2D.Double(leftMargin, y, bw, barH, 6, 6));

                    // Value
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    String val = ts.bookingCount + " đơn";
                    int vw = g2.getFontMetrics().stringWidth(val);
                    g2.drawString(val, leftMargin + bw - vw - 8, y + 20);

                    y += barH + gap;
                }

                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, maxHeight));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(textPrimary);
        lblTitle.setBorder(new EmptyBorder(20, 20, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        return panel;
    }

    // ═══════════════════════════════════════════════════
    //  RECENT ACTIVITY TABLE
    // ═══════════════════════════════════════════════════
    private JPanel createTablePanel() {
        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
                g2.setColor(new Color(0, 0, 0, 10));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel lblTitle = new JLabel("Hoạt động gần đây");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(textPrimary);
        lblTitle.setBorder(new EmptyBorder(0, 0, 16, 0));
        container.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Mã đơn", "Khách hàng", "Chuyến đi", "Ngày đặt", "Trạng thái", "Tổng tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(46);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(textPrimary);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(textPrimary);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(textSecondary);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(241, 245, 249)));

        // Status renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
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
                if (isSelected) c.setBackground(table.getSelectionBackground());
                else c.setBackground(Color.WHITE);
                return c;
            }
        });

        // Amount renderer (right-align)
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(JLabel.RIGHT);
                c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                c.setForeground(textPrimary);
                if (isSelected) c.setBackground(table.getSelectionBackground());
                else c.setBackground(Color.WHITE);
                return c;
            }
        };
        table.getColumnModel().getColumn(5).setCellRenderer(amountRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    // ═══════════════════════════════════════════════════
    //  REFRESH BUTTON
    // ═══════════════════════════════════════════════════
    private JButton createRefreshButton() {
        JButton btn = new JButton("⟳  Làm mới") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = blue;
                if (getModel().isPressed()) bgColor = blue.darker();
                else if (getModel().isRollover()) bgColor = blue.brighter();

                g2.setColor(bgColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();

                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(130, 42));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> refreshData());
        btnRefresh = btn;
        return btn;
    }

    // ═══════════════════════════════════════════════════
    //  UTILS
    // ═══════════════════════════════════════════════════
    private String formatMoney(double amount) {
        return NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"))
                .format(Math.round(amount));
    }
}