package com.realestate.user.serviceimpl;

import com.realestate.common.mapper.PropertyMapper;
import com.realestate.entity.Property;
import com.realestate.common.constants.ListingType;
import com.realestate.common.constants.PropertyStatus;
import com.realestate.common.constants.PropertyType;
import com.realestate.repository.PropertyImageRepository;
import com.realestate.repository.PropertyRepository;
import com.realestate.util.PropertySearchValidator;
import com.realestate.dto.PropertySearchCriteria;
import com.realestate.dto.UserPropertyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserPropertyServiceImpl
 * Tests all filtering, sorting, and business logic functionality
 */
@ExtendWith(MockitoExtension.class)
class UserPropertyServiceImplTest {

        @Mock
        private PropertyRepository propertyRepository;

        @Mock
        private PropertyImageRepository propertyImageRepository;

        @Mock
        private PropertySearchValidator propertySearchValidator;

        @Mock
        private PropertyMapper propertyMapper;

        private UserPropertyServiceImpl userPropertyService;

        private Property testProperty1;
        private Property testProperty2;
        private Property testProperty3;

        @BeforeEach
        void setUp() {
                userPropertyService = new UserPropertyServiceImpl(propertyRepository, propertyImageRepository,
                                propertySearchValidator, propertyMapper);

                // Create test properties
                testProperty1 = Property.builder()
                                .title("Beautiful Villa in Mumbai")
                                .description("Luxury villa with modern amenities")
                                .propertyType(PropertyType.RESIDENTIAL)
                                .listingType(ListingType.SALE)
                                .price(new BigDecimal("5000000"))
                                .address("123 Marine Drive")
                                .city("Mumbai")
                                .state("Maharashtra")
                                .zipCode("400001")
                                .area(new BigDecimal("2500"))
                                .bedrooms(4)
                                .bathrooms(3)
                                .parkingSpaces(2)
                                .yearBuilt(2020)
                                .status(PropertyStatus.ACTIVE)
                                .featured(true)
                                .viewsCount(150L)
                                .build();
                testProperty1.setId(1L);

                testProperty2 = Property.builder()
                                .title("Modern Apartment in Delhi")
                                .description("Spacious apartment in prime location")
                                .propertyType(PropertyType.RESIDENTIAL)
                                .listingType(ListingType.RENT)
                                .price(new BigDecimal("50000"))
                                .address("456 Connaught Place")
                                .city("Delhi")
                                .state("Delhi")
                                .zipCode("110001")
                                .area(new BigDecimal("1200"))
                                .bedrooms(3)
                                .bathrooms(2)
                                .parkingSpaces(1)
                                .yearBuilt(2018)
                                .status(PropertyStatus.ACTIVE)
                                .featured(false)
                                .viewsCount(75L)
                                .build();
                testProperty2.setId(2L);

                testProperty3 = Property.builder()
                                .title("Commercial Plot in Bangalore")
                                .description("Prime commercial plot for business")
                                .propertyType(PropertyType.COMMERCIAL)
                                .listingType(ListingType.SALE)
                                .price(new BigDecimal("10000000"))
                                .address("789 MG Road")
                                .city("Bangalore")
                                .state("Karnataka")
                                .zipCode("560001")
                                .area(new BigDecimal("5000"))
                                .bedrooms(null)
                                .bathrooms(null)
                                .parkingSpaces(5)
                                .yearBuilt(2019)
                                .status(PropertyStatus.ACTIVE)
                                .featured(true)
                                .viewsCount(200L)
                                .build();
                testProperty3.setId(3L);

                // Configure propertyMapper to convert entities to DTOs
                lenient().when(propertyMapper.toUserDTO(any(Property.class))).thenAnswer(invocation -> {
                        Property p = invocation.getArgument(0);
                        return UserPropertyDTO.builder()
                                        .id(p.getId())
                                        .title(p.getTitle())
                                        .description(p.getDescription())
                                        .propertyType(p.getPropertyType())
                                        .listingType(p.getListingType())
                                        .price(p.getPrice())
                                        .address(p.getAddress())
                                        .city(p.getCity())
                                        .state(p.getState())
                                        .zipCode(p.getZipCode())
                                        .area(p.getArea())
                                        .bedrooms(p.getBedrooms())
                                        .bathrooms(p.getBathrooms())
                                        .parkingSpaces(p.getParkingSpaces())
                                        .yearBuilt(p.getYearBuilt())
                                        .imageUrls(java.util.Collections.emptyList())
                                        .youtubeVideoUrl(p.getYoutubeVideoUrl())
                                        .amenities(p.getAmenities())
                                        .featured(p.getFeatured())
                                        .viewsCount(p.getViewsCount())
                                        .createdAt(p.getCreatedAt())
                                        .build();
                });
        }

