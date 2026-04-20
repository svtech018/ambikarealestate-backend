package com.realestate.admin.controller;

import com.realestate.admin.service.AdminUserService;
import com.realestate.dto.AdminUserDTO;
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
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminUserDTO>> createUser(
            @Valid @RequestBody AdminUserDTO dto) {
        AdminUserDTO created = adminUserService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User created successfully", created));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDTO>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserDTO dto) {
        AdminUserDTO updated = adminUserService.updateUser(userId, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updated));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deactivated successfully", null));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDTO>> getUser(@PathVariable Long userId) {
        AdminUserDTO user = adminUserService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminUserDTO>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AdminUserDTO> users = adminUserService.getAllUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }
}