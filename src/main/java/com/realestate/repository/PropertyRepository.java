package com.realestate.repository;

import com.realestate.common.constants.ListingType;
import com.realestate.common.constants.PropertyStatus;
import com.realestate.common.constants.PropertyType;
import com.realestate.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByStatus(PropertyStatus status, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByPropertyType(PropertyType propertyType, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByListingType(ListingType listingType, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByCityIgnoreCase(String city, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByStateIgnoreCase(String state, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByCityIgnoreCaseAndStateIgnoreCase(String city, String state, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByFeatured(Boolean featured, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByFeaturedAndStatus(Boolean featured, PropertyStatus status, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        @Query("SELECT p FROM Property p WHERE " +
                        "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
                        "(:listingType IS NULL OR p.listingType = :listingType) AND " +
                        "(:status IS NULL OR p.status = :status) AND " +
                        "(:city IS NULL OR LOWER(p.city) = LOWER(:city)) AND " +
                        "(:state IS NULL OR LOWER(p.state) = LOWER(:state)) AND " +
                        "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
                        "(:maxPrice IS NULL OR p.price <= :maxPrice)")
        Page<Property> findPropertiesWithFilters(
                        @Param("propertyType") PropertyType propertyType,
                        @Param("listingType") ListingType listingType,
                        @Param("status") PropertyStatus status,
                        @Param("city") String city,
                        @Param("state") String state,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        @Query("SELECT p FROM Property p WHERE " +
                        "LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(p.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        Page<Property> searchProperties(@Param("searchTerm") String searchTerm, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        @Query("SELECT p FROM Property p WHERE p.createdAt BETWEEN :startDate AND :endDate")
        Page<Property> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByBedrooms(Integer bedrooms, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByBathrooms(Integer bathrooms, Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        Page<Property> findByAreaBetween(BigDecimal minArea, BigDecimal maxArea, Pageable pageable);

        long countByStatus(PropertyStatus status);

        long countByPropertyType(PropertyType propertyType);

        long countByListingType(ListingType listingType);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        @Query("SELECT p FROM Property p WHERE p.status = 'ACTIVE' ORDER BY p.viewsCount DESC")
        Page<Property> findTopViewedProperties(Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        @Query("SELECT p FROM Property p WHERE p.status = :status ORDER BY p.createdAt DESC")
        Page<Property> findRecentProperties(@Param("status") PropertyStatus status, Pageable pageable);

        Page<Property> findByZipCode(String zipCode, Pageable pageable);

        @Query("SELECT DISTINCT p FROM Property p JOIN p.propertyImages pi")
        Page<Property> findPropertiesWithImages(Pageable pageable);

        @Query("SELECT p FROM Property p WHERE p.youtubeVideoUrl IS NOT NULL AND p.youtubeVideoUrl != ''")
        Page<Property> findPropertiesWithVideos(Pageable pageable);

        @EntityGraph(value = "Property.withImages", type = EntityGraph.EntityGraphType.LOAD)
        @Query("SELECT p FROM Property p WHERE p.id = :id")
        Optional<Property> findByIdWithImages(@Param("id") Long id);
}
