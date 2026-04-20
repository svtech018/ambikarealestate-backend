package com.realestate.util;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    private ValidationUtils() {}

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isWithinRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
}