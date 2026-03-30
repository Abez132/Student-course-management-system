package com.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import com.dao.StudentDAO;
import com.model.Student;
import com.ui.common.components.StyledButton;
import com.utils.Constants;
import com.utils.UIUtils;

public class StudentManagementPanel extends JPanel {
    private final StudentDAO studentDAO;
    private final JTable studentTable;
    private final DefaultTableModel tableModel;
    private JLabel loadingLabel;
    private JTextField searchField;

    public StudentManagementPanel() {
        this.studentDAO = new StudentDAO();
        setLayout(new BorderLayout());
        setBackground(Constants.SECONDARY_COLOR);

        // Create table model
        tableModel = new DefaultTableModel(
            new String[] { "ID", "First Name", "Last Name", "Email", "Gender", "Phone Number", "Actions" }, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        // Create table
        studentTable = new JTable(tableModel);
        studentTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        studentTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new javax.swing.JCheckBox()));

        // Create search panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Create scroll pane
        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(tableScrollPane, BorderLayout.CENTER);

        // Load initial data
        loadStudents();
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

        searchPanel.add(new JLabel("Search by ID or First Name: "));
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

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            tableModel.addRow(new String[] {
                String.valueOf(s.getId()),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getGender(),
                s.getPhone(),
                "Edit/Delete"
            });
        }
    }

    private class SearchWorker extends SwingWorker<List<Student>, Void> {
        private final String searchText;
        
        public SearchWorker(String searchText) {
            this.searchText = searchText;
        }
        
        @Override
        protected List<Student> doInBackground() throws Exception {
            List<Student> allStudents = studentDAO.getAllStudents();
            if (searchText.isEmpty()) {
                return allStudents;
            }
            
            return allStudents.stream()
                .filter(s -> String.valueOf(s.getId()).contains(searchText) || 
                        s.getFirstName().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        }
        
        @Override
        protected void done() {
            try {
                List<Student> results = get();
                tableModel.setRowCount(0);
                for (Student s : results) {
                    tableModel.addRow(new String[] {
                        String.valueOf(s.getId()),
                        s.getFirstName(),
                        s.getLastName(),
                        s.getEmail(),
                        s.getGender(),
                        s.getPhone(),
                        "Edit/Delete"
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                loadingLabel.setVisible(false);
            }
        }
    }

    // Button renderer and editor classes
    private class ButtonRenderer extends javax.swing.JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Edit/Delete");
            return this;
        }
    }

    private class ButtonEditor extends javax.swing.DefaultCellEditor {
        protected javax.swing.JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(javax.swing.JCheckBox checkBox) {
            super(checkBox);
            button = new javax.swing.JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Show a popup menu with Edit and Delete options
                ImageIcon editIcon = new ImageIcon(getClass().getResource("/icons/edit.png"));
                ImageIcon deleteIcon = new ImageIcon(getClass().getResource("/icons/delete.png"));

                // Resize icons to 16x16 pixels
                Image editImage = editIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                Image deleteImage = deleteIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);

                javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
                javax.swing.JMenuItem editItem = new javax.swing.JMenuItem("Edit", new ImageIcon(editImage));
                javax.swing.JMenuItem deleteItem = new javax.swing.JMenuItem("Delete", new ImageIcon(deleteImage));

                editItem.addActionListener(e -> editStudent(row));
                deleteItem.addActionListener(e -> deleteStudent(row));

                popup.add(editItem);
                popup.add(deleteItem);
                popup.show(button, button.getWidth() / 2, button.getHeight() / 2);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void editStudent(int row) {
        int studentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        Student student = studentDAO.getStudentById(studentId);
        
        if (student != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            new StudentEditDialog(parentFrame, student, updatedStudent -> {
                if (studentDAO.updateStudent(updatedStudent)) {
                    UIUtils.showSuccessDialog(this, "Student updated successfully!");
                    loadStudents();
                } else {
                    UIUtils.showErrorDialog(this, "Error updating student!");
                }
            });
        }
    }

    private void deleteStudent(int row) {
        int studentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        if (UIUtils.showConfirmDialog(this, "Are you sure you want to delete this student?")) {
            if (studentDAO.deleteStudent(studentId)) {
                UIUtils.showSuccessDialog(this, "Student deleted successfully!");
                loadStudents();
            } else {
                UIUtils.showErrorDialog(this, "Error deleting student!");
            }
        }
    }

    public boolean addStudent(Student student) {
        int newId = studentDAO.registerStudent(student);
        if (newId > 0) {
            student.setId(newId);
            loadStudents();
            return true;
        }
        return false;
    }

    public void refreshStudentList() {
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            tableModel.addRow(new String[] {
                String.valueOf(s.getId()),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getGender(),
                s.getPhone(),
                "Edit/Delete"
            });
        }
    }
} 