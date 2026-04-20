package com.realestate.user.controller;

import com.realestate.user.service.UserPropertyService;
import com.realestate.user.validation.PropertyFilterValidation;
import com.realestate.util.PropertySearchValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.realestate.common.constants.ListingType;
import com.realestate.common.constants.PropertyType;
import com.realestate.dto.ApiResponse;
import com.realestate.dto.PropertySearchCriteria;
import com.realestate.dto.UserPropertyDTO;
import com.realestate.entity.PropertyImage;
import com.realestate.exception.PropertyValidationException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

/**
 * REST Controller for User Property operations
 * Handles HTTP requests for property viewing, searching, and filtering
 * Provides public endpoints for property discovery without authentication
 */
@RestController
@RequestMapping("/api/properties")
@Validated
@Tag(name = "Property Search", description = "Property search and filtering operations for users")
public class UserPropertyController {

    private final UserPropertyService userPropertyService;
    private final PropertySearchValidator propertySearchValidator;
    private final PropertyFilterValidation propertyFilterValidation;

    /**
     * Constructor-based dependency injection for better testability and
     * immutability
     */
    public UserPropertyController(
            @Autowired UserPropertyService userPropertyService,
            @Autowired PropertySearchValidator propertySearchValidator,
            @Autowired PropertyFilterValidation propertyFilterValidation) {
        this.userPropertyService = Objects.requireNonNull(userPropertyService, "UserPropertyService cannot be null");
        this.propertySearchValidator = Objects.requireNonNull(propertySearchValidator,
                "PropertySearchValidator cannot be null");
        this.propertyFilterValidation = Objects.requireNonNull(propertyFilterValidation,
                "PropertyFilterValidation cannot be null");
    }

