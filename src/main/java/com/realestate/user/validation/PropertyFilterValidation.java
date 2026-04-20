package com.realestate.user.validation;

import com.realestate.common.constants.ListingType;
import com.realestate.common.constants.PropertyType;
import com.realestate.exception.PropertyValidationException;
import com.realestate.dto.PropertySearchCriteria;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Validation component for property filtering operations
 * Ensures that combined filters work correctly and validates complex filter
 * scenarios
 */
@Component
public class PropertyFilterValidation {

    /**
     * Validates combined filtering scenarios
     * 
     * @param criteria the search criteria with multiple filters
     * @throws PropertyValidationException if validation fails
     */
    public void validateCombinedFilters(PropertySearchCriteria criteria) {
        if (criteria == null) {
            return;
        }

        List<String> validationErrors = new ArrayList<>();

        // Validate price range within city/location context
        validatePriceRangeWithLocation(criteria, validationErrors);

        // Validate property type compatibility with other filters
        validatePropertyTypeCompatibility(criteria, validationErrors);

        // Validate area range with property type
        validateAreaRangeWithPropertyType(criteria, validationErrors);

        // Validate bedroom/bathroom combination
        validateBedroomBathroomCombination(criteria, validationErrors);

        // Validate year built with property type
        validateYearBuiltWithPropertyType(criteria, validationErrors);

        // Validate listing type with property type
        validateListingTypeWithPropertyType(criteria, validationErrors);

        // Validate search term with other filters
        validateSearchTermWithFilters(criteria, validationErrors);

        if (!validationErrors.isEmpty()) {
            throw new PropertyValidationException(
                    "Combined filter validation failed: " + String.join(", ", validationErrors));
        }
    }

    /**
     * Validates price range within location context
     */
    private void validatePriceRangeWithLocation(PropertySearchCriteria criteria, List<String> errors) {
        if (criteria.getPriceMin() != null && criteria.getPriceMax() != null) {
            BigDecimal priceRange = criteria.getPriceMax().subtract(criteria.getPriceMin());

            if (criteria.getPropertyType() == PropertyType.COMMERCIAL_SHOPS ||
                    criteria.getPropertyType() == PropertyType.RESORT_SPA) {
                if (priceRange.compareTo(new BigDecimal("100000000")) > 0) {
                    errors.add("Price range for commercial-style properties is too wide (max 100M difference)");
                }
            } else {
                if (priceRange.compareTo(new BigDecimal("75000000")) > 0) {
                    errors.add("Selected price range is too wide (max 75M difference)");
                }
            }

            if ((criteria.getPropertyType() == PropertyType.LAND_PLOTS ||
                    criteria.getPropertyType() == PropertyType.AGRICULTURE_LANDS) &&
                    criteria.getPriceMin() != null) {
                if (criteria.getPriceMin().compareTo(new BigDecimal("100000")) < 0) {
                    errors.add("Minimum price for land should be at least 1 Lakh");
                }
            }
        }
    }

    /**
     * Validates property type compatibility with other filters
     */
    private void validatePropertyTypeCompatibility(PropertySearchCriteria criteria, List<String> errors) {
        if (criteria.getPropertyType() != null) {
            if (criteria.getPropertyType() == PropertyType.COMMERCIAL_SHOPS ||
                    criteria.getPropertyType() == PropertyType.COFFEE_ESTATES ||
                    criteria.getPropertyType() == PropertyType.RESORT_SPA) {
                if (criteria.getMinBedrooms() != null || criteria.getMaxBedrooms() != null) {
                    errors.add("Commercial-style properties cannot be filtered by bedrooms");
                }
                if (criteria.getMinBathrooms() != null || criteria.getMaxBathrooms() != null) {
                    errors.add("Commercial-style properties cannot be filtered by bathrooms");
                }
            }

            if (criteria.getPropertyType() == PropertyType.LAND_PLOTS ||
                    criteria.getPropertyType() == PropertyType.AGRICULTURE_LANDS) {
                if (criteria.getMinBedrooms() != null || criteria.getMaxBedrooms() != null ||
                        criteria.getMinBathrooms() != null || criteria.getMaxBathrooms() != null) {
                    errors.add("Land properties typically don't have bedrooms or bathrooms");
                }
            }
        }
    }

    /**
     * Validates area range with property type
     */
    private void validateAreaRangeWithPropertyType(PropertySearchCriteria criteria, List<String> errors) {
        if (criteria.getAreaMin() != null && criteria.getPropertyType() != null) {
            switch (criteria.getPropertyType()) {
                case HOUSES_BUNGALOWS:
                case APARTMENTS_FLATS:
                    if (criteria.getAreaMin().compareTo(new BigDecimal("20000")) > 0) {
                        errors.add("Residential area filter seems too large (max reasonable: 20,000 sq ft)");
                    }
                    break;
                case LAND_PLOTS:
                case AGRICULTURE_LANDS:
                    if (criteria.getAreaMin().compareTo(new BigDecimal("100")) < 0) {
                        errors.add("Land area filter seems too small (min reasonable: 100 sq ft)");
                    }
                    break;
                case COMMERCIAL_SHOPS:
                case COFFEE_ESTATES:
                case RESORT_SPA:
                    if (criteria.getAreaMin().compareTo(new BigDecimal("100000")) > 0) {
                        errors.add("Commercial area filter seems extremely large");
                    }
                    break;
            }
        }
    }

