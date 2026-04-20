package com.realestate.repository;

import com.realestate.entity.Inquiry;
import com.realestate.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    Page<Inquiry> findByProperty(Property property, Pageable pageable);

    Page<Inquiry> findByProperty_Id(Long propertyId, Pageable pageable);

    Page<Inquiry> findByEmailIgnoreCase(String email, Pageable pageable);

    Page<Inquiry> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT i FROM Inquiry i WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.message) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Inquiry> searchInquiries(@Param("searchTerm") String searchTerm, Pageable pageable);

    long countByProperty(Property property);

    long countByProperty_Id(Long propertyId);

    @Query("SELECT i FROM Inquiry i WHERE i.property.id = :propertyId ORDER BY i.submittedAt DESC")
    Page<Inquiry> findRecentInquiriesForProperty(@Param("propertyId") Long propertyId, Pageable pageable);
}
