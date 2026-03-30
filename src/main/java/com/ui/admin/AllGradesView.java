package com.ui.admin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.dao.CourseDAO;
import com.ui.common.components.StyledButton;
import com.utils.Constants;

public class AllGradesView extends JFrame {
    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private CourseDAO courseDAO;

    public AllGradesView() {
        courseDAO = new CourseDAO();
        initializeFrame();
        createComponents();
        loadAllGrades();
    }

    private void initializeFrame() {
        setTitle("All Student Grades");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/icons/admin.png")).getImage());
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        // Table setup
        String[] columns = {"Student ID", "Student Name", "Course Code", "Course Name", "Grade"};
        tableModel = new DefaultTableModel(columns, 0);
        
        gradesTable = new JTable(tableModel);
        gradesTable.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.NORMAL_FONT_SIZE));
        gradesTable.setRowHeight(30);
        
        // Set column widths
        TableColumn gradeColumn = gradesTable.getColumnModel().getColumn(4);
        gradeColumn.setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        
        // Refresh button
        StyledButton refreshButton = new StyledButton("Refresh", Constants.PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadAllGrades());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAllGrades() {
        tableModel.setRowCount(0);
        List<Map<String, Object>> allGrades = courseDAO.getAllGrades();
        for (Map<String, Object> grade : allGrades) {
            tableModel.addRow(new Object[]{
                grade.get("studentId"),
                grade.get("studentName"),
                grade.get("courseCode"),
                grade.get("courseName"),
                grade.get("grade")
            });
        }
    }
} 