package com.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;


import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.SignUp;
import com.ui.common.components.StyledButton;
import com.utils.Constants;
import com.ui.common.components.RoundBorder;

public class AdminDashboard extends JFrame {
    protected JPanel sideMenu;
    protected JLabel titleLabel;
    protected StyledButton logoutButton;
    protected JPanel contentPanel;

    public AdminDashboard() {
        initializeFrame();
        createNavigationBar();
        createSideMenu();
        createContentPanel();
    }

    private void initializeFrame() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(new ImageIcon(getClass().getResource("/icons/admin.png")).getImage());
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(Constants.SECONDARY_COLOR);
    }

    private void createNavigationBar() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Constants.PRIMARY_COLOR);
        navPanel.setPreferredSize(new Dimension(getWidth(), 70));
        navPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        titleLabel = new JLabel("Administrator Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.TITLE_FONT_SIZE));
        titleLabel.setForeground(Constants.WHITE);
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel shadowLabel = new JLabel();
        shadowLabel.setOpaque(false);
        shadowLabel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0, 0, 0, 30)));
        
        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel, BorderLayout.CENTER);
        titleContainer.add(shadowLabel, BorderLayout.SOUTH);
        
        navPanel.add(titleContainer, BorderLayout.CENTER);
        add(navPanel, BorderLayout.NORTH);
    }

    private void createSideMenu() {
        sideMenu = new JPanel();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBackground(Constants.WHITE);
        sideMenu.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 0, 1, Constants.BORDER_COLOR),
            new EmptyBorder(20, 0, 20, 0)
        ));
        sideMenu.setPreferredSize(new Dimension(280, getHeight()));

        // Students Section
        addMenuSection("Students");
        addMenuButton("View Students", e -> showStudentManagement());
        addMenuButton("Add New Student", e -> new SignUp(true));

        // Courses Section
        addMenuSection("Courses");
        addMenuButton("View Courses", e -> showCourseManagement());
        addMenuButton("Add New Course", e -> showAddCourse());
        addMenuButton("Grade Students", e -> showGradingInterface());
        addMenuButton("View All Grades", e -> showAllGrades());

        // System Section
        addMenuSection("System");
        addMenuButton("File Transfer", e -> showFileTransferPanel());

        // Add spacer
        sideMenu.add(Box.createVerticalGlue());

        // Button container panel for better organization
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Constants.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Refresh button with improved visibility
        StyledButton refreshButton = new StyledButton("Refresh", Constants.PRIMARY_COLOR);
        refreshButton.setForeground(Constants.WHITE);
        refreshButton.setBorder(new RoundBorder(8, Constants.PRIMARY_COLOR.darker()));
        refreshButton.setMaximumSize(new Dimension(280, 50));
        refreshButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.NORMAL_FONT_SIZE));
        refreshButton.addActionListener(e -> refreshCurrentView());
        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createVerticalStrut(15));

        // Logout button with improved visibility
        logoutButton = new StyledButton("Logout", Constants.ERROR_COLOR);
        logoutButton.setForeground(Constants.WHITE);
        logoutButton.setBorder(new RoundBorder(8, Constants.ERROR_COLOR.darker()));
        logoutButton.setMaximumSize(new Dimension(280, 50));
        logoutButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.NORMAL_FONT_SIZE));
        logoutButton.addActionListener(e -> handleLogout());
        buttonPanel.add(logoutButton);

        // Add hover effects
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(Constants.PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(Constants.PRIMARY_COLOR);
            }
        });

        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(Constants.ERROR_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(Constants.ERROR_COLOR);
            }
        });

        sideMenu.add(buttonPanel);

        add(sideMenu, BorderLayout.WEST);
    }

    private void addMenuSection(String title) {
        JLabel sectionLabel = new JLabel(title);
        sectionLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.HEADER_FONT_SIZE));
        sectionLabel.setForeground(Constants.LIGHT_TEXT);
        sectionLabel.setBorder(new EmptyBorder(20, 25, 10, 25));
        sideMenu.add(sectionLabel);
    }

    private void addMenuButton(String text, java.awt.event.ActionListener listener) {
        StyledButton button = new StyledButton(text, Constants.WHITE);
        button.setForeground(Constants.TEXT_COLOR);
        button.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.NORMAL_FONT_SIZE));
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setMaximumSize(new Dimension(280, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(listener);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Constants.SECONDARY_COLOR);
                button.setForeground(Constants.PRIMARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Constants.WHITE);
                button.setForeground(Constants.TEXT_COLOR);
            }
        });
        
        sideMenu.add(button);
    }

    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        contentPanel.setBackground(Constants.SECONDARY_COLOR);
        add(contentPanel, BorderLayout.CENTER);
    }

    // Methods to update content
    protected void setContent(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Abstract methods to be implemented by subclasses
    public void showStudentManagement() {}
    protected void showCourseManagement() {}
    protected void showAddCourse() {}
    protected void showFileTransferPanel() {}
    
    protected void showGradingInterface() {
        GradingInterface gradingInterface = new GradingInterface();
        gradingInterface.setVisible(true);
    }
    
    protected void showAllGrades() {
        AllGradesView allGradesView = new AllGradesView();
        allGradesView.setVisible(true);
    }
    
    protected void handleLogout() {}
    
    public void refreshStudentList() {
        showStudentManagement();
    }

    protected void refreshCurrentView() {
        showStudentManagement();
    }
}