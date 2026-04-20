package com.realestate.admin.service;

import com.realestate.dto.AdminUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {

    AdminUserDTO createUser(AdminUserDTO userDTO);

    AdminUserDTO updateUser(Long userId, AdminUserDTO userDTO);

    void deleteUser(Long userId);

    AdminUserDTO getUserById(Long userId);

    Page<AdminUserDTO> getAllUsers(Pageable pageable);
}