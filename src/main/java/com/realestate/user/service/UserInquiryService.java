package com.realestate.user.service;

import com.realestate.dto.UserInquiryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserInquiryService {

    UserInquiryDTO submitInquiry(UserInquiryDTO dto);

    UserInquiryDTO getInquiryById(Long inquiryId);

    Page<UserInquiryDTO> getInquiriesByEmail(String email, Pageable pageable);

    Page<UserInquiryDTO> getInquiriesByPropertyId(Long propertyId, Pageable pageable);
}