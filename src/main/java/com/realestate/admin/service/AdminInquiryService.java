package com.realestate.admin.service;

import com.realestate.dto.AdminInquiryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminInquiryService {

    AdminInquiryDTO getInquiryById(Long inquiryId);

    Page<AdminInquiryDTO> getAllInquiries(Pageable pageable);

    AdminInquiryDTO updateAdminNotes(Long inquiryId, String adminNotes);

    void deleteInquiry(Long inquiryId);
}