package com.realestate.entity;

import com.realestate.common.constants.AreaUnit;
import com.realestate.common.constants.ListingType;
import com.realestate.common.constants.PropertyStatus;
import com.realestate.common.constants.PropertyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "properties", indexes = {
        @Index(name = "idx_property_type", columnList = "property_type"),
        @Index(name = "idx_listing_type", columnList = "listing_type"),
        @Index(name = "idx_property_status", columnList = "status"),
        @Index(name = "idx_property_price", columnList = "price"),
        @Index(name = "idx_property_city", columnList = "city"),
        @Index(name = "idx_property_state", columnList = "state")
})
@NamedEntityGraph(name = "Property.withImages", attributeNodes = @NamedAttributeNode("propertyImages"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Property extends BaseEntity {

    @NotBlank(message = "Property title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Property type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false, length = 50)
    private PropertyType propertyType;

    @NotNull(message = "Listing type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false, length = 50)
    private ListingType listingType;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Price must have at most 13 integer digits and 2 decimal places")
    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(name = "address", nullable = false)
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must not exceed 50 characters")
    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Pattern(regexp = "^[A-Za-z0-9 \\-]{3,10}$", message = "Postal code should be 3-10 alphanumeric characters (e.g., 400001 or 12345)")
    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @DecimalMin(value = "0.0", inclusive = false, message = "Area must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Area must have at most 10 integer digits and 2 decimal places")
    @Column(name = "area", precision = 12, scale = 2)
    private BigDecimal area;

    @Enumerated(EnumType.STRING)
    @Column(name = "area_unit", length = 10)
    @Builder.Default
    private AreaUnit areaUnit = AreaUnit.SQFT;

    @Min(value = 0, message = "Number of bedrooms cannot be negative")
    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Min(value = 0, message = "Number of bathrooms cannot be negative")
    @Column(name = "bathrooms")
    private Integer bathrooms;

    @Min(value = 0, message = "Number of parking spaces cannot be negative")
    @Column(name = "parking_spaces")
    private Integer parkingSpaces;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC, id ASC")
    @Builder.Default
    private List<PropertyImage> propertyImages = new ArrayList<>();

    @Column(name = "youtube_video_url")
    private String youtubeVideoUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "property_amenities", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "amenity")
    @Builder.Default
    private List<String> amenities = new ArrayList<>();

    @NotNull(message = "Property status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PropertyStatus status = PropertyStatus.ACTIVE;

    @Column(name = "featured", nullable = false)
    @Builder.Default
    private Boolean featured = false;

    @Column(name = "views_count", nullable = false)
    @Builder.Default
    private Long viewsCount = 0L;

    public Property(String title, PropertyType propertyType, ListingType listingType,
            BigDecimal price, String address, String city, String state) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.propertyType = Objects.requireNonNull(propertyType, "Property type cannot be null");
        this.listingType = Objects.requireNonNull(listingType, "Listing type cannot be null");
        this.price = Objects.requireNonNull(price, "Price cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
        this.city = Objects.requireNonNull(city, "City cannot be null");
        this.state = Objects.requireNonNull(state, "State cannot be null");
        this.viewsCount = 0L;
        this.propertyImages = new ArrayList<>();
        this.amenities = new ArrayList<>();
    }

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder(address);
        fullAddress.append(", ").append(city);
        fullAddress.append(", ").append(state);
        if (zipCode != null && !zipCode.trim().isEmpty()) {
            fullAddress.append(" ").append(zipCode);
        }
        return fullAddress.toString();
    }

    public void addPropertyImage(PropertyImage image) {
        if (image != null) {
            if (this.propertyImages == null) {
                this.propertyImages = new ArrayList<>();
            }
            image.setProperty(this);
            this.propertyImages.add(image);
        }
    }

    public void replacePropertyImages(List<PropertyImage> images) {
        if (this.propertyImages == null) {
            this.propertyImages = new ArrayList<>();
        }
        this.propertyImages.clear();
        if (images != null) {
            images.forEach(this::addPropertyImage);
        }
    }

    public void addAmenity(String amenity) {
        if (amenity != null && !amenity.trim().isEmpty()) {
            if (this.amenities == null) {
                this.amenities = new ArrayList<>();
            }
            this.amenities.add(amenity.trim());
        }
    }

    public void removeAmenity(String amenity) {
        if (this.amenities != null && amenity != null) {
            this.amenities.remove(amenity);
        }
    }

    public void markAsSold() {
        this.status = PropertyStatus.SOLD;
    }

    public void markAsWithdrawn() {
        this.status = PropertyStatus.WITHDRAWN;
    }

    public void markAsActive() {
        this.status = PropertyStatus.ACTIVE;
    }

    public void markAsPending() {
        this.status = PropertyStatus.PENDING;
    }

    public boolean isActive() {
        return PropertyStatus.ACTIVE.equals(this.status);
    }

    public void incrementViewsCount() {
        this.viewsCount = (this.viewsCount != null) ? this.viewsCount + 1 : 1L;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
}
