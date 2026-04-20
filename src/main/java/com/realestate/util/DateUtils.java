package com.realestate.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtils() {}

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMAT) : null;
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_ONLY_FORMAT) : null;
    }

    public static boolean isInPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }

    public static boolean isInFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }
}