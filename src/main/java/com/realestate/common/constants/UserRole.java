package com.realestate.common.constants;

public enum UserRole {
    USER("User"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("UserRole value cannot be null or empty");
        }
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(value.trim()) ||
                    role.displayName.equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid UserRole value: " + value);
    }
}
