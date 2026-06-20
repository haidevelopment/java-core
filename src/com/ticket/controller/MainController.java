package com.ticket.controller;

import com.ticket.view.LoginFrame;
import com.ticket.view.MainFrame;
import javax.swing.SwingUtilities;

public class MainController {
    private MainFrame mainFrame;

    public MainController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initEvents();
    }

    private void initEvents() {
        mainFrame.getDashboardButton().addActionListener(e -> {
            mainFrame.showPanel("DASHBOARD", mainFrame.getDashboardButton());
        });

        mainFrame.getTripsButton().addActionListener(e -> {
            mainFrame.showPanel("TRIPS", mainFrame.getTripsButton());
        });

        mainFrame.getTicketsButton().addActionListener(e -> {
            mainFrame.showPanel("TICKETS", mainFrame.getTicketsButton());
        });

        mainFrame.getUsersButton().addActionListener(e -> {
            mainFrame.showPanel("USERS", mainFrame.getUsersButton());
        });

        mainFrame.getCouponsButton().addActionListener(e -> {
            mainFrame.showPanel("COUPONS", mainFrame.getCouponsButton());
        });

        mainFrame.getSettingsButton().addActionListener(e -> {
            mainFrame.showPanel("SETTINGS", mainFrame.getSettingsButton());
        });

        mainFrame.getLogoutButton().addActionListener(e -> {
            mainFrame.dispose();
            
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                new LoginController(loginFrame);
                loginFrame.setVisible(true);
            });
        });
    }
}
