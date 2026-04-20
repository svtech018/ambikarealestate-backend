package com.realestate.admin.serviceimpl;

import com.realestate.admin.service.AdminReviewService;
import com.realestate.dto.AdminReviewDTO;
import com.realestate.entity.Review;
import com.realestate.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminReviewServiceImpl implements AdminReviewService {

    private final ReviewRepository reviewRepository;

    public AdminReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public AdminReviewDTO createReview(AdminReviewDTO dto) {
        Review review = Review.builder()
                .reviewerName(dto.getReviewerName())
                .reviewerRole(dto.getReviewerRole())
                .reviewType(dto.getReviewType() != null ? dto.getReviewType() : "TEXT")
                .rating(dto.getRating())
                .reviewText(dto.getReviewText())
                .youtubeUrl(dto.getYoutubeUrl())
                .showOnHomepage(dto.getShowOnHomepage() != null ? dto.getShowOnHomepage() : false)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return toDTO(reviewRepository.save(review));
    }

    @Override
    public AdminReviewDTO updateReview(Long id, AdminReviewDTO dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));

        review.setReviewerName(dto.getReviewerName());
        review.setReviewerRole(dto.getReviewerRole());
        review.setReviewType(dto.getReviewType() != null ? dto.getReviewType() : "TEXT");
        review.setRating(dto.getRating());
        review.setReviewText(dto.getReviewText());
        review.setYoutubeUrl(dto.getYoutubeUrl());
        review.setShowOnHomepage(dto.getShowOnHomepage() != null ? dto.getShowOnHomepage() : false);
        review.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        review.setActive(dto.getActive() != null ? dto.getActive() : true);

        return toDTO(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        return toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminReviewDTO> getAllReviews(Pageable pageable) {
        return reviewRepository.findAllByOrderBySortOrderAsc(pageable).map(this::toDTO);
    }

    private AdminReviewDTO toDTO(Review review) {
        return AdminReviewDTO.builder()
                .id(review.getId())
                .reviewerName(review.getReviewerName())
                .reviewerRole(review.getReviewerRole())
                .reviewType(review.getReviewType())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .youtubeUrl(review.getYoutubeUrl())
                .showOnHomepage(review.getShowOnHomepage())
                .sortOrder(review.getSortOrder())
                .active(review.getActive())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
