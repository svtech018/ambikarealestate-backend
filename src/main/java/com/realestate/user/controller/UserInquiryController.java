package com.realestate.user.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.UserInquiryDTO;
import com.realestate.user.service.UserInquiryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
public class UserInquiryController {

    private final UserInquiryService userInquiryService;

    public UserInquiryController(UserInquiryService userInquiryService) {
        this.userInquiryService = userInquiryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserInquiryDTO>> submitInquiry(
            @Valid @RequestBody UserInquiryDTO dto) {
        UserInquiryDTO created = userInquiryService.submitInquiry(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Inquiry submitted successfully", created));
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<ApiResponse<UserInquiryDTO>> getInquiry(@PathVariable Long inquiryId) {
        UserInquiryDTO inquiry = userInquiryService.getInquiryById(inquiryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Inquiry retrieved successfully", inquiry));
    }

    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<Page<UserInquiryDTO>>> getInquiriesByEmail(
            @RequestParam String email,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserInquiryDTO> inquiries = userInquiryService.getInquiriesByEmail(email, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Inquiries retrieved successfully", inquiries));
    }

    @GetMapping("/by-property/{propertyId}")
    public ResponseEntity<ApiResponse<Page<UserInquiryDTO>>> getInquiriesByProperty(
            @PathVariable Long propertyId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserInquiryDTO> inquiries = userInquiryService.getInquiriesByPropertyId(propertyId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Inquiries retrieved successfully", inquiries));
    }
}