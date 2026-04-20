package com.realestate.repository;

import com.realestate.common.constants.ListingType;
import com.realestate.common.constants.PropertyStatus;
import com.realestate.common.constants.PropertyType;
import com.realestate.dto.PropertySearchCriteria;
import com.realestate.entity.Property;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PropertySpecification {

    public static Specification<Property> withCriteria(PropertySearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE));

            if (criteria == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            addPriceRangeFilter(predicates, criteriaBuilder, root, criteria);
            addLocationFilters(predicates, criteriaBuilder, root, criteria);
            addPropertyCharacteristicsFilters(predicates, criteriaBuilder, root, criteria);
            addAreaAndSpaceFilters(predicates, criteriaBuilder, root, criteria);
            addDateAndFeatureFilters(predicates, criteriaBuilder, root, criteria);
            addTextSearchFilter(predicates, criteriaBuilder, root, criteria);

            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addPriceRangeFilter(List<Predicate> predicates, CriteriaBuilder cb,
            Root<Property> root, PropertySearchCriteria criteria) {
        if (criteria.getPriceMin() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), criteria.getPriceMin()));
        }
        if (criteria.getPriceMax() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), criteria.getPriceMax()));
        }
    }

    private static void addLocationFilters(List<Predicate> predicates, CriteriaBuilder cb,
            Root<Property> root, PropertySearchCriteria criteria) {
        if (StringUtils.hasText(criteria.getCity())) {
            String cityPattern = "%" + criteria.getCity().toLowerCase().trim() + "%";
            predicates.add(cb.like(cb.lower(root.get("city")), cityPattern));
        }
        if (StringUtils.hasText(criteria.getState())) {
            String statePattern = "%" + criteria.getState().toLowerCase().trim() + "%";
            predicates.add(cb.like(cb.lower(root.get("state")), statePattern));
        }
        if (StringUtils.hasText(criteria.getZipCode())) {
            predicates.add(cb.equal(root.get("zipCode"), criteria.getZipCode().trim()));
        }
    }

    private static void addPropertyCharacteristicsFilters(List<Predicate> predicates, CriteriaBuilder cb,
            Root<Property> root, PropertySearchCriteria criteria) {
        if (criteria.getPropertyType() != null) {
            predicates.add(cb.equal(root.get("propertyType"), criteria.getPropertyType()));
        }
        if (criteria.getListingType() != null) {
            predicates.add(cb.equal(root.get("listingType"), criteria.getListingType()));
        }
        if (criteria.getMinBedrooms() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("bedrooms"), criteria.getMinBedrooms()));
        }
        if (criteria.getMaxBedrooms() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("bedrooms"), criteria.getMaxBedrooms()));
        }
        if (criteria.getMinBathrooms() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("bathrooms"), criteria.getMinBathrooms()));
        }
        if (criteria.getMaxBathrooms() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("bathrooms"), criteria.getMaxBathrooms()));
        }
    }

    private static void addAreaAndSpaceFilters(List<Predicate> predicates, CriteriaBuilder cb,
            Root<Property> root, PropertySearchCriteria criteria) {
        if (criteria.getAreaMin() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("area"), criteria.getAreaMin()));
        }
        if (criteria.getAreaMax() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("area"), criteria.getAreaMax()));
        }
        if (criteria.getMinParkingSpaces() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("parkingSpaces"), criteria.getMinParkingSpaces()));
        }
        if (criteria.getMaxParkingSpaces() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("parkingSpaces"), criteria.getMaxParkingSpaces()));
        }
    }

    private static void addDateAndFeatureFilters(List<Predicate> predicates, CriteriaBuilder cb,
            Root<Property> root, PropertySearchCriteria criteria) {
        if (criteria.getMinYearBuilt() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("yearBuilt"), criteria.getMinYearBuilt()));
        }
        if (criteria.getMaxYearBuilt() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("yearBuilt"), criteria.getMaxYearBuilt()));
        }
        if (criteria.getFeatured() != null) {
            predicates.add(cb.equal(root.get("featured"), criteria.getFeatured()));
        }
    }

    private static void addTextSearchFilter(List<Predicate> predicates, CriteriaBuilder cb,
            Root<Property> root, PropertySearchCriteria criteria) {
        if (StringUtils.hasText(criteria.getSearchTerm())) {
            String searchPattern = "%" + criteria.getSearchTerm().toLowerCase().trim() + "%";
            List<Predicate> textPredicates = new ArrayList<>();
            textPredicates.add(cb.like(cb.lower(root.get("title")), searchPattern));
            textPredicates.add(cb.like(cb.lower(root.get("description")), searchPattern));
            textPredicates.add(cb.like(cb.lower(root.get("address")), searchPattern));
            textPredicates.add(cb.like(cb.lower(root.get("city")), searchPattern));
            predicates.add(cb.or(textPredicates.toArray(new Predicate[0])));
        }
    }

    public static Specification<Property> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE);
    }

    public static Specification<Property> isFeatured() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE),
                criteriaBuilder.equal(root.get("featured"), true));
    }

    public static Specification<Property> hasPropertyType(PropertyType propertyType) {
        return (root, query, criteriaBuilder) -> {
            if (propertyType == null) {
                return criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE);
            }
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE),
                    criteriaBuilder.equal(root.get("propertyType"), propertyType));
        };
    }

    public static Specification<Property> hasListingType(ListingType listingType) {
        return (root, query, criteriaBuilder) -> {
            if (listingType == null) {
                return criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE);
            }
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE),
                    criteriaBuilder.equal(root.get("listingType"), listingType));
        };
    }

    public static Specification<Property> inCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(city)) {
                return criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE);
            }
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("city")),
                            "%" + city.toLowerCase().trim() + "%"));
        };
    }

    public static Specification<Property> inState(String state) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(state)) {
                return criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE);
            }
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("state")),
                            "%" + state.toLowerCase().trim() + "%"));
        };
    }

    public static Specification<Property> priceInRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE));
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Property> areaInRange(BigDecimal minArea, BigDecimal maxArea) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE));
            if (minArea != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area"), minArea));
            }
            if (maxArea != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("area"), maxArea));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Property> bedroomsInRange(Integer minBedrooms, Integer maxBedrooms) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE));
            if (minBedrooms != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("bedrooms"), minBedrooms));
            }
            if (maxBedrooms != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("bedrooms"), maxBedrooms));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Property> bathroomsInRange(Integer minBathrooms, Integer maxBathrooms) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE));
            if (minBathrooms != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("bathrooms"), minBathrooms));
            }
            if (maxBathrooms != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("bathrooms"), maxBathrooms));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Property> textSearch(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(searchTerm)) {
                return criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE);
            }
            String searchPattern = "%" + searchTerm.toLowerCase().trim() + "%";
            List<Predicate> textPredicates = new ArrayList<>();
            textPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern));
            textPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern));
            textPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), searchPattern));
            textPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("amenities")), searchPattern));
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE),
                    criteriaBuilder.or(textPredicates.toArray(new Predicate[0])));
        };
    }

    public static Specification<Property> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE));
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Property> hasMinimumViews(Long minViews) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE));
            if (minViews != null && minViews > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("viewsCount"), minViews));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Property> hasImages() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE),
                    criteriaBuilder.isNotEmpty(root.get("propertyImages")));
        };
    }

    public static Specification<Property> hasYouTubeVideo() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE),
                criteriaBuilder.isNotNull(root.get("youtubeVideoUrl")),
                criteriaBuilder.notEqual(root.get("youtubeVideoUrl"), ""));
    }

    public static Specification<Property> recentProperties(int days) {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), cutoffDate));
        };
    }

    public static Specification<Property> yearBuiltInRange(Integer minYear, Integer maxYear) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE));
            if (minYear != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("yearBuilt"), minYear));
            }
            if (maxYear != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("yearBuilt"), maxYear));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
