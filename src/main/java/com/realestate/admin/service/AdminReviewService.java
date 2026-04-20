package com.realestate.admin.service;

import com.realestate.dto.AdminReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReviewService {

    AdminReviewDTO createReview(AdminReviewDTO dto);

    AdminReviewDTO updateReview(Long id, AdminReviewDTO dto);

    void deleteReview(Long id);

    AdminReviewDTO getReviewById(Long id);

    Page<AdminReviewDTO> getAllReviews(Pageable pageable);
}
