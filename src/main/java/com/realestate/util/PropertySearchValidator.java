package com.realestate.util;

import com.realestate.exception.PropertyValidationException;
import com.realestate.dto.PropertySearchCriteria;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Utility class for validating property search criteria
 * Provides comprehensive validation logic for search parameters
 */
@Component
public class PropertySearchValidator {

    /**
     * Validates the property search criteria
     * 
     * @param criteria the search criteria to validate
     * @throws PropertyValidationException if validation fails
     */
    public void validateSearchCriteria(PropertySearchCriteria criteria) {
        if (criteria == null) {
            return; // Null criteria is acceptable for getting all properties
        }

        validatePriceRange(criteria.getPriceMin(), criteria.getPriceMax());
        validateAreaRange(criteria.getAreaMin(), criteria.getAreaMax());
        validateBedroomRange(criteria.getMinBedrooms(), criteria.getMaxBedrooms());
        validateBathroomRange(criteria.getMinBathrooms(), criteria.getMaxBathrooms());
        validateParkingSpacesRange(criteria.getMinParkingSpaces(), criteria.getMaxParkingSpaces());
        validateYearBuiltRange(criteria.getMinYearBuilt(), criteria.getMaxYearBuilt());
        validateSearchTerm(criteria.getSearchTerm());
        validateSortParameters(criteria.getSortBy(), criteria.getSortDirection());
    }

