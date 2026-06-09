package com.realestate.repository;

import com.realestate.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    // Delete images belonging to a specific property
    void deleteByProperty_Id(Long propertyId);
}