package com.realestate.repository;

import com.realestate.common.constants.UserRole;
import com.realestate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username) OR LOWER(u.email) = LOWER(:email)")
    Optional<User> findByUsernameOrEmailIgnoreCase(@Param("username") String username, @Param("email") String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    Page<User> findByRole(UserRole role, Pageable pageable);

    Page<User> findByRoleAndActive(UserRole role, Boolean active, Pageable pageable);

    Page<User> findByActive(Boolean active, Pageable pageable);

    long countByRole(UserRole role);

    long countByActive(Boolean active);

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.active = true")
    List<User> findAllActiveAdmins();

    Optional<User> findFirstByRoleAndActiveTrueOrderByIdAsc(UserRole role);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
}
