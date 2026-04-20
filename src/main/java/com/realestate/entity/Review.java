package com.realestate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_reviews_homepage", columnList = "show_on_homepage, active"),
        @Index(name = "idx_reviews_type", columnList = "review_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Review extends BaseEntity {

    @NotBlank(message = "Reviewer name is required")
    @Size(max = 100)
    @Column(name = "reviewer_name", nullable = false, length = 100)
    private String reviewerName;

    @Size(max = 100)
    @Column(name = "reviewer_role", length = 100)
    private String reviewerRole;

    @NotBlank(message = "Review type is required")
    @Column(name = "review_type", nullable = false, length = 10)
    @Builder.Default
    private String reviewType = "TEXT";

    @Min(1)
    @Max(5)
    @Column(name = "rating")
    private Integer rating;

    @Size(max = 1000)
    @Column(name = "review_text", length = 1000)
    private String reviewText;

    @Size(max = 500)
    @Column(name = "youtube_url", length = 500)
    private String youtubeUrl;

    @Column(name = "show_on_homepage", nullable = false)
    @Builder.Default
    private Boolean showOnHomepage = false;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
