package com.realestate.repository;

import com.realestate.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByShowOnHomepageTrueOrderBySortOrderAsc();

    Page<Review> findAllByOrderBySortOrderAsc(Pageable pageable);
}