    /**
     * Get all properties with advanced filtering and sorting
     * Supports multiple query parameters for comprehensive property search
     */
    @GetMapping
    @Operation(summary = "Search properties with advanced filters", description = "Retrieve properties with comprehensive filtering options including price range, location, property type, and more")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Properties retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Page<UserPropertyDTO>>> getAllProperties(
            @Parameter(description = "Minimum price filter") @RequestParam(required = false) BigDecimal priceMin,
            @Parameter(description = "Maximum price filter") @RequestParam(required = false) BigDecimal priceMax,
            @Parameter(description = "City filter") @RequestParam(required = false) String city,
            @Parameter(description = "State filter") @RequestParam(required = false) String state,
            @Parameter(description = "ZIP code filter") @RequestParam(required = false) String zipCode,
            @Parameter(description = "Property type filter") @RequestParam(required = false) String type,
            @Parameter(description = "Property type filter") @RequestParam(required = false) String propertyType,
            @Parameter(description = "Listing type filter") @RequestParam(required = false) String listingType,
            @Parameter(description = "Minimum area filter") @RequestParam(required = false) BigDecimal areaMin,
            @Parameter(description = "Maximum area filter") @RequestParam(required = false) BigDecimal areaMax,
            @Parameter(description = "Minimum bedrooms") @RequestParam(required = false) Integer minBedrooms,
            @Parameter(description = "Maximum bedrooms") @RequestParam(required = false) Integer maxBedrooms,
            @Parameter(description = "Minimum bathrooms") @RequestParam(required = false) Integer minBathrooms,
            @Parameter(description = "Maximum bathrooms") @RequestParam(required = false) Integer maxBathrooms,
            @Parameter(description = "Minimum parking spaces") @RequestParam(required = false) Integer minParkingSpaces,
            @Parameter(description = "Maximum parking spaces") @RequestParam(required = false) Integer maxParkingSpaces,
            @Parameter(description = "Minimum year built") @RequestParam(required = false) Integer minYearBuilt,
            @Parameter(description = "Maximum year built") @RequestParam(required = false) Integer maxYearBuilt,
            @Parameter(description = "Featured properties only") @RequestParam(required = false) Boolean featured,
            @Parameter(description = "Search term for text search") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Sort field (price, createdAt, area, viewsCount)") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc, desc)") @RequestParam(defaultValue = "desc") String sortDirection,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (1-100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        try {
            // Validate pagination parameters
            propertySearchValidator.validatePaginationParameters(page, size);

            // Build search criteria
            PropertySearchCriteria criteria = buildSearchCriteria(
                    priceMin, priceMax, city, state, zipCode, resolvePropertyTypeParam(type, propertyType), listingType,
                    areaMin, areaMax, minBedrooms, maxBedrooms, minBathrooms, maxBathrooms,
                    minParkingSpaces, maxParkingSpaces, minYearBuilt, maxYearBuilt,
                    featured, searchTerm, sortBy, sortDirection);

            // Validate search criteria
            propertySearchValidator.validateSearchCriteria(criteria);
            propertyFilterValidation.validateCombinedFilters(criteria);

            // Create pageable
            Pageable pageable = PageRequest.of(page, size);

            // Execute search
            Page<UserPropertyDTO> properties = userPropertyService.searchProperties(criteria, pageable);

            ApiResponse<Page<UserPropertyDTO>> response = new ApiResponse<>(
                    true,
                    "Properties fetched successfully",
                    properties);

            return ResponseEntity.ok(response);

        } catch (PropertyValidationException e) {
            ApiResponse<Page<UserPropertyDTO>> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            ApiResponse<Page<UserPropertyDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to search properties: " + e.getMessage(),
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get property by ID
     */
    @GetMapping("/{propertyId}")
    @Operation(summary = "Get property by ID", description = "Retrieve detailed information about a specific property")
    public ResponseEntity<ApiResponse<UserPropertyDTO>> getPropertyById(
            @Parameter(description = "Property ID") @PathVariable @NotNull Long propertyId) {

        try {
            UserPropertyDTO property = userPropertyService.getPropertyById(propertyId);
            ApiResponse<UserPropertyDTO> response = new ApiResponse<>(
                    true,
                    "Property fetched successfully",
                    property);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            ApiResponse<UserPropertyDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            ApiResponse<UserPropertyDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to fetch property: " + e.getMessage(),
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get featured properties
     */
    @GetMapping("/featured")
    @Operation(summary = "Get featured properties", description = "Retrieve all featured properties with pagination")
    public ResponseEntity<ApiResponse<Page<UserPropertyDTO>>> getFeaturedProperties(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (1-100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        try {
            propertySearchValidator.validatePaginationParameters(page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<UserPropertyDTO> properties = userPropertyService.getFeaturedProperties(pageable);

            ApiResponse<Page<UserPropertyDTO>> response = new ApiResponse<>(
                    true,
                    "Featured properties fetched successfully",
                    properties);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Page<UserPropertyDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to fetch featured properties: " + e.getMessage(),
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get recent properties
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent properties", description = "Retrieve recently added properties sorted by creation date")
    public ResponseEntity<ApiResponse<Page<UserPropertyDTO>>> getRecentProperties(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (1-100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        try {
            propertySearchValidator.validatePaginationParameters(page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<UserPropertyDTO> properties = userPropertyService.getRecentProperties(pageable);

            ApiResponse<Page<UserPropertyDTO>> response = new ApiResponse<>(
                    true,
                    "Recent properties fetched successfully",
                    properties);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Page<UserPropertyDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to fetch recent properties: " + e.getMessage(),
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/images/{imageId}")
    @Operation(summary = "Get property image", description = "Returns a stored property image as binary content")
    public ResponseEntity<byte[]> getPropertyImage(
            @Parameter(description = "Property image ID") @PathVariable Long imageId) {

        PropertyImage image = userPropertyService.getPropertyImageById(imageId);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            mediaType = MediaType.parseMediaType(image.getContentType());
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + (image.getFileName() != null ? image.getFileName() : "property-image")
                                + "\"")
                .body(image.getImageData());
    }

    /**
     * Get properties by type
     */
    @GetMapping("/type/{propertyType}")
    @Operation(summary = "Get properties by type", description = "Retrieve properties filtered by property type")
    public ResponseEntity<ApiResponse<Page<UserPropertyDTO>>> getPropertiesByType(
            @Parameter(description = "Property type (PLOT, VILLA, APARTMENT, COMMERCIAL)") @PathVariable String propertyType,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (1-100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        try {
            propertySearchValidator.validatePaginationParameters(page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<UserPropertyDTO> properties = userPropertyService.getPropertiesByType(propertyType, pageable);

            ApiResponse<Page<UserPropertyDTO>> response = new ApiResponse<>(
                    true,
                    "Properties fetched successfully",
                    properties);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            ApiResponse<Page<UserPropertyDTO>> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            ApiResponse<Page<UserPropertyDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to fetch properties: " + e.getMessage(),
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search properties by text
     */
    @GetMapping("/search")
    @Operation(summary = "Search properties by text", description = "Search properties using text search across title, description, address, and amenities")
    public ResponseEntity<ApiResponse<Page<UserPropertyDTO>>> searchPropertiesByText(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (1-100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        try {
            propertySearchValidator.validatePaginationParameters(page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<UserPropertyDTO> properties = userPropertyService.searchPropertiesByText(searchTerm, pageable);

            ApiResponse<Page<UserPropertyDTO>> response = new ApiResponse<>(
                    true,
                    "Properties searched successfully",
                    properties);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Page<UserPropertyDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to search properties: " + e.getMessage(),
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method to build search criteria from request parameters
     */
    private PropertySearchCriteria buildSearchCriteria(
            BigDecimal priceMin, BigDecimal priceMax, String city, String state, String zipCode,
            String propertyTypeValue, String listingType, BigDecimal areaMin, BigDecimal areaMax,
            Integer minBedrooms, Integer maxBedrooms, Integer minBathrooms, Integer maxBathrooms,
            Integer minParkingSpaces, Integer maxParkingSpaces, Integer minYearBuilt, Integer maxYearBuilt,
            Boolean featured, String searchTerm, String sortBy, String sortDirection) {

        PropertySearchCriteria.PropertySearchCriteriaBuilder builder = PropertySearchCriteria.builder()
                .priceMin(priceMin)
                .priceMax(priceMax)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .areaMin(areaMin)
                .areaMax(areaMax)
                .minBedrooms(minBedrooms)
                .maxBedrooms(maxBedrooms)
                .minBathrooms(minBathrooms)
                .maxBathrooms(maxBathrooms)
                .minParkingSpaces(minParkingSpaces)
                .maxParkingSpaces(maxParkingSpaces)
                .minYearBuilt(minYearBuilt)
                .maxYearBuilt(maxYearBuilt)
                .featured(featured)
                .searchTerm(searchTerm)
                .sortBy(sortBy)
                .sortDirection(sortDirection);

        // Parse property type
        if (propertyTypeValue != null && !propertyTypeValue.trim().isEmpty()) {
            try {
            builder.propertyType(PropertyType.fromString(propertyTypeValue));
            } catch (IllegalArgumentException e) {
                throw new PropertyValidationException(
                        "Invalid property type. Valid values are: " +
                                java.util.Arrays.stream(PropertyType.values()).map(Enum::name)
                                        .collect(java.util.stream.Collectors.joining(", ")));
            }
        }

        // Parse listing type
        if (listingType != null && !listingType.trim().isEmpty()) {
            try {
                ListingType listingTypeEnum = ListingType.valueOf(listingType.toUpperCase());
                builder.listingType(listingTypeEnum);
            } catch (IllegalArgumentException e) {
                throw new PropertyValidationException(
                        "Invalid listing type. Valid values are: " +
                                java.util.Arrays.stream(ListingType.values()).map(Enum::name)
                                        .collect(java.util.stream.Collectors.joining(", ")));
            }
        }

        return builder.build();
    }

    private String resolvePropertyTypeParam(String type, String propertyType) {
        String rawValue = propertyType != null && !propertyType.trim().isEmpty() ? propertyType : type;
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }

        return switch (rawValue.trim().toUpperCase()) {
            case "RESIDENTIAL" -> "HOUSES_BUNGALOWS";
            case "COMMERCIAL", "INDUSTRIAL" -> "COMMERCIAL_SHOPS";
            case "LAND" -> "LAND_PLOTS";
            default -> rawValue;
        };
    }
}