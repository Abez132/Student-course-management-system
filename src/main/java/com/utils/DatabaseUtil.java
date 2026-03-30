package com.utils;

import java.sql.*;
import java.io.*;
import java.util.Properties;

public class DatabaseUtil {
    private static final String CONFIG_FILE = "src/main/resources/database.properties";
    private static Connection connection = null;
    private static final Object lock = new Object();

    public static Connection getConnection() {
        synchronized (lock) {
            try {
                if (connection == null || connection.isClosed()) {
                    Properties props = new Properties();
                    FileInputStream fis = new FileInputStream(CONFIG_FILE);
                    props.load(fis);
                    fis.close();

                    String url = props.getProperty("db.url");
                    String username = props.getProperty("db.username");
                    String password = props.getProperty("db.password");

                    connection = DriverManager.getConnection(url, username, password);
                }
                return connection;
            } catch (Exception e) {
                System.err.println("Error connecting to database: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void closeConnection() {
        synchronized (lock) {
            if (connection != null) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Error closing database connection: " + e.getMessage());
                } finally {
                    connection = null;
                }
            }
        }
    }

    public static void executeQuery(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
    }

    public static ResultSet executeSelect(String sql) {
        try {
            Statement stmt = getConnection().createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("Error executing select query: " + e.getMessage());
            return null;
        }
    }

    public static int executeUpdate(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error executing update query: " + e.getMessage());
            return -1;
        }
    }
} 