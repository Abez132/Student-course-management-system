package com;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.dao.StudentDAO;
import com.model.Student;
import com.ui.admin.AdminDashboardImpl;
import com.ui.common.components.StyledButton;
import com.utils.Constants;

public class SignUp {

    private JFrame signUpFrame;
    private JTextField firstNameField, lastNameField, emailField, phoneField;
    private JPasswordField passwordField;
    private boolean isFromAdmin;
    private StyledButton registerButton,backButton;
    private JComboBox<String> genderCombo;
    private StudentDAO studentDAO;
    private LoadingScreen loadingScreen;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public SignUp(boolean isFromAdmin) {
        this.isFromAdmin = isFromAdmin;
        this.studentDAO = new StudentDAO();
        this.loadingScreen = new LoadingScreen();
        createSignUpFrame();
    }

    private void createSignUpFrame() {
        signUpFrame = new JFrame("Sign Up");
        signUpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        signUpFrame.setSize(650, 650);
        signUpFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/signup.png")).getImage());
        signUpFrame.setLayout(new GridBagLayout());
        signUpFrame.setResizable(false);
        signUpFrame.setLocationRelativeTo(null);
        signUpFrame.getContentPane().setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title with better styling
        JLabel signUpText = new JLabel("Student Registration System");
        signUpText.setFont(new Font("SanSerif", Font.BOLD, 24));
        signUpText.setForeground(new Color(51, 51, 51));
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 30, 10);
        signUpFrame.add(signUpText, gbc);

        // Create a panel for the form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;
        gbc.gridy = 0;

        // Add form fields with improved styling
        addFormField(formPanel, "First Name:", firstNameField = createStyledTextField(), gbc, 0);
        addFormField(formPanel, "Last Name:", lastNameField = createStyledTextField(), gbc, 1);
        addFormField(formPanel, "Email:", emailField = createStyledTextField(), gbc, 2);
        addFormField(formPanel, "Password:", passwordField = createStyledPasswordField(), gbc, 3);
        
        // Gender combo box
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("SanSerif", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(genderLabel, gbc);

        String[] genders = {"Male", "Female"};
        genderCombo = new JComboBox<>(genders);
        genderCombo.setFont(new Font("SanSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);

        addFormField(formPanel, "Phone Number:", phoneField = createStyledTextField(), gbc, 5);

        // Buttons with improved styling
        registerButton = new StyledButton("Register", new Color(46, 204, 113));
        registerButton.addActionListener(e -> register());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 10, 10, 5);
        formPanel.add(registerButton, gbc);

        backButton = new StyledButton("Back", Constants.SECONDARY_COLOR);
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> {
            signUpFrame.dispose();
            if (isFromAdmin) {
                signUpFrame.dispose();
            } else {
                new LogIn();
            }
        });
        gbc.gridx = 1;
        gbc.insets = new Insets(20, 5, 10, 10);
        formPanel.add(backButton, gbc);

        // Add form panel to frame
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 20, 20, 20);
        signUpFrame.add(formPanel, gbc);

        signUpFrame.setVisible(true);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SanSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("SanSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private void addFormField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SanSerif", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private boolean validateInput() {
        if (firstNameField.getText().trim().isEmpty() || 
            lastNameField.getText().trim().isEmpty() || 
            emailField.getText().trim().isEmpty() || 
            passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(signUpFrame, 
                "Please fill all required fields!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            JOptionPane.showMessageDialog(signUpFrame, 
                "Please enter a valid email address!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!phoneField.getText().trim().isEmpty() && 
            !PHONE_PATTERN.matcher(phoneField.getText().trim()).matches()) {
            JOptionPane.showMessageDialog(signUpFrame, 
                "Please enter a valid 10-digit phone number!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (passwordField.getPassword().length < 6) {
            JOptionPane.showMessageDialog(signUpFrame, 
                "Password must be at least 6 characters long!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void register() {
        if (!validateInput()) {
            return;
        }

        loadingScreen.setMessage("Registering new student...");
        loadingScreen.showWithFade();

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phoneNumber = phoneField.getText().trim();
        String gender = (String) genderCombo.getSelectedItem();

        Student student = new Student(
            studentDAO.getNextStudentId(),
            firstName,
            lastName,
            email,
            password,
            gender,
            phoneNumber
        );

        int newId = studentDAO.registerStudent(student);
        
        loadingScreen.hideWithFade();
        
        if (newId > 0) {
            JOptionPane.showMessageDialog(signUpFrame, 
                "Sign Up Successful!\nYour ID is: " + newId, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            signUpFrame.dispose();
            
            if (isFromAdmin) {
                // Find the parent AdminDashboard window and refresh it
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(signUpFrame);
                if (parentFrame instanceof AdminDashboardImpl) {
                    ((AdminDashboardImpl) parentFrame).showStudentManagement();
                }
            } else {
                new Home(newId);
            }
        } else {
            JOptionPane.showMessageDialog(signUpFrame, 
                "Error registering student!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}