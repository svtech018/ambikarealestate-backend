package com.realestate.common.mapper;

import com.realestate.dto.AdminInquiryDTO;
import com.realestate.dto.UserInquiryDTO;
import com.realestate.entity.Inquiry;
import com.realestate.entity.Property;
import org.springframework.stereotype.Component;

@Component
public class InquiryMapper {

    public AdminInquiryDTO toAdminDTO(Inquiry inquiry) {
        return AdminInquiryDTO.builder()
                .id(inquiry.getId())
                .propertyId(inquiry.getPropertyId())
                .propertyTitle(inquiry.getPropertyTitle())
                .name(inquiry.getName())
                .email(inquiry.getEmail())
                .phoneNumber(inquiry.getPhoneNumber())
                .message(inquiry.getMessage())
                .adminNotes(inquiry.getAdminNotes())
                .submittedAt(inquiry.getSubmittedAt())
                .preferredContactTime(inquiry.getPreferredContactTime())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }

    public UserInquiryDTO toUserDTO(Inquiry inquiry) {
        return UserInquiryDTO.builder()
                .id(inquiry.getId())
                .propertyId(inquiry.getPropertyId())
                .propertyTitle(inquiry.getPropertyTitle())
                .name(inquiry.getName())
                .email(inquiry.getEmail())
                .phoneNumber(inquiry.getPhoneNumber())
                .message(inquiry.getMessage())
                .submittedAt(inquiry.getSubmittedAt())
                .preferredContactTime(inquiry.getPreferredContactTime())
                .build();
    }

    public Inquiry toEntity(UserInquiryDTO dto, Property property) {
        return Inquiry.builder()
                .property(property)
                .name(dto.getName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .message(dto.getMessage())
                .preferredContactTime(dto.getPreferredContactTime())
                .build();
    }
}
