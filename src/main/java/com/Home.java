package com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import java.awt.GridLayout;
import java.awt.Image;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.dao.CourseDAO;
import com.model.Course;
import com.ui.common.components.StyledButton;
import com.ui.student.FileViewPanel;
import com.ui.common.components.RoundBorder;

public class Home {
    private JFrame homeFrame;
    private JScrollPane content;
    private  int currentStudentId;
    private LoadingScreen loadingScreen;
    private CourseDAO courseDAO = new CourseDAO();

    private StyledButton logoutBtn,refreshBtn;

    // Simplified color palette - only keep colors that are used multiple times
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color LIGHT_TEXT = new Color(100, 116, 139);
    private static final Color TEXT_COLOR = new Color(31, 41, 55);

    private static final int COURSE_CARD_WIDTH = 300;
    private static final int COURSE_CARD_HEIGHT = 400;
    private static final int COURSE_IMAGE_WIDTH = 300;
    private static final int COURSE_IMAGE_HEIGHT = 160;

    public Home(int studentId) {
        this.currentStudentId = studentId;
        this.loadingScreen = new LoadingScreen();
        createHomeFrame();
    }

    private void createHomeFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
            
        }

        homeFrame = new JFrame("Student Dashboard");
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        homeFrame.setMinimumSize(new Dimension(512, 768));
        homeFrame.setLayout(new BorderLayout());
        homeFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/home.png")).getImage());
        homeFrame.getContentPane().setBackground(new Color(241, 245, 249));
        homeFrame.setLocationRelativeTo(null);

        createNavigationBar();
        createSidebar();
        createContentPanel();

        // Show courses immediately
        showCourses();

        homeFrame.setVisible(true);
    }

    private void createNavigationBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(new Color(31, 41, 55));
        navBar.setPreferredSize(new Dimension(homeFrame.getWidth(), 60));
        navBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // App title/logo
        JLabel title = new JLabel("Learning Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        navBar.add(title, BorderLayout.WEST);

        // User menu
        JPanel userMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userMenu.setOpaque(false);

        refreshBtn = new StyledButton("Refresh", new Color(0, 108, 211 ));
        logoutBtn = new StyledButton("Logout", new Color(250, 96, 96 ));

        refreshBtn.addActionListener(e -> refreshView());
        logoutBtn.addActionListener(e -> {
            homeFrame.dispose();
            new LogIn();
        });


        userMenu.add(refreshBtn);
        userMenu.add(logoutBtn);
        navBar.add(userMenu, BorderLayout.EAST);

        homeFrame.add(navBar, BorderLayout.NORTH);
    }

    private void createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(150, homeFrame.getHeight()));
        sidebar.setBackground(new Color(120,144,250));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Sidebar header
        JLabel sidebarHeader = new JLabel("Menu");
        sidebarHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sidebarHeader.setForeground(Color.WHITE);
        sidebarHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sidebarHeader);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Navigation buttons
        JButton coursesBtn = createSidebarButton("All Courses", e -> showCourses());
        JButton myCoursesBtn = createSidebarButton("My Courses", e -> displayEnrolledCourses());
        JButton gradesBtn = createSidebarButton("My Grades", e -> showGrades());
        JButton materialsBtn = createSidebarButton("Course Materials", e -> showCourseMaterials());

        sidebar.add(coursesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(myCoursesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(gradesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(materialsBtn);

        homeFrame.add(sidebar, BorderLayout.WEST);
    }

    private JButton createSidebarButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.BLUE);
        button.setBackground(new Color(44, 55, 73));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        
        button.addActionListener(action);
        
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(59, 130, 246));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(44, 55, 73));
            }
        });
        
        return button;
    }

    // Utility method for common thread operations
    private void executeInBackground(String loadingMessage, Runnable task) {
        loadingScreen.setMessage(loadingMessage);
        loadingScreen.showWithFade();

        new Thread(() -> {
            task.run();
            SwingUtilities.invokeLater(() -> loadingScreen.hideWithFade());
        }).start();
    }

    // Utility method for table styling
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(59, 130, 246, 50));
        table.setSelectionForeground(TEXT_COLOR);
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_COLOR);
        table.getTableHeader().setBackground(new Color(249, 250, 251));
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(centerRenderer);
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void createContentPanel() {
        JPanel scrollableContent = new JPanel();
        scrollableContent.setLayout(new BoxLayout(scrollableContent, BoxLayout.Y_AXIS));
        scrollableContent.setBackground(new Color(241, 245, 249));
        
        // Create a wrapper panel that will maintain the GridLayout
        JPanel cardsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        cardsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        cardsPanel.setBackground(new Color(241, 245, 249));
        scrollableContent.add(cardsPanel);
        
        content = new JScrollPane(scrollableContent);
        content.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        content.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        content.getVerticalScrollBar().setUnitIncrement(16);
        content.getHorizontalScrollBar().setUnitIncrement(16);
        content.setBorder(null);
        
        homeFrame.add(content, BorderLayout.CENTER);
    }

    private void showCourses() {
        executeInBackground("Loading courses...", () -> {
            List<Course> courses = courseDAO.getAllCourses();
            SwingUtilities.invokeLater(() -> {
                resetToGridLayout();
                JPanel scrollableContent = (JPanel)content.getViewport().getView();
                JPanel cardsPanel = (JPanel)scrollableContent.getComponent(0);
                
                for (Course course : courses) {
                    cardsPanel.add(createCourseCard(course));
                }

                cardsPanel.revalidate();
                cardsPanel.repaint();
            });
        });
    }

    private JPanel createCourseCard(Course course) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new RoundBorder(12, BORDER_COLOR));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(COURSE_CARD_WIDTH, COURSE_CARD_HEIGHT));
        card.setMaximumSize(new Dimension(COURSE_CARD_WIDTH, COURSE_CARD_HEIGHT));

        // Course Image
        ImageIcon icon = new ImageIcon(getClass().getResource("/" + course.getImageName()));
        ImageIcon scaledIcon = scaleIcon(icon, COURSE_IMAGE_WIDTH, COURSE_IMAGE_HEIGHT);

        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(0, 0, 10, 0)
        ));
        card.add(imageLabel, BorderLayout.NORTH);

        // Course Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Course Title
        JLabel titleLabel = new JLabel(course.getCourseName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        infoPanel.add(titleLabel);

        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Course Description
        JTextArea descArea = new JTextArea(course.getDescription());
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setForeground(LIGHT_TEXT);
        descArea.setBackground(Color.WHITE);
        descArea.setBorder(new EmptyBorder(0, 0, 15, 0));
        infoPanel.add(descArea);

        card.add(infoPanel, BorderLayout.CENTER);

        // Action Button
        JButton actionButton = new JButton();
        actionButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actionButton.setForeground(Color.WHITE);
        actionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        actionButton.setFocusPainted(false);
        actionButton.setBorderPainted(false);
        actionButton.setContentAreaFilled(true);
        actionButton.setBorder(new EmptyBorder(10, 15, 10, 15));

        boolean isEnrolled = courseDAO.isStudentEnrolled(currentStudentId, course.getId());
        if (isEnrolled) {
            actionButton.setText("Drop Course");
            actionButton.setBackground(ERROR_COLOR);
            actionButton.addActionListener(e -> handleCourseAction(course, true));
        } else {
            actionButton.setText("Enroll Now");
            actionButton.setBackground(PRIMARY_COLOR);
            actionButton.addActionListener(e -> handleCourseAction(course, false));
        }

        // Hover effect
        actionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                actionButton.setBackground(isEnrolled ? 
                    new Color(220, 38, 38) : // Darker red for drop
                    new Color(37, 99, 235)); // Darker blue for enroll
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                actionButton.setBackground(isEnrolled ? ERROR_COLOR : PRIMARY_COLOR);
            }
        });

        card.add(actionButton, BorderLayout.SOUTH);
        return card;
    }

    private void handleCourseAction(Course course, boolean isDropping) {
        executeInBackground(isDropping ? "Dropping course..." : "Enrolling in course...", () -> {
            boolean success = isDropping ? 
                courseDAO.dropCourse(currentStudentId, course.getId()) :
                courseDAO.enrollStudent(currentStudentId, course.getId());

            SwingUtilities.invokeLater(() -> {
                if (success) {
                    String message = isDropping ? 
                        "Successfully dropped " + course.getCourseName() :
                        "Successfully enrolled in " + course.getCourseName();
                    JOptionPane.showMessageDialog(homeFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                    showCourses();
                } else {
                    String message = isDropping ? 
                        "Error dropping course!" :
                        "Error enrolling in course!";
                    JOptionPane.showMessageDialog(homeFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    private static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    protected void displayEnrolledCourses() {
        executeInBackground("Loading enrolled courses...", () -> {
            List<Course> allCourses = courseDAO.getAllCourses();
            
            SwingUtilities.invokeLater(() -> {
                resetToGridLayout();
                JPanel scrollableContent = (JPanel)content.getViewport().getView();
                JPanel cardsPanel = (JPanel)scrollableContent.getComponent(0);

                for (Course course : allCourses) {
                    if (courseDAO.isStudentEnrolled(currentStudentId, course.getId())) {
                        cardsPanel.add(createCourseCard(course));
                    }
                }

                if (cardsPanel.getComponentCount() == 0) {
                    JLabel noCoursesLabel = new JLabel("You haven't enrolled in any courses yet", SwingConstants.CENTER);
                    noCoursesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    noCoursesLabel.setForeground(LIGHT_TEXT);
                    cardsPanel.add(noCoursesLabel);
                }

                cardsPanel.revalidate();
                cardsPanel.repaint();
            });
        });
    }

    private void showGrades() {
        executeInBackground("Loading grades...", () -> {
            List<Map<String, Object>> grades = courseDAO.getStudentGrades(currentStudentId);
            
            SwingUtilities.invokeLater(() -> {
                JPanel scrollableContent = (JPanel)content.getViewport().getView();
                JPanel cardsPanel = (JPanel)scrollableContent.getComponent(0);
                cardsPanel.removeAll();
                cardsPanel.setLayout(new BorderLayout()); // Use BorderLayout for the main panel
                
                // Create a new panel for grades content
                JPanel gradesPanel = new JPanel(new BorderLayout());
                gradesPanel.setBackground(new Color(241, 245, 249));

                // Header panel
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(PRIMARY_COLOR);
                headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

                JLabel titleLabel = new JLabel("My Academic Performance");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                titleLabel.setForeground(Color.WHITE);
                headerPanel.add(titleLabel, BorderLayout.WEST);
                gradesPanel.add(headerPanel, BorderLayout.NORTH);

                // Table card
                JPanel tableCard = new JPanel(new BorderLayout());
                tableCard.setBackground(Color.WHITE);
                tableCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR),
                    new EmptyBorder(20, 20, 20, 20)
                ));

                // Create and style table
                String[] columnNames = {"Course Code", "Course Name", "Grade"};
                Object[][] data = grades.stream()
                    .map(grade -> new Object[]{
                        grade.get("courseCode"),
                        grade.get("courseName"),
                        grade.get("grade") != null ? grade.get("grade") : "Not Graded"
                    })
                    .toArray(Object[][]::new);

                JTable gradesTable = new JTable(data, columnNames);
                styleTable(gradesTable);

                // Set column widths
                gradesTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Course Code
                gradesTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Course Name
                gradesTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Grade

                JScrollPane tableScrollPane = new JScrollPane(gradesTable);
                tableScrollPane.setBorder(null);
                tableCard.add(tableScrollPane, BorderLayout.CENTER);

                // Summary panel
                if (!grades.isEmpty()) {
                    JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    summaryPanel.setBackground(Color.WHITE);
                    summaryPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

                    int gradedCourses = (int) grades.stream()
                        .filter(g -> g.get("grade") != null && !g.get("grade").equals("Not Graded"))
                        .count();

                    JLabel summaryLabel = new JLabel(String.format("Total Courses: %d | Graded Courses: %d", 
                        grades.size(), gradedCourses));
                    summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    summaryLabel.setForeground(LIGHT_TEXT);
                    summaryPanel.add(summaryLabel);
                    tableCard.add(summaryPanel, BorderLayout.SOUTH);
                }

                gradesPanel.add(tableCard, BorderLayout.CENTER);

                if (grades.isEmpty()) {
                    JLabel noGradesLabel = new JLabel("No grades available yet", SwingConstants.CENTER);
                    noGradesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    noGradesLabel.setForeground(LIGHT_TEXT);
                    noGradesLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
                    gradesPanel.add(noGradesLabel, BorderLayout.CENTER);
                }

                cardsPanel.add(gradesPanel, BorderLayout.CENTER);
                cardsPanel.revalidate();
                cardsPanel.repaint();
            });
        });
    }

    private void showCourseMaterials() {
        JPanel scrollableContent = (JPanel)content.getViewport().getView();
        JPanel cardsPanel = (JPanel)scrollableContent.getComponent(0);
        cardsPanel.removeAll();
        
        // Create a wrapper panel for the file view
        JPanel fileViewWrapper = new JPanel(new BorderLayout());
        fileViewWrapper.setBackground(new Color(241, 245, 249));
        FileViewPanel fileViewPanel = new FileViewPanel();
        fileViewWrapper.add(fileViewPanel, BorderLayout.CENTER);
        
        cardsPanel.add(fileViewWrapper);
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void resetToGridLayout() {
        JPanel scrollableContent = (JPanel)content.getViewport().getView();
        JPanel cardsPanel = (JPanel)scrollableContent.getComponent(0);
        cardsPanel.removeAll();
        cardsPanel.setLayout(new GridLayout(0, 3, 20, 20));
        cardsPanel.setBackground(new Color(241, 245, 249));
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void refreshView() {
        showCourses();
    }
}