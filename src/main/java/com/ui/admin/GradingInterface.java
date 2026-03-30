package com.ui.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.dao.CourseDAO;
import com.dao.StudentDAO;
import com.model.Course;
import com.model.Student;
import com.ui.common.components.StyledButton;
import com.utils.Constants;

public class GradingInterface extends JFrame {
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> courseSelector;
    private CourseDAO courseDAO;
    private StudentDAO studentDAO;

    public GradingInterface() {
        courseDAO = new CourseDAO();
        studentDAO = new StudentDAO();
        initializeFrame();
        createComponents();
        loadCourses();
    }

    private void initializeFrame() {
        setTitle("Grade Students");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setIconImage(new ImageIcon(getClass().getResource("/icons/admin.png")).getImage());
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        // Course selector panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel courseLabel = new JLabel("Select Course Code: ");
        courseLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.NORMAL_FONT_SIZE));
        
        courseSelector = new JComboBox<>();
        courseSelector.setPreferredSize(new Dimension(300, 30));
        courseSelector.addActionListener(e -> loadEnrolledStudents());
        
        topPanel.add(courseLabel, BorderLayout.WEST);
        topPanel.add(courseSelector, BorderLayout.CENTER);
        
        // Table setup
        String[] columns = {"Student ID", "Name", "Course Code", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only grade column is editable
            }
        };
        
        gradeTable = new JTable(tableModel);
        gradeTable.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.NORMAL_FONT_SIZE));
        gradeTable.setRowHeight(30);
        
        // Set up grade column with dropdown
        TableColumn gradeColumn = gradeTable.getColumnModel().getColumn(3);
        String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F", "Not Graded"};
        JComboBox<String> gradeComboBox = new JComboBox<>(grades);
        gradeColumn.setCellEditor(new DefaultCellEditor(gradeComboBox));
        gradeColumn.setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        
        // Save button
        StyledButton saveButton = new StyledButton("Save Grades", Constants.PRIMARY_COLOR);
        saveButton.addActionListener(e -> saveGrades());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCourses() {
        courseSelector.removeAllItems();
        List<Course> courses = courseDAO.getAllCourses();
        System.out.println("Loading courses for grading interface...");
        
        if (courses.isEmpty()) {
            System.out.println("No courses found in the database");
            JOptionPane.showMessageDialog(this, 
                "No courses available for grading.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Add a default empty selection
        courseSelector.addItem("Select a course...");
        
        for (Course course : courses) {
            System.out.println("Adding course to selector: " + course.getCourseCode() + " - " + course.getCourseName());
            courseSelector.addItem(course.getCourseCode());
        }
        
        courseSelector.setSelectedIndex(0);
        System.out.println("Total courses loaded: " + courses.size());
    }

    private void loadEnrolledStudents() {
        tableModel.setRowCount(0);
        String selectedCourseCode = (String) courseSelector.getSelectedItem();
        
        if (selectedCourseCode == null || selectedCourseCode.equals("Select a course...")) {
            System.out.println("No course selected");
            return;
        }

        System.out.println("\n=== Loading Enrolled Students ===");
        System.out.println("Selected Course Code: " + selectedCourseCode);
        
        Course selectedCourse = courseDAO.getCourseByCode(selectedCourseCode);
        if (selectedCourse == null) {
            System.out.println("Course not found: " + selectedCourseCode);
            JOptionPane.showMessageDialog(this, 
                "Course not found: " + selectedCourseCode, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Found course: " + selectedCourse.getCourseName() + " (ID: " + selectedCourse.getId() + ")");
        List<Map<String, Object>> students = courseDAO.getEnrolledStudentsWithGrades(selectedCourse.getId());
        System.out.println("Retrieved " + students.size() + " enrolled students");
        
        if (students.isEmpty()) {
            System.out.println("No students found for this course");
            JOptionPane.showMessageDialog(this, 
                "No students enrolled in this course.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        System.out.println("Adding students to table:");
        for (Map<String, Object> student : students) {
            String grade = (String) student.get("grade");
            if (grade == null) grade = "Not Graded";
            
            Object[] rowData = {
                student.get("studentId"),
                student.get("name"),
                selectedCourseCode,
                grade
            };
            
            tableModel.addRow(rowData);
            System.out.println("Added to table: " + student.get("name") + 
                            " (ID: " + student.get("studentId") + 
                            ", Grade: " + grade + ")");
        }
        
        tableModel.fireTableDataChanged();
        System.out.println("Table updated with " + students.size() + " students");
        System.out.println("=== End of Student Loading ===\n");
    }

    private void saveGrades() {
        String selectedCourseCode = (String) courseSelector.getSelectedItem();
        if (selectedCourseCode == null) return;

        Course selectedCourse = courseDAO.getCourseByCode(selectedCourseCode);
        if (selectedCourse == null) return;

        boolean success = true;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String studentId = (String) tableModel.getValueAt(i, 0);
            String grade = (String) tableModel.getValueAt(i, 3);
            
            // Get student ID from the database
            Student student = studentDAO.getStudentByStudentId(studentId);
            if (student != null) {
                success &= courseDAO.updateGrade(student.getId(), selectedCourse.getId(), grade);
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Grades saved successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Error saving some grades. Please try again.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 