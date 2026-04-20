package com.realestate.common.constants;

public enum PropertyType {
    LAND_PLOTS("Land/Plots"),
    HOUSES_BUNGALOWS("Houses/Bungalows"),
    APARTMENTS_FLATS("Apartments/Flats"),
    COMMERCIAL_SHOPS("Commercial Shops/Spaces"),
    COFFEE_ESTATES("Coffee Estates"),
    AGRICULTURE_LANDS("Agriculture Lands"),
    RESORT_SPA("Resort/Spa");

    private final String displayName;

    PropertyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PropertyType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("PropertyType value cannot be null or empty");
        }
        for (PropertyType type : PropertyType.values()) {
            if (type.name().equalsIgnoreCase(value.trim()) ||
                    type.displayName.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid PropertyType value: " + value);
    }
}