    /**
     * Validates price range
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @throws PropertyValidationException if price range is invalid
     */
    private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new PropertyValidationException("Minimum price cannot be negative");
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new PropertyValidationException("Maximum price cannot be negative");
        }

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new PropertyValidationException("Minimum price cannot be greater than maximum price");
        }

        // Reasonable upper limit for price (e.g., 1 billion)
        BigDecimal maxAllowedPrice = new BigDecimal("1000000000");
        if (minPrice != null && minPrice.compareTo(maxAllowedPrice) > 0) {
            throw new PropertyValidationException("Minimum price exceeds maximum allowed limit");
        }
        if (maxPrice != null && maxPrice.compareTo(maxAllowedPrice) > 0) {
            throw new PropertyValidationException("Maximum price exceeds maximum allowed limit");
        }
    }

    /**
     * Validates area range
     * 
     * @param minArea minimum area
     * @param maxArea maximum area
     * @throws PropertyValidationException if area range is invalid
     */
    private void validateAreaRange(BigDecimal minArea, BigDecimal maxArea) {
        if (minArea != null && minArea.compareTo(BigDecimal.ZERO) < 0) {
            throw new PropertyValidationException("Minimum area cannot be negative");
        }

        if (maxArea != null && maxArea.compareTo(BigDecimal.ZERO) < 0) {
            throw new PropertyValidationException("Maximum area cannot be negative");
        }

        if (minArea != null && maxArea != null && minArea.compareTo(maxArea) > 0) {
            throw new PropertyValidationException("Minimum area cannot be greater than maximum area");
        }

        // Reasonable upper limit for area (e.g., 100,000 sq ft)
        BigDecimal maxAllowedArea = new BigDecimal("100000");
        if (minArea != null && minArea.compareTo(maxAllowedArea) > 0) {
            throw new PropertyValidationException("Minimum area exceeds maximum allowed limit");
        }
        if (maxArea != null && maxArea.compareTo(maxAllowedArea) > 0) {
            throw new PropertyValidationException("Maximum area exceeds maximum allowed limit");
        }
    }

    /**
     * Validates bedroom range
     * 
     * @param minBedrooms minimum bedrooms
     * @param maxBedrooms maximum bedrooms
     * @throws PropertyValidationException if bedroom range is invalid
     */
    private void validateBedroomRange(Integer minBedrooms, Integer maxBedrooms) {
        if (minBedrooms != null && minBedrooms < 0) {
            throw new PropertyValidationException("Minimum bedrooms cannot be negative");
        }

        if (maxBedrooms != null && maxBedrooms < 0) {
            throw new PropertyValidationException("Maximum bedrooms cannot be negative");
        }

        if (minBedrooms != null && maxBedrooms != null && minBedrooms > maxBedrooms) {
            throw new PropertyValidationException("Minimum bedrooms cannot be greater than maximum bedrooms");
        }

        // Reasonable upper limit for bedrooms (e.g., 20)
        int maxAllowedBedrooms = 20;
        if (minBedrooms != null && minBedrooms > maxAllowedBedrooms) {
            throw new PropertyValidationException("Minimum bedrooms exceeds maximum allowed limit");
        }
        if (maxBedrooms != null && maxBedrooms > maxAllowedBedrooms) {
            throw new PropertyValidationException("Maximum bedrooms exceeds maximum allowed limit");
        }
    }

    /**
     * Validates bathroom range
     * 
     * @param minBathrooms minimum bathrooms
     * @param maxBathrooms maximum bathrooms
     * @throws PropertyValidationException if bathroom range is invalid
     */
    private void validateBathroomRange(Integer minBathrooms, Integer maxBathrooms) {
        if (minBathrooms != null && minBathrooms < 0) {
            throw new PropertyValidationException("Minimum bathrooms cannot be negative");
        }

        if (maxBathrooms != null && maxBathrooms < 0) {
            throw new PropertyValidationException("Maximum bathrooms cannot be negative");
        }

        if (minBathrooms != null && maxBathrooms != null && minBathrooms > maxBathrooms) {
            throw new PropertyValidationException("Minimum bathrooms cannot be greater than maximum bathrooms");
        }

        // Reasonable upper limit for bathrooms (e.g., 15)
        int maxAllowedBathrooms = 15;
        if (minBathrooms != null && minBathrooms > maxAllowedBathrooms) {
            throw new PropertyValidationException("Minimum bathrooms exceeds maximum allowed limit");
        }
        if (maxBathrooms != null && maxBathrooms > maxAllowedBathrooms) {
            throw new PropertyValidationException("Maximum bathrooms exceeds maximum allowed limit");
        }
    }

    /**
     * Validates parking spaces range
     * 
     * @param minParkingSpaces minimum parking spaces
     * @param maxParkingSpaces maximum parking spaces
     * @throws PropertyValidationException if parking spaces range is invalid
     */
    private void validateParkingSpacesRange(Integer minParkingSpaces, Integer maxParkingSpaces) {
        if (minParkingSpaces != null && minParkingSpaces < 0) {
            throw new PropertyValidationException("Minimum parking spaces cannot be negative");
        }

        if (maxParkingSpaces != null && maxParkingSpaces < 0) {
            throw new PropertyValidationException("Maximum parking spaces cannot be negative");
        }

        if (minParkingSpaces != null && maxParkingSpaces != null && minParkingSpaces > maxParkingSpaces) {
            throw new PropertyValidationException(
                    "Minimum parking spaces cannot be greater than maximum parking spaces");
        }

        // Reasonable upper limit for parking spaces (e.g., 50)
        int maxAllowedParkingSpaces = 50;
        if (minParkingSpaces != null && minParkingSpaces > maxAllowedParkingSpaces) {
            throw new PropertyValidationException("Minimum parking spaces exceeds maximum allowed limit");
        }
        if (maxParkingSpaces != null && maxParkingSpaces > maxAllowedParkingSpaces) {
            throw new PropertyValidationException("Maximum parking spaces exceeds maximum allowed limit");
        }
    }

    /**
     * Validates year built range
     * 
     * @param minYearBuilt minimum year built
     * @param maxYearBuilt maximum year built
     * @throws PropertyValidationException if year built range is invalid
     */
    private void validateYearBuiltRange(Integer minYearBuilt, Integer maxYearBuilt) {
        int currentYear = java.time.LocalDateTime.now().getYear();
        int minAllowedYear = 1800; // Reasonable minimum year

        if (minYearBuilt != null && minYearBuilt < minAllowedYear) {
            throw new PropertyValidationException("Minimum year built cannot be before " + minAllowedYear);
        }

        if (maxYearBuilt != null && maxYearBuilt < minAllowedYear) {
            throw new PropertyValidationException("Maximum year built cannot be before " + minAllowedYear);
        }

        if (minYearBuilt != null && minYearBuilt > currentYear + 5) {
            throw new PropertyValidationException("Minimum year built cannot be more than 5 years in the future");
        }

        if (maxYearBuilt != null && maxYearBuilt > currentYear + 5) {
            throw new PropertyValidationException("Maximum year built cannot be more than 5 years in the future");
        }

        if (minYearBuilt != null && maxYearBuilt != null && minYearBuilt > maxYearBuilt) {
            throw new PropertyValidationException("Minimum year built cannot be greater than maximum year built");
        }
    }

    /**
     * Validates search term
     * 
     * @param searchTerm the search term
     * @throws PropertyValidationException if search term is invalid
     */
    private void validateSearchTerm(String searchTerm) {
        if (searchTerm != null) {
            if (searchTerm.trim().length() < 2 && !searchTerm.trim().isEmpty()) {
                throw new PropertyValidationException("Search term must be at least 2 characters long");
            }

            if (searchTerm.length() > 255) {
                throw new PropertyValidationException("Search term cannot exceed 255 characters");
            }

            // Check for potentially harmful patterns
            if (searchTerm.matches(".*[<>\"'%;()&+]")) {
                throw new PropertyValidationException("Search term contains invalid characters");
            }
        }
    }

    /**
     * Validates sort parameters
     * 
     * @param sortBy        the sort field
     * @param sortDirection the sort direction
     * @throws PropertyValidationException if sort parameters are invalid
     */
    private void validateSortParameters(String sortBy, String sortDirection) {
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String[] validSortFields = { "price", "createdAt", "area", "viewsCount", "title", "city" };
            boolean isValidSortField = false;

            for (String validField : validSortFields) {
                if (validField.equalsIgnoreCase(sortBy.trim())) {
                    isValidSortField = true;
                    break;
                }
            }

            if (!isValidSortField) {
                throw new PropertyValidationException(
                        "Invalid sort field. Valid options are: price, createdAt, area, viewsCount, title, city");
            }
        }

        if (sortDirection != null && !sortDirection.trim().isEmpty()) {
            String direction = sortDirection.trim().toLowerCase();
            if (!"asc".equals(direction) && !"desc".equals(direction)) {
                throw new PropertyValidationException(
                        "Invalid sort direction. Valid options are: asc, desc");
            }
        }
    }

    /**
     * Validates pagination parameters
     * 
     * @param page the page number
     * @param size the page size
     * @throws PropertyValidationException if pagination parameters are invalid
     */
    public void validatePaginationParameters(int page, int size) {
        if (page < 0) {
            throw new PropertyValidationException("Page number cannot be negative");
        }

        if (size <= 0) {
            throw new PropertyValidationException("Page size must be greater than 0");
        }

        if (size > 100) {
            throw new PropertyValidationException("Page size cannot exceed 100");
        }
    }
}