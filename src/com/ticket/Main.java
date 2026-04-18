package com.ticket;

import javax.swing.SwingUtilities;
import com.ticket.view.LoginFrame;
import com.ticket.controller.LoginController;
import com.ticket.repository.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.initDatabase();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new LoginController(loginFrame);
            loginFrame.setVisible(true);
        });
    }
}
