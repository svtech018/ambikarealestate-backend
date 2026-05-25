package com.realestate.user.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.ReviewDTO;
import com.realestate.entity.Review;
import com.realestate.repository.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getAllReviews() {
        List<ReviewDTO> reviews = reviewRepository
                .findAll()
                .stream()
                .filter(r -> Boolean.TRUE.equals(r.getActive()))
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "All reviews retrieved", reviews));
    }

    @GetMapping("/homepage")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getHomepageReviews() {
        List<ReviewDTO> reviews = reviewRepository
                .findByShowOnHomepageTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Homepage reviews retrieved", reviews));
    }

    private ReviewDTO toDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .reviewerName(review.getReviewerName())
                .reviewerRole(review.getReviewerRole())
                .reviewType(review.getReviewType())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .youtubeUrl(review.getYoutubeUrl())
                .build();
    }
}
