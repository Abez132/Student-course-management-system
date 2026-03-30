package com.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(Constants.Validation.getEmailPattern());
    private static final Pattern PHONE_PATTERN = Pattern.compile(Constants.Validation.getPhonePattern());

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= Constants.Validation.getMinPasswordLength();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim();
    }

    public static boolean validateLoginInput(String id, String firstName, String password) {
        return !isNullOrEmpty(id) && !isNullOrEmpty(firstName) && !isNullOrEmpty(password);
    }

    public static boolean validateRegistrationInput(String firstName, String lastName, 
            String email, String password, String phone) {
        if (isNullOrEmpty(firstName) || isNullOrEmpty(lastName) || 
            isNullOrEmpty(email) || isNullOrEmpty(password)) {
            return false;
        }

        if (!isValidEmail(email)) {
            return false;
        }

        if (!isNullOrEmpty(phone) && !isValidPhone(phone)) {
            return false;
        }

        return isValidPassword(password);
    }
} 