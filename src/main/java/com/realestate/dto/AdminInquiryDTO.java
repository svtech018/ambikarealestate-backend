package com.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminInquiryDTO {

    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private String name;
    private String email;
    private String phoneNumber;
    private String message;
    private String adminNotes;
    private LocalDateTime submittedAt;
    private String preferredContactTime;
    private LocalDateTime createdAt;
}
