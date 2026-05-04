package com.realestate.common.mapper;

import com.realestate.common.constants.AreaUnit;
import com.realestate.common.constants.PropertyStatus;
import com.realestate.dto.AdminPropertyDTO;
import com.realestate.dto.UserPropertyDTO;
import com.realestate.entity.Property;
import com.realestate.entity.PropertyImage;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyMapper {

    private String buildImageUrl(PropertyImage image) {
        // Generate relative URL so frontend can fetch from any domain
        // This works for localhost, Railway, Vercel, or any deployment
        return "/api/properties/images/" + image.getId();
    }

    private List<String> mapImageUrls(Property property) {
        if (property.getPropertyImages() == null) {
            return Collections.emptyList();
        }
        return property.getPropertyImages().stream()
                .map(this::buildImageUrl)
                .collect(Collectors.toList());
    }

    public AdminPropertyDTO toAdminDTO(Property property) {
        return AdminPropertyDTO.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .propertyType(property.getPropertyType())
                .listingType(property.getListingType())
                .price(property.getPrice())
                .address(property.getAddress())
                .city(property.getCity())
                .state(property.getState())
                .zipCode(property.getZipCode())
                .area(property.getArea())
                .areaUnit(property.getAreaUnit() != null ? property.getAreaUnit() : AreaUnit.SQFT)
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .parkingSpaces(property.getParkingSpaces())
                .yearBuilt(property.getYearBuilt())
                .imageUrls(mapImageUrls(property))
                .youtubeVideoUrl(property.getYoutubeVideoUrl())
                .amenities(property.getAmenities())
                .status(property.getStatus())
                .featured(property.getFeatured())
                .viewsCount(property.getViewsCount())
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();
    }

    public UserPropertyDTO toUserDTO(Property property) {
        return UserPropertyDTO.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .propertyType(property.getPropertyType())
                .listingType(property.getListingType())
                .price(property.getPrice())
                .address(property.getAddress())
                .city(property.getCity())
                .state(property.getState())
                .zipCode(property.getZipCode())
                .area(property.getArea())
                .areaUnit(property.getAreaUnit() != null ? property.getAreaUnit() : AreaUnit.SQFT)
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .parkingSpaces(property.getParkingSpaces())
                .yearBuilt(property.getYearBuilt())
                .imageUrls(mapImageUrls(property))
                .youtubeVideoUrl(property.getYoutubeVideoUrl())
                .amenities(property.getAmenities())
                .featured(property.getFeatured())
                .viewsCount(property.getViewsCount())
                .createdAt(property.getCreatedAt())
                .build();
    }

    public Property toEntity(AdminPropertyDTO dto) {
        return Property.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .propertyType(dto.getPropertyType())
                .listingType(dto.getListingType())
                .price(dto.getPrice())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode() != null && !dto.getZipCode().isBlank() ? dto.getZipCode().trim() : null)
                .area(dto.getArea())
                .areaUnit(dto.getAreaUnit() != null ? dto.getAreaUnit() : AreaUnit.SQFT)
                .bedrooms(dto.getBedrooms())
                .bathrooms(dto.getBathrooms())
                .parkingSpaces(dto.getParkingSpaces())
                .yearBuilt(dto.getYearBuilt())
                .youtubeVideoUrl(dto.getYoutubeVideoUrl())
                .amenities(dto.getAmenities())
                .status(dto.getStatus() != null ? dto.getStatus() : PropertyStatus.ACTIVE)
                .featured(dto.getFeatured() != null ? dto.getFeatured() : false)
                .build();
    }

    public void updateEntityFromDTO(Property property, AdminPropertyDTO dto) {
        if (dto.getTitle() != null)
            property.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            property.setDescription(dto.getDescription());
        if (dto.getPropertyType() != null)
            property.setPropertyType(dto.getPropertyType());
        if (dto.getListingType() != null)
            property.setListingType(dto.getListingType());
        if (dto.getPrice() != null)
            property.setPrice(dto.getPrice());
        if (dto.getAddress() != null)
            property.setAddress(dto.getAddress());
        if (dto.getCity() != null)
            property.setCity(dto.getCity());
        if (dto.getState() != null)
            property.setState(dto.getState());
        if (dto.getZipCode() != null)
            property.setZipCode(dto.getZipCode().isBlank() ? null : dto.getZipCode().trim());
        if (dto.getArea() != null)
            property.setArea(dto.getArea());
        if (dto.getAreaUnit() != null)
            property.setAreaUnit(dto.getAreaUnit());
        if (dto.getBedrooms() != null)
            property.setBedrooms(dto.getBedrooms());
        if (dto.getBathrooms() != null)
            property.setBathrooms(dto.getBathrooms());
        if (dto.getParkingSpaces() != null)
            property.setParkingSpaces(dto.getParkingSpaces());
        if (dto.getYearBuilt() != null)
            property.setYearBuilt(dto.getYearBuilt());
        if (dto.getYoutubeVideoUrl() != null)
            property.setYoutubeVideoUrl(dto.getYoutubeVideoUrl());
        if (dto.getAmenities() != null)
            property.setAmenities(dto.getAmenities());
        if (dto.getStatus() != null)
            property.setStatus(dto.getStatus());
        if (dto.getFeatured() != null)
            property.setFeatured(dto.getFeatured());
    }
}
