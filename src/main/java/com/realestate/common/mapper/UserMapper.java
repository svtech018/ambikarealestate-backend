package com.realestate.common.mapper;

import com.realestate.dto.AdminUserDTO;
import com.realestate.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public AdminUserDTO toDTO(User user) {
        return AdminUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .location(user.getLocation())
                .role(user.getRole())
                .active(user.getActive())
                .build();
    }

    public User toEntity(AdminUserDTO dto, String encodedPassword) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(encodedPassword)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .location(dto.getLocation())
                .role(dto.getRole())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public void updateEntityFromDTO(User user, AdminUserDTO dto) {
        if (dto.getFirstName() != null)
            user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)
            user.setLastName(dto.getLastName());
        if (dto.getPhoneNumber() != null)
            user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getLocation() != null)
            user.setLocation(dto.getLocation());
        if (dto.getRole() != null)
            user.setRole(dto.getRole());
        if (dto.getActive() != null)
            user.setActive(dto.getActive());
        if (dto.getEmail() != null)
            user.setEmail(dto.getEmail());
    }
}
