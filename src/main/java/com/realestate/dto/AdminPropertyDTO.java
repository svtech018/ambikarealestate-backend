package com.realestate.dto;

import com.realestate.common.constants.AreaUnit;
import com.realestate.common.constants.ListingType;
import com.realestate.common.constants.PropertyStatus;
import com.realestate.common.constants.PropertyType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPropertyDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    private String description;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    @NotNull(message = "Listing type is required")
    private ListingType listingType;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
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
    private PropertyStatus status;
    private Boolean featured;
    private Long viewsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
