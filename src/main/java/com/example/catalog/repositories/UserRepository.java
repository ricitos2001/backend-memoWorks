package com.example.catalog.repositories;

import com.example.catalog.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User getUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    User getUserById(Long id);

    Optional<User> findUserByEmail(String email);

    User getUserByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmailAndIdNot(String email, Long id);
}
