package com.realestate.common.constants;

public final class AppConstants {

    private AppConstants() {
    }

    // Pagination defaults
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    // Validation limits
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;
    public static final int MAX_ADDRESS_LENGTH = 255;
    public static final int MAX_CITY_LENGTH = 100;
    public static final int MAX_STATE_LENGTH = 50;
    public static final int MAX_MESSAGE_LENGTH = 1000;
    public static final int MIN_SEARCH_TERM_LENGTH = 2;
    public static final int MAX_SEARCH_TERM_LENGTH = 255;

    // API paths
    public static final String API_BASE = "/api";
    public static final String API_ADMIN = API_BASE + "/admin";
    public static final String API_AUTH = API_BASE + "/auth";
    public static final String API_PROPERTIES = API_BASE + "/properties";
    public static final String API_INQUIRIES = API_BASE + "/inquiries";
}
