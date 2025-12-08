package com.example.catalog.repositories;

import com.example.catalog.domain.dto.UserResponseDTO;
import com.example.catalog.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User getUserByUsername(String username);

    boolean existsByUsername(String username);

    User getUserById(Long id);

    Optional<User> findUserByEmail(String email);
}
