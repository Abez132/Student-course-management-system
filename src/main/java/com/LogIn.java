package com;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.dao.StudentDAO;
import com.model.Student;
import com.ui.common.components.StyledButton;
import com.ui.common.components.RoundBorder;

public class LogIn {
    private JFrame loginFrame;
    private JTextField idField, firstNameField;
    private JPasswordField passwordField;
    private JLabel wrongCredentialLbl;
    private StyledButton signUpButton, loginButton, administratorButton;
    private StudentDAO studentDAO;
    private LoadingScreen loadingScreen;

    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(99, 102, 241);
    private static final Color SECONDARY_COLOR = new Color(236, 72, 153);
    private static final Color LIGHT_BG = new Color(249, 250, 251);
    private static final Color TEXT_COLOR = new Color(31, 41, 55);
    private static final Color LIGHT_TEXT = new Color(156, 163, 175);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);

    public LogIn() {
        studentDAO = new StudentDAO();
        loadingScreen = new LoadingScreen();
        createLoginFrame();
    }

    private void createLoginFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginFrame = new JFrame("Student Portal");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(650, 650);
        loginFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/login.png")).getImage());
        loginFrame.setLayout(new BorderLayout());
        loginFrame.setResizable(false);
        loginFrame.getContentPane().setBackground(LIGHT_BG);
        loginFrame.setLocationRelativeTo(null);

        // Main content panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(LIGHT_BG);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Card panel for form
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new CompoundBorder(
            new RoundBorder(16, BORDER_COLOR),
            new EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        
        JLabel titleLabel = new JLabel("Student Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(subtitleLabel, gbc);

        gbc.insets = new Insets(8, 0, 4, 0);

        // First Name Field
        firstNameField = createTextField("First Name");
        cardPanel.add(createInputPanel("First Name", firstNameField), gbc);

        // ID Field
        idField = createTextField("Student ID");
        cardPanel.add(createInputPanel("Student ID", idField), gbc);

        // Password Field
        passwordField = createPasswordField();
        cardPanel.add(createInputPanel("Password", passwordField), gbc);

        // Login and Sign up buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        buttonPanel.setOpaque(false);
        
        // top row panel for login and signup
        JPanel topButtonPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        topButtonPanel.setOpaque(false);
        
        // Login button
        loginButton = new StyledButton("LOGIN", PRIMARY_COLOR);
        loginButton.addActionListener(e -> login());
        topButtonPanel.add(loginButton);
        
        // Sign up button
        signUpButton = new StyledButton("SIGN UP", SECONDARY_COLOR);
        signUpButton.addActionListener(e -> {
            loginFrame.dispose();
            new SignUp(false);
        });
        topButtonPanel.add(signUpButton);
        
        buttonPanel.add(topButtonPanel);
        
        // Admin button
        administratorButton = new StyledButton("ADMIN",new Color(0xbdbdbd));
        administratorButton.setForeground(Color.BLACK);
        administratorButton.addActionListener(e -> {
            loginFrame.dispose();
            new Adminstrator();
        });
        buttonPanel.add(administratorButton);
        
        gbc.insets = new Insets(16, 0, 0, 0);
        cardPanel.add(buttonPanel, gbc);

        // Error message
        wrongCredentialLbl = new JLabel(" ", SwingConstants.CENTER);
        wrongCredentialLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        wrongCredentialLbl.setForeground(ERROR_COLOR);
        wrongCredentialLbl.setMinimumSize(new Dimension(300, 20));
        gbc.insets = new Insets(16, 0, 0, 0);
        cardPanel.add(wrongCredentialLbl, gbc);

        mainPanel.add(cardPanel);
        loginFrame.add(mainPanel, BorderLayout.CENTER);
        loginFrame.setVisible(true);
    }

    private JPanel createInputPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        panel.add(label, BorderLayout.NORTH);
        
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
            new RoundBorder(8, BORDER_COLOR),
            new EmptyBorder(12, 16, 12, 16)
        ));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(TEXT_COLOR);
        
        // Add placeholder effect
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(LIGHT_TEXT);
                }
            }
        });
        
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
            new RoundBorder(8, BORDER_COLOR),
            new EmptyBorder(12, 16, 12, 16)
        ));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(TEXT_COLOR);
        field.setEchoChar('•');
        
        return field;
    }


    private boolean validateInput() {
        String firstName = firstNameField.getText().trim();
        String userId = idField.getText().trim();
        char[] password = passwordField.getPassword();
        
        if (firstName.isEmpty() || firstName.equals("First Name")) {
            showError("Please enter your first name");
            return false;
        }
        
        if (userId.isEmpty() || userId.equals("Student ID")) {
            showError("Please enter your student ID");
            return false;
        }
        
        if (password.length == 0) {
            showError("Please enter your password");
            return false;
        }
        
        wrongCredentialLbl.setText(" ");
        return true;
    }

    private void showError(String message) {
        wrongCredentialLbl.setText(message);
    }
    

    private void login() {
        if (!validateInput()) {
            return;
        }

        String userId = idField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String password = new String(passwordField.getPassword());

        loadingScreen.setMessage("Authenticating...");
        loadingScreen.showWithFade();

        new Thread(() -> {
            Student student = studentDAO.authenticate(firstName, password, userId);

            SwingUtilities.invokeLater(() -> {
                loadingScreen.hideWithFade();
                
                if (student != null) {
                    loginFrame.dispose();
                    new Home(student.getId());
                } else {
                    showError("Invalid credentials. Please try again.");
                    passwordField.setText("");
                }
            });
        }).start();
    }
}