    /**
     * Validates bedroom and bathroom combination
     */
    private void validateBedroomBathroomCombination(PropertySearchCriteria criteria, List<String> errors) {
        if (criteria.getMinBedrooms() != null && criteria.getMinBathrooms() != null) {
            // Generally, bathrooms should not exceed bedrooms by more than 1
            if (criteria.getMinBathrooms() > criteria.getMinBedrooms() + 1) {
                errors.add("Number of bathrooms typically should not exceed bedrooms by more than 1");
            }
        }

        if (criteria.getMaxBedrooms() != null && criteria.getMaxBathrooms() != null) {
            // For maximum values, bathrooms can be more flexible
            if (criteria.getMaxBathrooms() > criteria.getMaxBedrooms() * 2) {
                errors.add("Maximum bathrooms filter seems unrealistic compared to bedrooms");
            }
        }
    }

    /**
     * Validates year built with property type
     */
    private void validateYearBuiltWithPropertyType(PropertySearchCriteria criteria, List<String> errors) {
        if (criteria.getMinYearBuilt() != null && criteria.getPropertyType() != null) {
            int currentYear = java.time.LocalDateTime.now().getYear();

            if ((criteria.getPropertyType() == PropertyType.LAND_PLOTS ||
                    criteria.getPropertyType() == PropertyType.AGRICULTURE_LANDS) &&
                    criteria.getMinYearBuilt() != null) {
                errors.add("Year built filter is not typically applicable to land properties");
            }

            // Validate reasonable year ranges for different property types
            if (criteria.getMinYearBuilt() > currentYear) {
                errors.add("Year built cannot be in the future");
            }
        }
    }

    /**
     * Validates listing type with property type
     */
    private void validateListingTypeWithPropertyType(PropertySearchCriteria criteria, List<String> errors) {
        if (criteria.getListingType() != null && criteria.getPropertyType() != null) {
            if (criteria.getListingType() == ListingType.RENT &&
                    criteria.getPropertyType() == PropertyType.AGRICULTURE_LANDS) {
                errors.add("Agriculture land is rarely listed for rent; verify this filter combination");
            }
        }
    }

    /**
     * Validates search term with other filters for logical consistency
     */
    private void validateSearchTermWithFilters(PropertySearchCriteria criteria, List<String> errors) {
        if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) {
            String searchTerm = criteria.getSearchTerm().toLowerCase().trim();

            // Check for conflicting search terms with property type filters
            if (criteria.getPropertyType() != null) {
                String propertyTypeName = criteria.getPropertyType().name().toLowerCase();

                // If search term contains a different property type than the filter
                for (PropertyType type : PropertyType.values()) {
                    String typeName = type.name().toLowerCase();
                    if (searchTerm.contains(typeName) && !typeName.equals(propertyTypeName)) {
                        errors.add("Search term contains '" + typeName + "' but property type filter is set to '"
                                + propertyTypeName + "'");
                        break;
                    }
                }
            }

            // Check for conflicting search terms with listing type filters
            if (criteria.getListingType() != null) {
                String listingTypeName = criteria.getListingType().name().toLowerCase();

                for (ListingType type : ListingType.values()) {
                    String typeName = type.name().toLowerCase();
                    if (searchTerm.contains(typeName) && !typeName.equals(listingTypeName)) {
                        errors.add("Search term contains '" + typeName + "' but listing type filter is set to '"
                                + listingTypeName + "'");
                        break;
                    }
                }
            }
        }
    }

    /**
     * Validates that the combination of filters will likely return results
     */
    public boolean isFilterCombinationRealistic(PropertySearchCriteria criteria) {
        if (criteria == null || !criteria.hasFilters()) {
            return true;
        }

        // Check if the filter combination is too restrictive
        int filterCount = 0;

        if (criteria.getPriceMin() != null || criteria.getPriceMax() != null)
            filterCount++;
        if (criteria.getCity() != null || criteria.getState() != null)
            filterCount++;
        if (criteria.getPropertyType() != null)
            filterCount++;
        if (criteria.getListingType() != null)
            filterCount++;
        if (criteria.getAreaMin() != null || criteria.getAreaMax() != null)
            filterCount++;
        if (criteria.getMinBedrooms() != null || criteria.getMaxBedrooms() != null)
            filterCount++;
        if (criteria.getMinBathrooms() != null || criteria.getMaxBathrooms() != null)
            filterCount++;
        if (criteria.getFeatured() != null)
            filterCount++;
        if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty())
            filterCount++;

        // If too many specific filters are applied, it might return no results
        return filterCount <= 6; // Allow up to 6 different filter categories
    }
}