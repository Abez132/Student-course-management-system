package com.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.dao.CourseDAO;
import com.model.Course;
import com.ui.common.components.StyledButton;
import com.utils.Constants;
import com.utils.UIUtils;

public class CourseManagementPanel extends JPanel {
    private final CourseDAO courseDAO;
    private JLabel loadingLabel;
    private JTextField searchField;
    private JPanel coursesPanel;

    public CourseManagementPanel() {
        this.courseDAO = new CourseDAO();
        setLayout(new BorderLayout());
        setBackground(Constants.SECONDARY_COLOR);

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.SECONDARY_COLOR);
        headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Course Management", SwingUtilities.CENTER);
        titleLabel.setFont(Constants.getBoldFont(Constants.TITLE_FONT_SIZE));
        titleLabel.setForeground(Constants.TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Create search panel
        JPanel searchPanel = createSearchPanel();
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Create courses panel
        coursesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        coursesPanel.setBackground(Constants.SECONDARY_COLOR);

        JScrollPane scrollPane = new JScrollPane(coursesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Load initial data
        loadCourses();
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBackground(Constants.SECONDARY_COLOR);
        searchPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField(20);
        searchField.setPreferredSize(new java.awt.Dimension(200, 30));
        StyledButton searchButton = new StyledButton("Search", Constants.PRIMARY_COLOR);
        loadingLabel = new JLabel("Searching...");
        loadingLabel.setForeground(Constants.PRIMARY_COLOR);
        loadingLabel.setVisible(false);

        searchPanel.add(new JLabel("Search by Course Name or Code: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(loadingLabel);

        // Add search functionality
        searchButton.addActionListener(e -> performSearch());

        // Add real-time search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private Timer searchTimer = new Timer(300, e -> performSearch());

            {
                searchTimer.setRepeats(false);
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchTimer.restart(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchTimer.restart(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchTimer.restart(); }
        });

        return searchPanel;
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        loadingLabel.setVisible(true);
        new SearchWorker(searchText).execute();
    }

    private void loadCourses() {
        coursesPanel.removeAll();
        List<Course> courses = courseDAO.getAllCourses();
        for (Course course : courses) {
            coursesPanel.add(createCourseCard(course));
        }
        coursesPanel.revalidate();
        coursesPanel.repaint();
    }

    private JPanel createCourseCard(Course course) {
        JPanel card = new JPanel();
        card.setPreferredSize(new java.awt.Dimension(Constants.COURSE_CARD_WIDTH, Constants.COURSE_CARD_HEIGHT));
        card.setMaximumSize(new java.awt.Dimension(Constants.COURSE_CARD_WIDTH, Constants.COURSE_CARD_HEIGHT));
        card.setBackground(Constants.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Course Image
        ImageIcon icon = new ImageIcon(getClass().getResource("/" + course.getImageName()));
        ImageIcon scaledIcon = scaleIcon(icon, Constants.COURSE_CARD_WIDTH, 150);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Constants.WHITE);

        JLabel iconLabel = new JLabel(scaledIcon, SwingUtilities.CENTER);
        JLabel titleLabel = new JLabel(course.getCourseName(), SwingUtilities.CENTER);
        titleLabel.setFont(Constants.getBoldFont(Constants.HEADER_FONT_SIZE));
        titleLabel.setForeground(Constants.TEXT_COLOR);
        titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel codeLabel = new JLabel("Code: " + course.getCourseCode(), SwingUtilities.CENTER);
        codeLabel.setFont(Constants.getFont(Constants.NORMAL_FONT_SIZE));
        codeLabel.setForeground(Constants.TEXT_COLOR);

        topPanel.add(iconLabel, BorderLayout.CENTER);
        topPanel.add(titleLabel, BorderLayout.SOUTH);
        topPanel.add(codeLabel, BorderLayout.NORTH);

        card.add(topPanel, BorderLayout.NORTH);

        // Course Description
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(course.getDescription());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(Constants.getFont(Constants.NORMAL_FONT_SIZE));
        textArea.setEditable(false);
        textArea.setBackground(Constants.WHITE);
        textArea.setForeground(Constants.TEXT_COLOR);
        textArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        card.add(textArea, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Constants.WHITE);

        StyledButton editButton = new StyledButton("Edit", Constants.ACCENT_COLOR);
        StyledButton deleteButton = new StyledButton("Delete", Constants.ERROR_COLOR);

        editButton.addActionListener(e -> editCourse(course));
        deleteButton.addActionListener(e -> deleteCourse(course.getId()));

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private void editCourse(Course course) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        new CourseEditDialog(parentFrame, course, updatedCourse -> {
            if (courseDAO.updateCourse(updatedCourse)) {
                UIUtils.showSuccessDialog(this, "Course updated successfully!");
                loadCourses();
            } else {
                UIUtils.showErrorDialog(this, "Error updating course!");
            }
        });
    }

    private void deleteCourse(int courseId) {
        if (UIUtils.showConfirmDialog(this, "Are you sure you want to delete this course?")) {
            if (courseDAO.deleteCourse(courseId)) {
                UIUtils.showSuccessDialog(this, "Course deleted successfully!");
                loadCourses();
            } else {
                UIUtils.showErrorDialog(this, "Error deleting course!");
            }
        }
    }

    private class SearchWorker extends SwingWorker<List<Course>, Void> {
        private final String searchText;

        public SearchWorker(String searchText) {
            this.searchText = searchText;
        }

        @Override
        protected List<Course> doInBackground() throws Exception {
            List<Course> allCourses = courseDAO.getAllCourses();
            if (searchText.isEmpty()) {
                return allCourses;
            }

            return allCourses.stream()
                .filter(c -> c.getCourseName().toLowerCase().contains(searchText) ||
                            c.getCourseCode().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        }

        @Override
        protected void done() {
            try {
                List<Course> results = get();
                coursesPanel.removeAll();
                
                for (Course course : results) {
                    coursesPanel.add(createCourseCard(course));
                }

                coursesPanel.revalidate();
                coursesPanel.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                loadingLabel.setVisible(false);
            }
        }
    }

    public boolean addCourse(Course course) {
        int newId = courseDAO.addCourse(course);
        if (newId > 0) {
            course.setId(newId);
            loadCourses();
            return true;
        }
        return false;
    }
} 