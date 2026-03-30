package com.ui.admin;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.LogIn;
import com.model.Course;
import com.utils.UIUtils;

public class AdminDashboardImpl extends AdminDashboard {
    private final StudentManagementPanel studentPanel;
    private final CourseManagementPanel coursePanel;
    private final FileTransferPanel fileTransferPanel;

    public AdminDashboardImpl() {
        super();
        studentPanel = new StudentManagementPanel();
        coursePanel = new CourseManagementPanel();
        fileTransferPanel = new FileTransferPanel();
        showStudentManagement(); // Show student management view by default
        setLocationRelativeTo(null); // Center the window
        setVisible(true); // Make sure the frame is visible
    }

    @Override
    public void showStudentManagement() {
        contentPanel.removeAll();
        studentPanel.refreshStudentList();
        contentPanel.add(studentPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void showCourseManagement() {
        contentPanel.removeAll();
        contentPanel.add(coursePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void showAddCourse() {
        JFrame parentFrame = this;
        Course newCourse = new Course(0, "", "", "", "");
        new CourseEditDialog(parentFrame, newCourse, updatedCourse -> {
            if (coursePanel.addCourse(updatedCourse)) {
                UIUtils.showSuccessDialog(this, "Course added successfully!");
                showCourseManagement();
            } else {
                UIUtils.showErrorDialog(this, "Error adding course!");
            }
        });
    }

    @Override
    protected void handleLogout() {
        dispose();
        new LogIn();
    }

    @Override
    protected void showFileTransferPanel() {
        contentPanel.removeAll();
        contentPanel.add(fileTransferPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void refreshCurrentView() {
        if (getContentPane().getComponent(1) instanceof StudentManagementPanel) {
            showStudentManagement();
        } else if (getContentPane().getComponent(1) instanceof CourseManagementPanel) {
            showCourseManagement();
        } else if (getContentPane().getComponent(1) instanceof FileTransferPanel) {
            showFileTransferPanel();
        }
    }
} 