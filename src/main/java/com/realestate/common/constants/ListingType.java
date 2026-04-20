package com.realestate.common.constants;

public enum ListingType {
    SALE("For Sale"),
    RENT("For Rent");

    private final String displayName;

    ListingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ListingType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ListingType value cannot be null or empty");
        }
        for (ListingType type : ListingType.values()) {
            if (type.name().equalsIgnoreCase(value.trim()) ||
                    type.displayName.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ListingType value: " + value);
    }
}
