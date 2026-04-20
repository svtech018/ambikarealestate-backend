package com.realestate.common.constants;

public enum PropertyStatus {
    ACTIVE("Active"),
    PENDING("Pending"),
    SOLD("Sold"),
    WITHDRAWN("Withdrawn");

    private final String displayName;

    PropertyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PropertyStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("PropertyStatus value cannot be null or empty");
        }
        for (PropertyStatus status : PropertyStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim()) ||
                    status.displayName.equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PropertyStatus value: " + value);
    }

    public boolean isAvailable() {
        return this == ACTIVE || this == PENDING;
    }
}
