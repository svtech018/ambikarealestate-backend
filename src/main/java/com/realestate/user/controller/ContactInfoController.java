package com.realestate.user.controller;

import com.realestate.common.constants.UserRole;
import com.realestate.dto.ApiResponse;
import com.realestate.dto.PublicContactInfoDTO;
import com.realestate.entity.User;
import com.realestate.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact-info")
public class ContactInfoController {

    private final UserRepository userRepository;

    public ContactInfoController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PublicContactInfoDTO>> getContactInfo() {
        User user = userRepository.findFirstByRoleAndActiveTrueOrderByIdAsc(UserRole.ADMIN)
                .orElseGet(() -> User.builder()
                        .id(0L)
                        .firstName("System")
                        .lastName("Admin")
                        .email("admin@realestate.com")
                        .phoneNumber("")
                        .build());

        PublicContactInfoDTO contactInfo = PublicContactInfoDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .location(user.getLocation())
                .build();

        return ResponseEntity.ok(new ApiResponse<>(true, "Contact info retrieved", contactInfo));
    }
}