package com.realestate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "inquiries", indexes = {
        @Index(name = "idx_inquiry_property", columnList = "property_id"),
        @Index(name = "idx_inquiry_email", columnList = "email"),
        @Index(name = "idx_inquiry_submitted_at", columnList = "submitted_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Inquiry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "submitted_at", nullable = false)
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "preferred_contact_time")
    private String preferredContactTime;

    public Inquiry(Property property, String name, String email, String phoneNumber, String message) {
        this.property = Objects.requireNonNull(property, "Property cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.phoneNumber = phoneNumber;
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.submittedAt = LocalDateTime.now();
    }

    public String getPropertyTitle() {
        return property != null ? property.getTitle() : null;
    }

    public Long getPropertyId() {
        return property != null ? property.getId() : null;
    }

    @Override
    public String toString() {
        return "Inquiry{" +
                "id=" + getId() +
                ", propertyId=" + getPropertyId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", submittedAt=" + submittedAt +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
