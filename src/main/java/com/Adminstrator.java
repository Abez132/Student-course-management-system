package com;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.ui.admin.AdminDashboardImpl;
import com.ui.common.components.StyledButton;
import com.utils.Constants;

public class Adminstrator {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel wrongCredentialLbl;
    private StyledButton loginButton,backButton;
    private JFrame loginFrame;

    public Adminstrator() {
        adminLogInFrame();
    }
        
    private void adminLogInFrame() {
        loginFrame = new JFrame("Administrator Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(650,650);
        loginFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/admin.png")).getImage());
        loginFrame.setLayout(new GridBagLayout());
        loginFrame.setResizable(false);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.getContentPane().setBackground(Constants.SECONDARY_COLOR);

        // Create a panel for the login form
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Constants.WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel loginText = new JLabel("Administrator Login", SwingConstants.CENTER);
        loginText.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.TITLE_FONT_SIZE));
        loginText.setForeground(Constants.TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(loginText, gbc);

        // Username field
        gbc.gridwidth = 1;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.NORMAL_FONT_SIZE));
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.NORMAL_FONT_SIZE));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.NORMAL_FONT_SIZE));
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.NORMAL_FONT_SIZE));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);

        // Login button
        loginButton = new StyledButton("Login", Constants.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 5, 20, 5);
        loginButton.addActionListener(e -> adminLogIn());
        loginPanel.add(loginButton, gbc);

        // Back button
        backButton = new StyledButton("Back", Constants.SECONDARY_COLOR);
        backButton.setForeground(Constants.TEXT_COLOR);
        gbc.gridx = 1;
        gbc.gridy = 3;
        backButton.addActionListener(e -> {
            loginFrame.dispose();
            new LogIn();
        });
        loginPanel.add(backButton, gbc);

        // Error label
        wrongCredentialLbl = new JLabel("", SwingConstants.CENTER);
        wrongCredentialLbl.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.NORMAL_FONT_SIZE));
        wrongCredentialLbl.setForeground(Constants.ERROR_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 15, 15, 15);
        loginPanel.add(wrongCredentialLbl, gbc);

        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);
    }

    private void adminLogIn() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (username.equals("admin") && password.equals("123456")) {
            wrongCredentialLbl.setText("");
            loginFrame.dispose();
            new AdminDashboardImpl();
        } else {
            wrongCredentialLbl.setText("Invalid username or password");
        }
    }
}
