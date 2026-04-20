package com.realestate.admin.serviceimpl;

import com.realestate.dto.AdminUserDTO;
import com.realestate.admin.service.AdminUserService;
import com.realestate.common.mapper.UserMapper;
import com.realestate.entity.User;
import com.realestate.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AdminUserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public AdminUserDTO createUser(AdminUserDTO dto) {
        if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            throw new RuntimeException("Username already exists: " + dto.getUsername());
        }
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }

        User user = userMapper.toEntity(dto, passwordEncoder.encode(dto.getPassword()));
        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Override
    public AdminUserDTO updateUser(Long userId, AdminUserDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        userMapper.updateEntityFromDTO(user, dto);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.deactivateAccount();
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDTO);
    }
}