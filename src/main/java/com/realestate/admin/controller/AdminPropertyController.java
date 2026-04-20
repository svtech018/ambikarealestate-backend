package com.realestate.admin.controller;

import com.realestate.admin.service.AdminPropertyService;
import com.realestate.dto.AdminPropertyDTO;
import com.realestate.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/properties")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPropertyController {

    private final AdminPropertyService adminPropertyService;

    public AdminPropertyController(AdminPropertyService adminPropertyService) {
        this.adminPropertyService = adminPropertyService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminPropertyDTO>> createProperty(
            @Valid @RequestBody AdminPropertyDTO dto) {
        AdminPropertyDTO created = adminPropertyService.createProperty(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Property created successfully", created));
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<AdminPropertyDTO>> updateProperty(
            @PathVariable Long propertyId,
            @Valid @RequestBody AdminPropertyDTO dto) {
        AdminPropertyDTO updated = adminPropertyService.updateProperty(propertyId, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Property updated successfully", updated));
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long propertyId) {
        adminPropertyService.deleteProperty(propertyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Property deleted successfully", null));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<AdminPropertyDTO>> getProperty(@PathVariable Long propertyId) {
        AdminPropertyDTO property = adminPropertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Property retrieved successfully", property));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminPropertyDTO>>> getAllProperties(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AdminPropertyDTO> properties = adminPropertyService.getAllProperties(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Properties retrieved successfully", properties));
    }
}