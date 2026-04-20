package com.realestate.dto;

import com.realestate.common.constants.ListingType;
import com.realestate.common.constants.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertySearchCriteria {

    private BigDecimal priceMin;
    private BigDecimal priceMax;

    private String city;
    private String state;
    private String zipCode;

    private PropertyType propertyType;
    private ListingType listingType;

    private BigDecimal areaMin;
    private BigDecimal areaMax;

    private Integer minBedrooms;
    private Integer maxBedrooms;
    private Integer minBathrooms;
    private Integer maxBathrooms;

    private Integer minParkingSpaces;
    private Integer maxParkingSpaces;

    private Integer minYearBuilt;
    private Integer maxYearBuilt;

    private Boolean featured;

    private String searchTerm;

    private String sortBy;
    private String sortDirection;

    public boolean hasFilters() {
        return priceMin != null || priceMax != null ||
                city != null || state != null || zipCode != null ||
                propertyType != null || listingType != null ||
                areaMin != null || areaMax != null ||
                minBedrooms != null || maxBedrooms != null ||
                minBathrooms != null || maxBathrooms != null ||
                minParkingSpaces != null || maxParkingSpaces != null ||
                minYearBuilt != null || maxYearBuilt != null ||
                featured != null ||
                (searchTerm != null && !searchTerm.trim().isEmpty());
    }

    public boolean hasPriceFilters() {
        return priceMin != null || priceMax != null;
    }

    public boolean hasLocationFilters() {
        return city != null || state != null || zipCode != null;
    }

    public boolean hasAreaFilters() {
        return areaMin != null || areaMax != null;
    }

    public boolean hasRoomFilters() {
        return minBedrooms != null || maxBedrooms != null ||
                minBathrooms != null || maxBathrooms != null;
    }

    public boolean hasTextSearch() {
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }

    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PropertySearchCriteria{");
        if (hasPriceFilters()) {
            sb.append("price=").append(priceMin).append("-").append(priceMax).append(", ");
        }
        if (hasLocationFilters()) {
            sb.append("location=").append(city).append("/").append(state).append("/").append(zipCode).append(", ");
        }
        if (propertyType != null) {
            sb.append("type=").append(propertyType).append(", ");
        }
        if (listingType != null) {
            sb.append("listingType=").append(listingType).append(", ");
        }
        if (hasAreaFilters()) {
            sb.append("area=").append(areaMin).append("-").append(areaMax).append(", ");
        }
        if (hasRoomFilters()) {
            sb.append("rooms=").append(minBedrooms).append("-").append(maxBedrooms)
                    .append("bed/").append(minBathrooms).append("-").append(maxBathrooms).append("bath, ");
        }
        if (featured != null) {
            sb.append("featured=").append(featured).append(", ");
        }
        if (hasTextSearch()) {
            sb.append("search='").append(searchTerm).append("', ");
        }
        if (hasSorting()) {
            sb.append("sort=").append(sortBy).append(" ").append(sortDirection).append(", ");
        }
        if (sb.length() > 22) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean isValidPriceRange() {
        if (priceMin == null || priceMax == null)
            return true;
        return priceMin.compareTo(priceMax) <= 0;
    }

    public boolean isValidAreaRange() {
        if (areaMin == null || areaMax == null)
            return true;
        return areaMin.compareTo(areaMax) <= 0;
    }

    public boolean isValidBedroomRange() {
        if (minBedrooms == null || maxBedrooms == null)
            return true;
        return minBedrooms <= maxBedrooms;
    }
}
