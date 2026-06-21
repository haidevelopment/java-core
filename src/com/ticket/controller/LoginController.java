package com.ticket.controller;

import com.ticket.model.Account;
import com.ticket.repository.UserRepository;
import com.ticket.view.LoginFrame;
import com.ticket.view.MainFrame;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginFrame loginFrame;
    private UserRepository userRepository;

    public LoginController(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.userRepository = new UserRepository();
        
        initEvents();
    }

    private void initEvents() {
        loginFrame.getLoginButton().addActionListener(e -> {
            String username = loginFrame.getUsername();
            String password = loginFrame.getPassword();
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Please enter all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Account account = userRepository.login(username, password);
            if (account != null) {
                JOptionPane.showMessageDialog(loginFrame, "Login successful! Welcome " + account.getFullName());
                
                loginFrame.dispose(); 
                
                SwingUtilities.invokeLater(() -> {
                    MainFrame mainFrame = new MainFrame(account);
                    new MainController(mainFrame);
                    mainFrame.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
