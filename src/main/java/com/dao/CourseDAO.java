package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model.Course;
import com.utils.DatabaseUtil;

public class CourseDAO {
    
    public int addCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, description, image_name) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDescription());
            pstmt.setString(4, course.getImageName());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        System.out.println("Fetching all courses...");
        
        try (Connection conn = DatabaseUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(new Course(
                    rs.getInt("id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getString("description"),
                    rs.getString("image_name")
                ));
            }
            System.out.println("Found " + courses.size() + " courses");
        } catch (SQLException e) {
            System.err.println("Error fetching courses: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }
    
    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, image_name = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDescription());
            pstmt.setString(4, course.getImageName());
            pstmt.setInt(5, course.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Course getCourseById(int id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                        rs.getInt("id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        rs.getString("image_name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean isStudentEnrolled(int studentId, int courseId) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if student is enrolled
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean enrollStudent(int studentId, int courseId) {
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean dropCourse(int studentId, int courseId) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> getEnrolledStudentsWithGrades(int courseId) {
        List<Map<String, Object>> students = new ArrayList<>();
        String sql = "SELECT s.id, s.student_id, s.first_name, s.last_name, e.grade, c.course_code " +
                    "FROM enrollments e " +
                    "INNER JOIN students s ON s.id = e.student_id " +
                    "INNER JOIN courses c ON c.id = e.course_id " +
                    "WHERE e.course_id = ?";
        
        System.out.println("\n=== Fetching Enrolled Students ===");
        System.out.println("Course ID: " + courseId);
        System.out.println("SQL Query: " + sql);
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return students;
            }
            System.out.println("Database connection established successfully");
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, courseId);
                System.out.println("Executing query with course ID: " + courseId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        Map<String, Object> student = new HashMap<>();
                        int studentId = rs.getInt("id");
                        String studentIdStr = rs.getString("student_id");
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        String grade = rs.getString("grade");
                        String courseCode = rs.getString("course_code");
                        
                        student.put("id", studentId);
                        student.put("studentId", studentIdStr);
                        student.put("name", firstName + " " + lastName);
                        student.put("grade", grade);
                        student.put("courseCode", courseCode);
                        
                        students.add(student);
                        System.out.println("Found student #" + count + ":");
                        System.out.println("  - ID: " + studentId);
                        System.out.println("  - Student ID: " + studentIdStr);
                        System.out.println("  - Name: " + firstName + " " + lastName);
                        System.out.println("  - Grade: " + (grade != null ? grade : "Not Graded"));
                        System.out.println("  - Course: " + courseCode);
                    }
                    System.out.println("Total students found: " + count);
                }
            }
        } catch (SQLException e) {
            System.err.println("\n=== Database Error ===");
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Stack Trace:");
            e.printStackTrace();
        }
        
        System.out.println("=== End of Student Fetch ===\n");
        return students;
    }

    public boolean updateGrade(int studentId, int courseId, String grade) {
        String sql = "UPDATE enrollments SET grade = ? WHERE student_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, grade);
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Course getCourseByCode(String courseCode) {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        System.out.println("Looking up course with code: " + courseCode);
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseCode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        rs.getString("image_name")
                    );
                    System.out.println("Found course: " + course.getCourseName() + " (ID: " + course.getId() + ")");
                    return course;
                }
            }
            System.out.println("No course found with code: " + courseCode);
        } catch (SQLException e) {
            System.err.println("Error looking up course: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getAllGrades() {
        List<Map<String, Object>> grades = new ArrayList<>();
        String sql = "SELECT s.student_id, s.first_name, s.last_name, c.course_code, c.course_name, e.grade " +
                    "FROM students s " +
                    "JOIN enrollments e ON s.id = e.student_id " +
                    "JOIN courses c ON e.course_id = c.id " +
                    "ORDER BY s.student_id, c.course_code";
        
        try (Connection conn = DatabaseUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> grade = new HashMap<>();
                grade.put("studentId", rs.getString("student_id"));
                grade.put("studentName", rs.getString("first_name") + " " + rs.getString("last_name"));
                grade.put("courseCode", rs.getString("course_code"));
                grade.put("courseName", rs.getString("course_name"));
                grade.put("grade", rs.getString("grade"));
                grades.add(grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }

    public List<Map<String, Object>> getStudentGrades(int studentId) {
        List<Map<String, Object>> grades = new ArrayList<>();
        String sql = "SELECT c.course_code, c.course_name, e.grade " +
                    "FROM enrollments e " +
                    "JOIN courses c ON e.course_id = c.id " +
                    "WHERE e.student_id = ? " +
                    "ORDER BY c.course_code";
        
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> grade = new HashMap<>();
                    grade.put("courseCode", rs.getString("course_code"));
                    grade.put("courseName", rs.getString("course_name"));
                    grade.put("grade", rs.getString("grade"));
                    grades.add(grade);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }
} 