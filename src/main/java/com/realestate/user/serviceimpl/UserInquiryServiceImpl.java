package com.realestate.user.serviceimpl;

import com.realestate.common.mapper.InquiryMapper;
import com.realestate.dto.UserInquiryDTO;
import com.realestate.entity.Inquiry;
import com.realestate.entity.Property;
import com.realestate.repository.InquiryRepository;
import com.realestate.repository.PropertyRepository;
import com.realestate.user.service.UserInquiryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserInquiryServiceImpl implements UserInquiryService {

    private final InquiryRepository inquiryRepository;
    private final PropertyRepository propertyRepository;
    private final InquiryMapper inquiryMapper;

    public UserInquiryServiceImpl(InquiryRepository inquiryRepository,
            PropertyRepository propertyRepository,
            InquiryMapper inquiryMapper) {
        this.inquiryRepository = inquiryRepository;
        this.propertyRepository = propertyRepository;
        this.inquiryMapper = inquiryMapper;
    }

    @Override
    public UserInquiryDTO submitInquiry(UserInquiryDTO dto) {
        Property property = null;
        if (dto.getPropertyId() != null && dto.getPropertyId() > 0) {
            property = propertyRepository.findById(dto.getPropertyId()).orElse(null);
        }

        Inquiry inquiry = inquiryMapper.toEntity(dto, property);
        Inquiry saved = inquiryRepository.save(inquiry);
        return inquiryMapper.toUserDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInquiryDTO getInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));
        return inquiryMapper.toUserDTO(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserInquiryDTO> getInquiriesByEmail(String email, Pageable pageable) {
        return inquiryRepository.findByEmailIgnoreCase(email, pageable).map(inquiryMapper::toUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserInquiryDTO> getInquiriesByPropertyId(Long propertyId, Pageable pageable) {
        return inquiryRepository.findByProperty_Id(propertyId, pageable).map(inquiryMapper::toUserDTO);
    }
}