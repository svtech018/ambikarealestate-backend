package com.realestate.dto;

import com.realestate.common.constants.AreaUnit;
import com.realestate.common.constants.PropertyType;
import com.realestate.common.constants.ListingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPropertyDTO {

    private Long id;
    private String title;
    private String description;
    private PropertyType propertyType;
    private ListingType listingType;
    private BigDecimal price;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private BigDecimal area;
    private AreaUnit areaUnit;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer parkingSpaces;
    private Integer yearBuilt;
    private List<String> imageUrls;
    private String youtubeVideoUrl;
    private List<String> amenities;
    private Boolean featured;
    private Long viewsCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public String getFormattedPrice() {
        if (price == null) {
            return "Price not available";
        }
        return String.format("₹%,.2f", price);
    }

    public String getFormattedArea() {
        if (area == null) {
            return "Area not available";
        }
        return String.format("%.0f sq ft", area);
    }

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        if (address != null && !address.trim().isEmpty()) {
            fullAddress.append(address);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (fullAddress.length() > 0)
                fullAddress.append(", ");
            fullAddress.append(city);
        }
        if (state != null && !state.trim().isEmpty()) {
            if (fullAddress.length() > 0)
                fullAddress.append(", ");
            fullAddress.append(state);
        }
        if (zipCode != null && !zipCode.trim().isEmpty()) {
            if (fullAddress.length() > 0)
                fullAddress.append(" - ");
            fullAddress.append(zipCode);
        }
        return fullAddress.toString();
    }

    public String getPropertySummary() {
        StringBuilder summary = new StringBuilder();
        if (bedrooms != null && bedrooms > 0) {
            summary.append(bedrooms).append(" BHK");
        }
        if (bathrooms != null && bathrooms > 0) {
            if (summary.length() > 0)
                summary.append(", ");
            summary.append(bathrooms).append(" Bath");
        }
        if (area != null) {
            if (summary.length() > 0)
                summary.append(", ");
            summary.append(getFormattedArea());
        }
        return summary.toString();
    }

    public boolean hasImages() {
        return imageUrls != null && !imageUrls.isEmpty();
    }

    public String getThumbnailImageUrl() {
        if (hasImages()) {
            return imageUrls.get(0);
        }
        return null;
    }

    public boolean hasVideo() {
        return youtubeVideoUrl != null && !youtubeVideoUrl.trim().isEmpty();
    }

    public boolean hasAmenities() {
        return amenities != null && !amenities.isEmpty();
    }

    public String getAmenitiesString() {
        if (hasAmenities()) {
            return String.join(", ", amenities);
        }
        return "No amenities listed";
    }

    public boolean isFeatured() {
        return featured != null && featured;
    }

    public String getFormattedViewsCount() {
        if (viewsCount == null || viewsCount == 0) {
            return "No views yet";
        }
        if (viewsCount == 1) {
            return "1 view";
        }
        return String.format("%,d views", viewsCount);
    }

    public Integer getPropertyAge() {
        if (yearBuilt != null) {
            int currentYear = LocalDateTime.now().getYear();
            return currentYear - yearBuilt;
        }
        return null;
    }

    public String getPropertyAgeDescription() {
        Integer age = getPropertyAge();
        if (age == null) {
            return "Year built not available";
        }
        if (age == 0) {
            return "Brand new";
        } else if (age == 1) {
            return "1 year old";
        } else {
            return age + " years old";
        }
    }
}
