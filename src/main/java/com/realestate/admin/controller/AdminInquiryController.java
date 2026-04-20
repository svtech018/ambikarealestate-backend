package com.realestate.admin.controller;

import com.realestate.admin.service.AdminInquiryService;
import com.realestate.dto.AdminInquiryDTO;
import com.realestate.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/inquiries")
@PreAuthorize("hasRole('ADMIN')")
public class AdminInquiryController {

    private final AdminInquiryService adminInquiryService;

    public AdminInquiryController(AdminInquiryService adminInquiryService) {
        this.adminInquiryService = adminInquiryService;
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<ApiResponse<AdminInquiryDTO>> getInquiry(@PathVariable Long inquiryId) {
        AdminInquiryDTO inquiry = adminInquiryService.getInquiryById(inquiryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Inquiry retrieved successfully", inquiry));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminInquiryDTO>>> getAllInquiries(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AdminInquiryDTO> inquiries = adminInquiryService.getAllInquiries(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Inquiries retrieved successfully", inquiries));
    }

    @PatchMapping("/{inquiryId}/notes")
    public ResponseEntity<ApiResponse<AdminInquiryDTO>> updateAdminNotes(
            @PathVariable Long inquiryId,
            @RequestParam String adminNotes) {
        AdminInquiryDTO updated = adminInquiryService.updateAdminNotes(inquiryId, adminNotes);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin notes updated successfully", updated));
    }

    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<ApiResponse<Void>> deleteInquiry(@PathVariable Long inquiryId) {
        adminInquiryService.deleteInquiry(inquiryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Inquiry deleted successfully", null));
    }
}