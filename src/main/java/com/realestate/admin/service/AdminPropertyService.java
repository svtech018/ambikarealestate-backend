package com.realestate.admin.service;

import com.realestate.dto.AdminPropertyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPropertyService {

    AdminPropertyDTO createProperty(AdminPropertyDTO propertyDTO);

    AdminPropertyDTO updateProperty(Long propertyId, AdminPropertyDTO propertyDTO);

    void deleteProperty(Long propertyId);

    AdminPropertyDTO getPropertyById(Long propertyId);

    Page<AdminPropertyDTO> getAllProperties(Pageable pageable);
}