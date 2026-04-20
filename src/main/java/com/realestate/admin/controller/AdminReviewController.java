package com.realestate.admin.controller;

import com.realestate.admin.service.AdminReviewService;
import com.realestate.dto.AdminReviewDTO;
import com.realestate.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    public AdminReviewController(AdminReviewService adminReviewService) {
        this.adminReviewService = adminReviewService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminReviewDTO>> createReview(
            @Valid @RequestBody AdminReviewDTO dto) {
        AdminReviewDTO created = adminReviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Review created successfully", created));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<AdminReviewDTO>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody AdminReviewDTO dto) {
        AdminReviewDTO updated = adminReviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review updated successfully", updated));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        adminReviewService.deleteReview(reviewId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review deleted successfully", null));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<AdminReviewDTO>> getReview(@PathVariable Long reviewId) {
        AdminReviewDTO review = adminReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review retrieved successfully", review));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminReviewDTO>>> getAllReviews(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AdminReviewDTO> reviews = adminReviewService.getAllReviews(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reviews retrieved successfully", reviews));
    }
}
