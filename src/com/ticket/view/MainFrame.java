package com.ticket.view;

import com.ticket.model.User;
import com.ticket.model.Booking;
import com.ticket.repository.BookingRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private User currentUser;
    private BookingRepository bookingRepo;
    private JButton btnLogout;
    private JButton btnDashboard, btnTrips, btnTickets, btnUsers;
    private java.util.List<JButton> menuButtons = new java.util.ArrayList<>();
    private boolean isAdmin;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private final Color sidebarColor = new Color(44, 62, 80);
    private final Color activeMenuColor = new Color(52, 73, 94);
    private final Color backgroundColor = new Color(240, 242, 245);
    private final Color primaryBlue = new Color(41, 128, 185);

    public MainFrame(User user) {
        this.currentUser = user;
        this.bookingRepo = new BookingRepository();
        this.isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());

        if (this.isAdmin) {
            setTitle("Booking Pro - Admin Dashboard");
        } else {
            setTitle("Booking Pro - Staff Dashboard");
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        initComponents();
    }

    public JButton getLogoutButton() {
        return btnLogout;
    }

    public JButton getTripsButton() {
        return btnTrips;
    }

    public JButton getDashboardButton() {
        return btnDashboard;
    }

    public JButton getTicketsButton() {
        return btnTickets;
    }

    public JButton getUsersButton() {
        return btnUsers;
    }

    public void showPanel(String name, JButton activeBtn) {
        cardLayout.show(cardPanel, name);
        updateMenuHighlight(activeBtn);
    }

    private void updateMenuHighlight(JButton activeBtn) {
        for (JButton btn : menuButtons) {
            if (btn == activeBtn) {
                btn.setBackground(activeMenuColor);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(sidebarColor);
                btn.setForeground(new Color(191, 203, 209));
            }
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createSidebar(), BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(backgroundColor);
        contentPanel.add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        JPanel dashboardArea = new JPanel();
        dashboardArea.setLayout(new BoxLayout(dashboardArea, BoxLayout.Y_AXIS));
        dashboardArea.setBackground(backgroundColor);
        dashboardArea.setBorder(new EmptyBorder(25, 30, 25, 30));
        dashboardArea.add(createStatsPanel());
        dashboardArea.add(Box.createRigidArea(new Dimension(0, 25)));
        dashboardArea.add(createTablePanel());

        JScrollPane scrollDashboard = new JScrollPane(dashboardArea);
        scrollDashboard.setBorder(BorderFactory.createEmptyBorder());

        TripManagementPanel tripPanel = new TripManagementPanel(this.isAdmin);

        BookingManagementPanel bookingPanel = new BookingManagementPanel(this.isAdmin);

        UserManagementPanel userPanel = new UserManagementPanel();

        cardPanel.add(scrollDashboard, "DASHBOARD");
        cardPanel.add(tripPanel, "TRIPS");
        cardPanel.add(bookingPanel, "TICKETS");
        cardPanel.add(userPanel, "USERS");

        contentPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(null);

        JLabel lblLogo = new JLabel("BOOKING PRO", SwingConstants.CENTER);
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLogo.setBounds(0, 40, 250, 40);
        sidebar.add(lblLogo);

        btnDashboard = createMenuButton("      Dashboard", 130);
        btnTrips = createMenuButton("      Trips", 185);
        btnTickets = createMenuButton("      Tickets", 240);

        menuButtons.add(btnDashboard);
        menuButtons.add(btnTrips);
        menuButtons.add(btnTickets);

        int nextY = 295;

        if (this.isAdmin) {
            btnUsers = createMenuButton("      Users", nextY);
            menuButtons.add(btnUsers);
            nextY += 55;
        } else {
            btnUsers = new JButton();
        }

        btnDashboard.setBackground(activeMenuColor);
        btnDashboard.setForeground(Color.WHITE);

        for (JButton btn : menuButtons) {
            sidebar.add(btn);
        }

        sidebar.add(createMenuButton("      Reports", nextY));
        nextY += 55;
        sidebar.add(createMenuButton("      Settings", nextY));

        return sidebar;
    }

    private JButton createMenuButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(0, y, 250, 50);
        btn.setForeground(new Color(191, 203, 209));
        btn.setBackground(sidebarColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel lblTitle = new JLabel("Overview Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(new EmptyBorder(0, 30, 0, 0));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 22));
        userPanel.setBackground(Color.WHITE);

        JLabel lblUser = new JLabel("Hello, " + currentUser.getFullName());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 15));

        btnLogout = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(231, 76, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        userPanel.add(lblUser);
        userPanel.add(btnLogout);
        header.add(lblTitle, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createStatsPanel() {
        List<Booking> allBookings = bookingRepo.getRecentBookings();
        double revenue = bookingRepo.getTotalRevenue();
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(backgroundColor);
        statsPanel.setMaximumSize(new Dimension(2000, 120));

        statsPanel.add(createCard("Total Bookings", String.valueOf(allBookings.size()), new Color(52, 152, 219)));
        statsPanel.add(createCard("Total Revenue", "$" + String.format("%.2f", revenue), new Color(46, 204, 113)));
        statsPanel.add(createCard("Trip Coverage", "100%", new Color(155, 89, 182)));
        statsPanel.add(createCard("Platform Status", "Online", new Color(241, 194, 50)));

        return statsPanel;
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

        JLabel lblTableTitle = new JLabel("Real-time Booking Data");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setBorder(new EmptyBorder(0, 0, 20, 5));
        container.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"CODE", "Customer", "Trip Detail", "Date", "Status", "Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        List<Booking> bookings = bookingRepo.getRecentBookings();
        for (Booking b : bookings) {
            model.addRow(new Object[]{
                b.getBookingCode(),
                b.getCustomerName(),
                b.getTripName(),
                b.getBookingDate().toString().substring(0, 16),
                b.getStatus(),
                "$" + b.getTotalAmount()
            });
        }

        JTable table = new JTable(model);
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
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
