package com.utils;

import java.awt.Color;
import java.awt.Font;

public class Constants {
    private Constants() {
        // Private constructor to prevent instantiation
    }

    // Validation Constants
    public static final class Validation {
        private Validation() {}
        
        public static int getMinPasswordLength() {
            return MIN_PASSWORD_LENGTH;
        }
        
        public static String getEmailPattern() {
            return EMAIL_PATTERN;
        }
        
        public static String getPhonePattern() {
            return PHONE_PATTERN;
        }
        
        public static String getLoginSuccess() {
            return LOGIN_SUCCESS;
        }
        
        public static String getLoginFailed() {
            return LOGIN_FAILED;
        }
        
        public static String getRegistrationSuccess() {
            return REGISTRATION_SUCCESS;
        }
        
        public static String getRegistrationFailed() {
            return REGISTRATION_FAILED;
        }
        
        public static String getFillAllFields() {
            return FILL_ALL_FIELDS;
        }
        
        public static String getInvalidEmail() {
            return INVALID_EMAIL;
        }
        
        public static String getInvalidPhone() {
            return INVALID_PHONE;
        }
        
        public static String getPasswordTooShort() {
            return PASSWORD_TOO_SHORT;
        }
    }

    // Base Colors
    private static final int PRIMARY_RGB = 0x3B82F6; // Modern blue
    private static final int ACCENT_RGB = 0xad5c5c;  // Teal
    private static final int DARK_RGB = 0x1F2937;    // Dark blue-gray
    
    // Colors
    public static final Color PRIMARY_COLOR = new Color(PRIMARY_RGB);
    public static final Color PRIMARY_LIGHT = new Color(PRIMARY_RGB, true).brighter();
    public static final Color PRIMARY_DARK = new Color(PRIMARY_RGB, true).darker();
    
    public static final Color ACCENT_COLOR = new Color(ACCENT_RGB);
    public static final Color ACCENT_LIGHT = new Color(ACCENT_RGB, true).brighter();
    
    public static final Color DARK_BG = new Color(DARK_RGB);
    public static final Color DARK_LIGHT = new Color(DARK_RGB, true).brighter();
    
    public static final Color TEXT_COLOR = new Color(DARK_RGB);
    public static final Color LIGHT_TEXT = new Color(100, 116, 139);
    public static final Color WHITE = Color.WHITE;
    
    public static final Color ERROR_COLOR = new Color(239, 68, 68);
    public static final Color SUCCESS_COLOR = ACCENT_COLOR;
    public static final Color WARNING_COLOR = new Color(245, 158, 11);
    
    public static final Color BORDER_COLOR = new Color(226, 232, 240);
    public static final Color SECONDARY_COLOR = new Color(241, 245, 249);

    // Fonts
    public static final String FONT_FAMILY = "Segoe UI";
    public static final int TITLE_FONT_SIZE = 24;
    public static final int HEADER_FONT_SIZE = 18;
    public static final int NORMAL_FONT_SIZE = 14;
    public static final int SMALL_FONT_SIZE = 12;

    // Dimensions
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;
    public static final int LOGIN_WINDOW_WIDTH = 500;
    public static final int LOGIN_WINDOW_HEIGHT = 600;
    public static final int COURSE_CARD_WIDTH = 300;
    public static final int COURSE_CARD_HEIGHT = 400;

    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String PHONE_PATTERN = "^\\d{10}$";

    // Messages
    public static final String LOGIN_SUCCESS = "Login successful!";
    public static final String LOGIN_FAILED = "Invalid credentials. Please try again.";
    public static final String REGISTRATION_SUCCESS = "Registration successful!";
    public static final String REGISTRATION_FAILED = "Registration failed!";
    public static final String FILL_ALL_FIELDS = "Please fill all required fields!";
    public static final String INVALID_EMAIL = "Please enter a valid email address!";
    public static final String INVALID_PHONE = "Please enter a valid 10-digit phone number!";
    public static final String PASSWORD_TOO_SHORT = "Password must be at least 6 characters long!";

    // Utility methods
    public static Font getFont(int size) {
        return new Font(FONT_FAMILY, Font.PLAIN, size);
    }

    public static Font getBoldFont(int size) {
        return new Font(FONT_FAMILY, Font.BOLD, size);
    }
} 