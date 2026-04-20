package com.realestate.admin.serviceimpl;

import com.realestate.dto.AdminInquiryDTO;
import com.realestate.admin.service.AdminInquiryService;
import com.realestate.common.mapper.InquiryMapper;
import com.realestate.entity.Inquiry;
import com.realestate.repository.InquiryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminInquiryServiceImpl implements AdminInquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMapper inquiryMapper;

    public AdminInquiryServiceImpl(InquiryRepository inquiryRepository, InquiryMapper inquiryMapper) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryMapper = inquiryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminInquiryDTO getInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));
        return inquiryMapper.toAdminDTO(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminInquiryDTO> getAllInquiries(Pageable pageable) {
        return inquiryRepository.findAll(pageable).map(inquiryMapper::toAdminDTO);
    }

    @Override
    public AdminInquiryDTO updateAdminNotes(Long inquiryId, String adminNotes) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));

        inquiry.setAdminNotes(adminNotes);
        Inquiry saved = inquiryRepository.save(inquiry);
        return inquiryMapper.toAdminDTO(saved);
    }

    @Override
    public void deleteInquiry(Long inquiryId) {
        if (!inquiryRepository.existsById(inquiryId)) {
            throw new RuntimeException("Inquiry not found with ID: " + inquiryId);
        }
        inquiryRepository.deleteById(inquiryId);
    }
}