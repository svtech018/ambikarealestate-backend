package com.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

    private Long id;
    private String reviewerName;
    private String reviewerRole;
    private String reviewType;
    private Integer rating;
    private String reviewText;
    private String youtubeUrl;
}