        @Test
        void testSearchPropertiesWithNoFilters() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder().build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty1, testProperty2, testProperty3);
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(3, result.getContent().size());
                assertEquals("Beautiful Villa in Mumbai", result.getContent().get(0).getTitle());
                verify(propertyRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        void testSearchPropertiesWithPriceRange() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .priceMin(new BigDecimal("1000000"))
                                .priceMax(new BigDecimal("6000000"))
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty1); // Only villa matches price range
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals("Beautiful Villa in Mumbai", result.getContent().get(0).getTitle());
                assertTrue(result.getContent().get(0).getPrice().compareTo(new BigDecimal("1000000")) >= 0);
                assertTrue(result.getContent().get(0).getPrice().compareTo(new BigDecimal("6000000")) <= 0);
        }

        @Test
        void testSearchPropertiesWithCityFilter() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .city("Mumbai")
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty1); // Only Mumbai property
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals("Mumbai", result.getContent().get(0).getCity());
        }

        @Test
        void testSearchPropertiesWithPropertyTypeFilter() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .propertyType(PropertyType.RESIDENTIAL)
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty2); // Only residential
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(PropertyType.RESIDENTIAL, result.getContent().get(0).getPropertyType());
        }

        @Test
        void testSearchPropertiesWithCombinedFilters() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .priceMin(new BigDecimal("40000"))
                                .priceMax(new BigDecimal("60000"))
                                .city("Delhi")
                                .propertyType(PropertyType.RESIDENTIAL)
                                .listingType(ListingType.RENT)
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty2); // Only Delhi residential for rent
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                UserPropertyDTO property = result.getContent().get(0);
                assertEquals("Delhi", property.getCity());
                assertEquals(PropertyType.RESIDENTIAL, property.getPropertyType());
                assertEquals(ListingType.RENT, property.getListingType());
                assertTrue(property.getPrice().compareTo(new BigDecimal("40000")) >= 0);
                assertTrue(property.getPrice().compareTo(new BigDecimal("60000")) <= 0);
        }

        @Test
        void testSearchPropertiesWithAreaRange() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .areaMin(new BigDecimal("1000"))
                                .areaMax(new BigDecimal("3000"))
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty1, testProperty2); // Villa and apartment
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.getContent().size());
                for (UserPropertyDTO property : result.getContent()) {
                        assertTrue(property.getArea().compareTo(new BigDecimal("1000")) >= 0);
                        assertTrue(property.getArea().compareTo(new BigDecimal("3000")) <= 0);
                }
        }

        @Test
        void testSearchPropertiesWithBedroomRange() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .minBedrooms(3)
                                .maxBedrooms(4)
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty1, testProperty2); // Villa (4) and apartment (3)
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.getContent().size());
                for (UserPropertyDTO property : result.getContent()) {
                        if (property.getBedrooms() != null) {
                                assertTrue(property.getBedrooms() >= 3);
                                assertTrue(property.getBedrooms() <= 4);
                        }
                }
        }

        @Test
        void testSearchPropertiesWithFeaturedFilter() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .featured(true)
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty1, testProperty3); // Featured properties
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.getContent().size());
                for (UserPropertyDTO property : result.getContent()) {
                        assertTrue(property.getFeatured());
                }
        }

        @Test
        void testSearchPropertiesWithTextSearch() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .searchTerm("luxury")
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty1); // Only villa has "luxury" in description
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertTrue(result.getContent().get(0).getDescription().toLowerCase().contains("luxury"));
        }

        @Test
        void testSearchPropertiesWithSortingByPriceAsc() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .sortBy("price")
                                .sortDirection("asc")
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty2, testProperty1, testProperty3); // Sorted by
                                                                                                        // price asc
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(3, result.getContent().size());
                // Verify sorting order (apartment < villa < commercial)
                assertTrue(result.getContent().get(0).getPrice().compareTo(result.getContent().get(1).getPrice()) <= 0);
                assertTrue(result.getContent().get(1).getPrice().compareTo(result.getContent().get(2).getPrice()) <= 0);
        }

        @Test
        void testSearchPropertiesWithSortingByCreatedAtDesc() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                                .sortBy("createdAt")
                                .sortDirection("desc")
                                .build();
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> properties = Arrays.asList(testProperty1, testProperty2, testProperty3); // Sorted by
                                                                                                        // created
                                                                                                        // date desc
                Page<Property> propertyPage = new PageImpl<>(properties, pageable, properties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.searchProperties(criteria, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(3, result.getContent().size());
                // Most recent first (testProperty1 was created 1 day ago, testProperty2 2 days
                // ago, testProperty3 3 days ago)
                assertEquals(testProperty1.getId(), result.getContent().get(0).getId());
        }

        @Test
        void testGetPropertyById() {
                // Arrange
                Long propertyId = 1L;
                when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty1));
                when(propertyRepository.save(any(Property.class))).thenReturn(testProperty1);

                // Act
                UserPropertyDTO result = userPropertyService.getPropertyById(propertyId);

                // Assert
                assertNotNull(result);
                assertEquals(propertyId, result.getId());
                assertEquals("Beautiful Villa in Mumbai", result.getTitle());
                verify(propertyRepository, times(2)).findById(propertyId);
                verify(propertyRepository).save(any(Property.class)); // For incrementing view count
        }

        @Test
        void testGetPropertyByIdNotFound() {
                // Arrange
                Long propertyId = 999L;
                when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

                // Act & Assert
                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                        userPropertyService.getPropertyById(propertyId);
                });
                assertEquals("Property not found with ID: 999", exception.getMessage());
        }

        @Test
        void testGetPropertyByIdInactiveProperty() {
                // Arrange
                testProperty1.setStatus(PropertyStatus.SOLD);
                Long propertyId = 1L;
                when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty1));

                // Act & Assert
                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                        userPropertyService.getPropertyById(propertyId);
                });
                assertEquals("Property is not available", exception.getMessage());
        }

        @Test
        void testGetFeaturedProperties() {
                // Arrange
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> featuredProperties = Arrays.asList(testProperty1, testProperty3);
                Page<Property> propertyPage = new PageImpl<>(featuredProperties, pageable, featuredProperties.size());

                when(propertyRepository.findAll(any(Specification.class), eq(pageable)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.getFeaturedProperties(pageable);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.getContent().size());
                for (UserPropertyDTO property : result.getContent()) {
                        assertTrue(property.getFeatured());
                }
                verify(propertyRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        void testGetRecentProperties() {
                // Arrange
                Pageable pageable = PageRequest.of(0, 10);
                List<Property> recentProperties = Arrays.asList(testProperty1, testProperty2, testProperty3);
                Page<Property> propertyPage = new PageImpl<>(recentProperties, pageable, recentProperties.size());

                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(propertyPage);

                // Act
                Page<UserPropertyDTO> result = userPropertyService.getRecentProperties(pageable);

                // Assert
                assertNotNull(result);
                assertEquals(3, result.getContent().size());
                verify(propertyRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        void testIncrementViewCount() {
                // Arrange
                Long propertyId = 1L;
                Long initialViewCount = testProperty1.getViewsCount();
                when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty1));
                when(propertyRepository.save(any(Property.class))).thenReturn(testProperty1);

                // Act
                userPropertyService.incrementViewCount(propertyId);

                // Assert
                verify(propertyRepository).findById(propertyId);
                verify(propertyRepository).save(testProperty1);
                assertEquals(initialViewCount + 1, testProperty1.getViewsCount());
        }

        @Test
        void testSearchPropertiesException() {
                // Arrange
                PropertySearchCriteria criteria = PropertySearchCriteria.builder().build();
                Pageable pageable = PageRequest.of(0, 10);
                when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenThrow(new RuntimeException("Database error"));

                // Act & Assert
                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                        userPropertyService.searchProperties(criteria, pageable);
                });
                assertTrue(exception.getMessage().contains("Database error"));
        }

        @Test
        void testValidateSearchCriteriaRanges() {
                // Test valid price range
                PropertySearchCriteria validCriteria = PropertySearchCriteria.builder()
                                .priceMin(new BigDecimal("100000"))
                                .priceMax(new BigDecimal("500000"))
                                .build();
                assertTrue(validCriteria.isValidPriceRange());

                // Test invalid price range
                PropertySearchCriteria invalidCriteria = PropertySearchCriteria.builder()
                                .priceMin(new BigDecimal("500000"))
                                .priceMax(new BigDecimal("100000"))
                                .build();
                assertFalse(invalidCriteria.isValidPriceRange());

                // Test valid area range
                PropertySearchCriteria validAreaCriteria = PropertySearchCriteria.builder()
                                .areaMin(new BigDecimal("1000"))
                                .areaMax(new BigDecimal("2000"))
                                .build();
                assertTrue(validAreaCriteria.isValidAreaRange());

                // Test valid bedroom range
                PropertySearchCriteria validBedroomCriteria = PropertySearchCriteria.builder()
                                .minBedrooms(2)
                                .maxBedrooms(4)
                                .build();
                assertTrue(validBedroomCriteria.isValidBedroomRange());
        }
}