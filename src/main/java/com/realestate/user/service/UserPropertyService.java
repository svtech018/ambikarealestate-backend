package com.realestate.user.service;

import com.realestate.dto.PropertySearchCriteria;
import com.realestate.dto.UserPropertyDTO;
import com.realestate.entity.PropertyImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for User Property operations
 * Defines business logic methods for property viewing and searching in user
 * module
 */
public interface UserPropertyService {

    /**
     * Search properties with advanced filtering and sorting
     * 
     * @param searchCriteria the search criteria containing all filters
     * @param pageable       pagination and sorting information
     * @return Page of properties matching the search criteria
     */
    Page<UserPropertyDTO> searchProperties(PropertySearchCriteria searchCriteria, Pageable pageable);

    /**
     * Get property by ID
     * 
     * @param propertyId the property ID
     * @return property details
     * @throws RuntimeException if property not found
     */
    UserPropertyDTO getPropertyById(Long propertyId);

    /**
     * Get featured properties
     * 
     * @param pageable pagination information
     * @return Page of featured properties
     */
    Page<UserPropertyDTO> getFeaturedProperties(Pageable pageable);

    /**
     * Get recently added properties
     * 
     * @param pageable pagination information
     * @return Page of recent properties
     */
    Page<UserPropertyDTO> getRecentProperties(Pageable pageable);

    /**
     * Get properties by type
     * 
     * @param propertyType the property type
     * @param pageable     pagination information
     * @return Page of properties of the specified type
     */
    Page<UserPropertyDTO> getPropertiesByType(String propertyType, Pageable pageable);

    /**
     * Get properties by city
     * 
     * @param city     the city name
     * @param pageable pagination information
     * @return Page of properties in the specified city
     */
    Page<UserPropertyDTO> getPropertiesByCity(String city, Pageable pageable);

    /**
     * Get properties by price range
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination information
     * @return Page of properties within the price range
     */
    Page<UserPropertyDTO> getPropertiesByPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Search properties by text
     * 
     * @param searchTerm the search term
     * @param pageable   pagination information
     * @return Page of properties matching the search term
     */
    Page<UserPropertyDTO> searchPropertiesByText(String searchTerm, Pageable pageable);

    /**
     * Increment view count for a property
     * 
     * @param propertyId the property ID
     */
    void incrementViewCount(Long propertyId);

    PropertyImage getPropertyImageById(Long imageId);
}