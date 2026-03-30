package com.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.network.FileTransferClient;
import com.network.FileTransferServer;
import com.utils.Constants;
import com.utils.UIUtils;

public class FileTransferPanel extends JPanel {
    private final FileTransferClient fileTransferClient;
    private final FileTransferServer fileTransferServer;
    private JTable fileTable;
    private DefaultTableModel tableModel;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton refreshButton;
    private JButton deleteButton;

    public FileTransferPanel() {
        this.fileTransferClient = new FileTransferClient();
        this.fileTransferServer = new FileTransferServer();
        setLayout(new BorderLayout());
        setBackground(Constants.SECONDARY_COLOR);
        initializeComponents();
        refreshFileList();
    }

    private void initializeComponents() {
        // Title
        JLabel titleLabel = new JLabel("Course Materials Management", SwingConstants.CENTER);
        titleLabel.setFont(Constants.getBoldFont(Constants.TITLE_FONT_SIZE));
        titleLabel.setForeground(Constants.TEXT_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"File Name", "Size", "Last Modified"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        fileTable = new JTable(tableModel);
        fileTable.setFont(Constants.getFont(Constants.NORMAL_FONT_SIZE));
        fileTable.getTableHeader().setFont(Constants.getBoldFont(Constants.NORMAL_FONT_SIZE));
        fileTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(fileTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Constants.SECONDARY_COLOR);

        uploadButton = new JButton("Upload File");
        downloadButton = new JButton("Download Selected");
        deleteButton = new JButton("Delete Selected");
        refreshButton = new JButton("Refresh List");

        uploadButton.addActionListener(e -> uploadFile());
        downloadButton.addActionListener(e -> downloadFile());
        deleteButton.addActionListener(e -> deleteFile());
        refreshButton.addActionListener(e -> refreshFileList());

        buttonPanel.add(uploadButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileTransferClient.uploadFile(selectedFile.getAbsolutePath());
            UIUtils.showSuccessDialog(this, "File uploaded successfully!");
            refreshFileList();
        }
    }

    private void downloadFile() {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showErrorDialog(this, "Please select a file to download.");
            return;
        }

        String fileName = (String) tableModel.getValueAt(selectedRow, 0);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(fileName));
        int result = fileChooser.showSaveDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            fileTransferClient.downloadFile(fileName, saveFile.getParent());
            UIUtils.showSuccessDialog(this, "File downloaded successfully!");
        }
    }

    private void deleteFile() {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showErrorDialog(this, "Please select a file to delete.");
            return;
        }

        String fileName = (String) tableModel.getValueAt(selectedRow, 0);
        File file = new File("uploads/" + fileName);
        if (file.exists() && file.delete()) {
            UIUtils.showSuccessDialog(this, "File deleted successfully!");
            refreshFileList();
        } else {
            UIUtils.showErrorDialog(this, "Error deleting file!");
        }
    }

    private void refreshFileList() {
        tableModel.setRowCount(0);
        List<String> files = fileTransferServer.listFiles();
        for (String fileName : files) {
            File file = new File("uploads/" + fileName);
            if (file.exists()) {
                Object[] row = {
                    fileName,
                    formatFileSize(file.length()),
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified())
                };
                tableModel.addRow(row);
            }
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
} 