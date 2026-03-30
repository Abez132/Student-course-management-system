package com.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.model.Course;
import com.ui.common.components.StyledButton;
import com.utils.Constants;
import com.utils.UIUtils;

public class CourseEditDialog extends JDialog {
    private final Course course;
    private final Consumer<Course> onSave;
    private JTextField nameField;
    private JTextField codeField;
    private JTextArea descriptionArea;
    private JLabel imagePreviewLabel;
    private String selectedImagePath;

    public CourseEditDialog(JFrame parent, Course course, Consumer<Course> onSave) {
        super(parent, "Edit Course", true);
        this.course = course;
        this.onSave = onSave;

        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(parent);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Constants.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Course Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Course Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(course.getCourseName());
        nameField.setFont(Constants.getFont(Constants.NORMAL_FONT_SIZE));
        formPanel.add(nameField, gbc);

        // Course Code
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Course Code:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        codeField = new JTextField(course.getCourseCode());
        codeField.setFont(Constants.getFont(Constants.NORMAL_FONT_SIZE));
        formPanel.add(codeField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(course.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(Constants.getFont(Constants.NORMAL_FONT_SIZE));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(descriptionArea, gbc);

        // Image Selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Course Image:"), gbc);

        JPanel imagePanel = new JPanel(new BorderLayout(10, 0));
        imagePanel.setBackground(Constants.WHITE);

        // Image preview
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new java.awt.Dimension(150, 150));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imagePreviewLabel, BorderLayout.CENTER);

        // Image selection button
        StyledButton selectImageButton = new StyledButton("Select Image", Constants.PRIMARY_COLOR);
        selectImageButton.addActionListener(e -> selectImage());
        imagePanel.add(selectImageButton, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(imagePanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Constants.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        StyledButton saveButton = new StyledButton("Save", Constants.PRIMARY_COLOR);
        StyledButton cancelButton = new StyledButton("Cancel", Constants.ERROR_COLOR);

        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial image if exists
        if (course.getImageName() != null && !course.getImageName().isEmpty()) {
            loadImagePreview(course.getImageName());
        }

        setVisible(true);
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser("src/main/resources");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getName();
            loadImagePreview(selectedImagePath);
        }
    }

    private void loadImagePreview(String imageName) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/" + imageName));
            if (icon.getIconWidth() > 0) {
                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("No Image");
            }
        } catch (Exception e) {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No Image");
        }
    }

    private void saveChanges() {
        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || code.isEmpty() || description.isEmpty() || selectedImagePath == null) {
            UIUtils.showErrorDialog(this, "All fields are required!");
            return;
        }

        course.setCourseName(name);
        course.setCourseCode(code);
        course.setDescription(description);
        course.setImageName(selectedImagePath);

        onSave.accept(course);
        dispose();
    }
} 