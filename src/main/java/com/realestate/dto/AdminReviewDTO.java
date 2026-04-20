package com.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminReviewDTO {

    private Long id;
    private String reviewerName;
    private String reviewerRole;
    private String reviewType;
    private Integer rating;
    private String reviewText;
    private String youtubeUrl;
    private Boolean showOnHomepage;
    private Integer sortOrder;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
