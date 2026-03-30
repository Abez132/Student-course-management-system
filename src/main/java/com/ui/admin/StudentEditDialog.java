package com.ui.admin;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.model.Student;
import com.ui.common.components.StyledButton;
import com.utils.Constants;
import com.utils.UIUtils;
import com.utils.ValidationUtils;

public class StudentEditDialog extends JDialog {
    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JTextField phoneField;
    private final JComboBox<String> genderCombo;
    private final Consumer<Student> onSave;

    public StudentEditDialog(JFrame parent, Student student, Consumer<Student> onSave) {
        super(parent, "Edit Student", true);
        this.onSave = onSave;

        // Initialize fields
        firstNameField = UIUtils.createStyledTextField();
        lastNameField = UIUtils.createStyledTextField();
        emailField = UIUtils.createStyledTextField();
        passwordField = UIUtils.createStyledPasswordField();
        phoneField = UIUtils.createStyledTextField();
        genderCombo = new JComboBox<>(new String[]{"Male", "Female"});

        // Set initial values
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        emailField.setText(student.getEmail());
        passwordField.setText(student.getPassword());
        phoneField.setText(student.getPhone());
        genderCombo.setSelectedItem(student.getGender());

        // Create form panel
        JPanel formPanel = UIUtils.createFormPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add form fields
        addFormField(formPanel, "First Name:", firstNameField, gbc, 0);
        addFormField(formPanel, "Last Name:", lastNameField, gbc, 1);
        addFormField(formPanel, "Email:", emailField, gbc, 2);
        addFormField(formPanel, "Password:", passwordField, gbc, 3);

        // Gender combo box
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(Constants.getFont(Constants.NORMAL_FONT_SIZE));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(genderLabel, gbc);

        genderCombo.setFont(Constants.getFont(Constants.NORMAL_FONT_SIZE));
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);

        addFormField(formPanel, "Phone Number:", phoneField, gbc, 5);

        // Buttons
        JPanel buttonPanel = new JPanel();
        StyledButton saveButton = new StyledButton("Save", Constants.SUCCESS_COLOR);
        StyledButton cancelButton = new StyledButton("Cancel", Constants.WARNING_COLOR);

        saveButton.addActionListener(e -> handleSave(student));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addFormField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(Constants.getFont(Constants.NORMAL_FONT_SIZE));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void handleSave(Student originalStudent) {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();
        String gender = (String) genderCombo.getSelectedItem();

        if (!ValidationUtils.validateRegistrationInput(firstName, lastName, email, password, phone)) {
            UIUtils.showErrorDialog(this, "Please fill all required fields correctly!");
            return;
        }

        originalStudent.setFirstName(firstName);
        originalStudent.setLastName(lastName);
        originalStudent.setEmail(email);
        originalStudent.setPassword(password);
        originalStudent.setGender(gender);
        originalStudent.setPhone(phone);

        onSave.accept(originalStudent);
        dispose();
    }
} 