package com.realestate.user.serviceimpl;

import com.realestate.common.constants.PropertyStatus;
import com.realestate.common.constants.PropertyType;
import com.realestate.common.mapper.PropertyMapper;
import com.realestate.dto.PropertySearchCriteria;
import com.realestate.dto.UserPropertyDTO;
import com.realestate.entity.Property;
import com.realestate.entity.PropertyImage;
import com.realestate.repository.PropertyImageRepository;
import com.realestate.repository.PropertyRepository;
import com.realestate.repository.PropertySpecification;
import com.realestate.user.service.UserPropertyService;
import com.realestate.util.PropertySearchValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class UserPropertyServiceImpl implements UserPropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final PropertySearchValidator propertySearchValidator;
    private final PropertyMapper propertyMapper;

    public UserPropertyServiceImpl(PropertyRepository propertyRepository,
            PropertyImageRepository propertyImageRepository,
            PropertySearchValidator propertySearchValidator,
            PropertyMapper propertyMapper) {
        this.propertyRepository = Objects.requireNonNull(propertyRepository, "PropertyRepository cannot be null");
        this.propertyImageRepository = Objects.requireNonNull(propertyImageRepository,
                "PropertyImageRepository cannot be null");
        this.propertySearchValidator = Objects.requireNonNull(propertySearchValidator,
                "PropertySearchValidator cannot be null");
        this.propertyMapper = Objects.requireNonNull(propertyMapper, "PropertyMapper cannot be null");
    }

    @Override
    public Page<UserPropertyDTO> searchProperties(PropertySearchCriteria searchCriteria, Pageable pageable) {
        propertySearchValidator.validateSearchCriteria(searchCriteria);
        propertySearchValidator.validatePaginationParameters(pageable.getPageNumber(), pageable.getPageSize());

        Specification<Property> specification = PropertySpecification.withCriteria(searchCriteria);
        Pageable sortedPageable = applySorting(searchCriteria, pageable);
        Page<Property> properties = propertyRepository.findAll(specification, sortedPageable);

        return properties.map(propertyMapper::toUserDTO);
    }

    @Override
    public UserPropertyDTO getPropertyById(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));

        if (!PropertyStatus.ACTIVE.equals(property.getStatus())) {
            throw new RuntimeException("Property is not available");
        }

        incrementViewCount(propertyId);
        return propertyMapper.toUserDTO(property);
    }

    @Override
    public Page<UserPropertyDTO> getFeaturedProperties(Pageable pageable) {
        Specification<Property> specification = PropertySpecification.isFeatured();
        Page<Property> properties = propertyRepository.findAll(specification, pageable);
        return properties.map(propertyMapper::toUserDTO);
    }

    @Override
    public Page<UserPropertyDTO> getRecentProperties(Pageable pageable) {
        Specification<Property> specification = PropertySpecification.isActive();
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Property> properties = propertyRepository.findAll(specification, sortedPageable);
        return properties.map(propertyMapper::toUserDTO);
    }

    @Override
    public Page<UserPropertyDTO> getPropertiesByType(String propertyType, Pageable pageable) {
        try {
            PropertyType type = PropertyType.valueOf(propertyType.toUpperCase());
            Specification<Property> specification = PropertySpecification.hasPropertyType(type);
            Page<Property> properties = propertyRepository.findAll(specification, pageable);
            return properties.map(propertyMapper::toUserDTO);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid property type: " + propertyType);
        }
    }

    @Override
    public Page<UserPropertyDTO> getPropertiesByCity(String city, Pageable pageable) {
        Specification<Property> specification = PropertySpecification.inCity(city);
        Page<Property> properties = propertyRepository.findAll(specification, pageable);
        return properties.map(propertyMapper::toUserDTO);
    }

    @Override
    public Page<UserPropertyDTO> getPropertiesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable) {
        Specification<Property> specification = PropertySpecification.priceInRange(minPrice, maxPrice);
        Page<Property> properties = propertyRepository.findAll(specification, pageable);
        return properties.map(propertyMapper::toUserDTO);
    }

    @Override
    public Page<UserPropertyDTO> searchPropertiesByText(String searchTerm, Pageable pageable) {
        PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                .searchTerm(searchTerm)
                .build();
        Specification<Property> specification = PropertySpecification.withCriteria(criteria);
        Page<Property> properties = propertyRepository.findAll(specification, pageable);
        return properties.map(propertyMapper::toUserDTO);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));

        property.incrementViewsCount();
        propertyRepository.save(property);
    }

    @Override
    public PropertyImage getPropertyImageById(Long imageId) {
        return propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Property image not found with ID: " + imageId));
    }

    private Pageable applySorting(PropertySearchCriteria criteria, Pageable pageable) {
        if (criteria == null || criteria.getSortBy() == null || criteria.getSortBy().trim().isEmpty()) {
            Sort defaultSort = Sort.by(Sort.Direction.DESC, "createdAt");
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        String sortBy = criteria.getSortBy().trim().toLowerCase();
        String sortDirection = criteria.getSortDirection() != null ? criteria.getSortDirection().trim() : "desc";

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort;
        switch (sortBy) {
            case "price":
                sort = Sort.by(direction, "price").and(Sort.by(Sort.Direction.DESC, "createdAt"));
                break;
            case "area":
                sort = Sort.by(direction, "area").and(Sort.by(Sort.Direction.ASC, "price"));
                break;
            case "viewscount":
            case "views":
                sort = Sort.by(direction, "viewsCount").and(Sort.by(Sort.Direction.DESC, "createdAt"));
                break;
            case "title":
                sort = Sort.by(direction, "title");
                break;
            case "city":
                sort = Sort.by(direction, "city").and(Sort.by(Sort.Direction.ASC, "price"));
                break;
            case "createdat":
            case "created":
            case "date":
            default:
                sort = Sort.by(direction, "createdAt");
                break;
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}