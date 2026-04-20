package com.realestate.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.entity.Property;
import com.realestate.common.constants.ListingType;
import com.realestate.common.constants.PropertyStatus;
import com.realestate.common.constants.PropertyType;
import com.realestate.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class UserPropertyControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Property testProperty1;
    private Property testProperty2;
    private Property testProperty3;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create and save test properties (no createdBy — admin-only accounts are
        // independent)
        testProperty1 = Property.builder()
                .title("Beautiful Villa in Mumbai")
                .description("Luxury villa with modern amenities and sea view")
                .propertyType(PropertyType.RESIDENTIAL)
                .listingType(ListingType.SALE)
                .price(new BigDecimal("5000000"))
                .address("123 Marine Drive")
                .city("Mumbai")
                .state("Maharashtra")
                .zipCode("40001")
                .area(new BigDecimal("2500"))
                .bedrooms(4)
                .bathrooms(3)
                .parkingSpaces(2)
                .yearBuilt(2020)
                .status(PropertyStatus.ACTIVE)
                .featured(true)
                .viewsCount(150L)
                .build();
        testProperty1 = propertyRepository.save(testProperty1);

        testProperty2 = Property.builder()
                .title("Modern Apartment in Delhi")
                .description("Spacious apartment in prime location with metro connectivity")
                .propertyType(PropertyType.INDUSTRIAL)
                .listingType(ListingType.RENT)
                .price(new BigDecimal("50000"))
                .address("456 Connaught Place")
                .city("Delhi")
                .state("Delhi")
                .zipCode("11001")
                .area(new BigDecimal("1200"))
                .bedrooms(3)
                .bathrooms(2)
                .parkingSpaces(1)
                .yearBuilt(2018)
                .status(PropertyStatus.ACTIVE)
                .featured(false)
                .viewsCount(75L)
                .build();
        testProperty2 = propertyRepository.save(testProperty2);

        testProperty3 = Property.builder()
                .title("Commercial Plot in Bangalore")
                .description("Prime commercial plot for business development")
                .propertyType(PropertyType.COMMERCIAL)
                .listingType(ListingType.SALE)
                .price(new BigDecimal("10000000"))
                .address("789 MG Road")
                .city("Bangalore")
                .state("Karnataka")
                .zipCode("56001")
                .area(new BigDecimal("5000"))
                .bedrooms(null)
                .bathrooms(null)
                .parkingSpaces(5)
                .yearBuilt(2019)
                .status(PropertyStatus.ACTIVE)
                .featured(true)
                .viewsCount(200L)
                .build();
        testProperty3 = propertyRepository.save(testProperty3);
    }

    @Test
    void testGetAllPropertiesWithoutFilters() throws Exception {
        mockMvc.perform(get("/api/properties")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Properties fetched successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.content[0].title").isNotEmpty())
                .andExpect(jsonPath("$.data.content[0].price").isNumber())
                .andExpect(jsonPath("$.data.content[0].city").isNotEmpty());
    }

    @Test
    void testGetAllPropertiesWithPriceRangeFilter() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("priceMin", "1000000")
                .param("priceMax", "6000000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title").value("Beautiful Villa in Mumbai"))
                .andExpect(jsonPath("$.data.content[0].price").value(5000000));
    }

    @Test
    void testGetAllPropertiesWithCityFilter() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("city", "Mumbai")
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].city").value("Mumbai"))
                .andExpect(jsonPath("$.data.content[0].title").value("Beautiful Villa in Mumbai"));
    }

    @Test
    void testGetAllPropertiesWithPropertyTypeFilter() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("type", "INDUSTRIAL")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].propertyType").value("INDUSTRIAL"))
                .andExpect(jsonPath("$.data.content[0].title").value("Modern Apartment in Delhi"));
    }

    @Test
    void testGetAllPropertiesWithListingTypeFilter() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("listingType", "RENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].listingType").value("RENT"))
                .andExpect(jsonPath("$.data.content[0].title").value("Modern Apartment in Delhi"));
    }

    @Test
    void testGetAllPropertiesWithCombinedFilters() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("priceMin", "40000")
                .param("priceMax", "60000")
                .param("city", "Delhi")
                .param("type", "INDUSTRIAL")
                .param("listingType", "RENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].city").value("Delhi"))
                .andExpect(jsonPath("$.data.content[0].propertyType").value("INDUSTRIAL"))
                .andExpect(jsonPath("$.data.content[0].listingType").value("RENT"))
                .andExpect(jsonPath("$.data.content[0].price").value(50000));
    }

    @Test
    void testGetAllPropertiesWithAreaRangeFilter() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("areaMin", "1000")
                .param("areaMax", "3000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(2))) // Villa and apartment
                .andExpect(jsonPath("$.data.content[*].area", everyItem(allOf(
                        greaterThanOrEqualTo(1000),
                        lessThanOrEqualTo(3000)))));
    }

    @Test
    void testGetAllPropertiesWithBedroomRangeFilter() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("minBedrooms", "3")
                .param("maxBedrooms", "4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(2))) // Villa and apartment
                .andExpect(jsonPath("$.data.content[*].bedrooms", everyItem(allOf(
                        greaterThanOrEqualTo(3),
                        lessThanOrEqualTo(4)))));
    }

    @Test
    void testGetAllPropertiesWithFeaturedFilter() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("featured", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(2))) // Villa and commercial
                .andExpect(jsonPath("$.data.content[*].featured", everyItem(is(true))));
    }

    @Test
    void testGetAllPropertiesWithTextSearch() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("searchTerm", "luxury")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title").value("Beautiful Villa in Mumbai"));
    }

    @Test
    void testGetAllPropertiesWithSortingByPriceAsc() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("sortBy", "price")
                .param("sortDirection", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                .andExpect(jsonPath("$.data.content[0].price").value(50000)) // Apartment (lowest)
                .andExpect(jsonPath("$.data.content[1].price").value(5000000)) // Villa (middle)
                .andExpect(jsonPath("$.data.content[2].price").value(10000000)); // Commercial (highest)
    }

    @Test
    void testGetAllPropertiesWithSortingByPriceDesc() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("sortBy", "price")
                .param("sortDirection", "desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                .andExpect(jsonPath("$.data.content[0].price").value(10000000)) // Commercial (highest)
                .andExpect(jsonPath("$.data.content[1].price").value(5000000)) // Villa (middle)
                .andExpect(jsonPath("$.data.content[2].price").value(50000)); // Apartment (lowest)
    }

    @Test
    void testGetAllPropertiesWithSortingByCreatedAtDesc() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("sortBy", "createdAt")
                .param("sortDirection", "desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                // Most recent first (testProperty3 saved last)
                .andExpect(jsonPath("$.data.content[0].title").value("Commercial Plot in Bangalore"));
    }

    @Test
    void testGetAllPropertiesWithPagination() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("page", "0")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.number").value(0))
                .andExpect(jsonPath("$.data.size").value(2));
    }

    @Test
    void testGetPropertyById() throws Exception {
        mockMvc.perform(get("/api/properties/{id}", testProperty1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Property fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(testProperty1.getId()))
                .andExpect(jsonPath("$.data.title").value("Beautiful Villa in Mumbai"))
                .andExpect(jsonPath("$.data.price").value(5000000))
                .andExpect(jsonPath("$.data.city").value("Mumbai"));
    }

    @Test
    void testGetPropertyByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/properties/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetFeaturedProperties() throws Exception {
        mockMvc.perform(get("/api/properties/featured")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Featured properties fetched successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content[*].featured", everyItem(is(true))));
    }

    @Test
    void testGetRecentProperties() throws Exception {
        mockMvc.perform(get("/api/properties/recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Recent properties fetched successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(3)));
    }

    @Test
    void testInvalidPropertyTypeFilter() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("type", "INVALID_TYPE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Invalid property type. Valid values are: RESIDENTIAL, COMMERCIAL, INDUSTRIAL, LAND"));
    }

    @Test
    void testInvalidListingTypeFilter() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("listingType", "INVALID_TYPE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid listing type. Valid values are: SALE, RENT"));
    }

    @Test
    void testInvalidPriceRange() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("priceMin", "500000")
                .param("priceMax", "100000") // Max less than min
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Minimum price cannot be greater than maximum price"));
    }

    @Test
    void testInvalidAreaRange() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("areaMin", "2000")
                .param("areaMax", "1000") // Max less than min
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Minimum area cannot be greater than maximum area"));
    }

    @Test
    void testInvalidPaginationParameters() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("page", "-1") // Invalid page number
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/properties")
                .param("size", "0") // Invalid page size
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmptyResultsWithStrictFilters() throws Exception {
        mockMvc.perform(get("/api/properties")
                .param("city", "NonExistentCity")
                .param("type", "LAND")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void testDefaultSortingAndPagination() throws Exception {
        mockMvc.perform(get("/api/properties")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.number").value(0)) // Default page 0
                .andExpect(jsonPath("$.data.size").value(20)) // Default size 20
                .andExpect(jsonPath("$.data.sort.sorted").value(true)); // Default sorting applied
    }
}