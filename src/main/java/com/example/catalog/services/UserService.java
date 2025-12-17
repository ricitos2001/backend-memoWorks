package com.example.catalog.services;

import com.example.catalog.domain.dto.*;
import com.example.catalog.domain.entities.User;
import com.example.catalog.mappers.UserMapper;
import com.example.catalog.repositories.UserRepository;
import com.example.catalog.web.exceptions.DuplicatedUserException;
import com.example.catalog.web.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponseDTO> list(Pageable pageable) {
        Page<UserResponseDTO> users = userRepository.findAll(pageable).map(UserMapper::toDTO);
        return users;
    }

    public UserResponseDTO showById(Long id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        } else {
            return UserMapper.toDTO(user);
        }
    }

    public UserResponseDTO showByName(String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        } else {
            return UserMapper.toDTO(user);
        }
    }

    public UserResponseDTO showByEmail(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(email);
        } else {
            return UserMapper.toDTO(user);
        }
    }

    public UserResponseDTO create(UserRequestDTO dto) {
        String username = dto.getUsername() != null ? dto.getUsername().toLowerCase() : null;
        String email = dto.getEmail() != null ? dto.getEmail().toLowerCase() : null;

        if (username != null && userRepository.existsByUsername(username)) {
            throw new DuplicatedUserException(username);
        }
        if (email != null && userRepository.findByEmail(email).isPresent()) {
            throw new DuplicatedUserException(email);
        }

        User user = UserMapper.toEntity(dto);
        if (user.getUsername() != null) user.setUsername(user.getUsername().toLowerCase());
        if (user.getEmail() != null) user.setEmail(user.getEmail().toLowerCase());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);
        return UserMapper.toDTO(savedUser);
    }

    public UserResponseDTO update(Long id, @RequestBody UserRequestDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        // validar unicidad de username y email excluyendo al propio usuario
        if (dto.getUsername() != null && userRepository.existsByUsernameAndIdNot(dto.getUsername(), id)) {
            throw new DuplicatedUserException(dto.getUsername());
        }
        if (dto.getEmail() != null && userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicatedUserException(dto.getEmail());
        }

        updateBasicFields(dto, user);

        // si se provee contraseña, codificar antes de guardar
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return UserMapper.toDTO(updatedUser);
    }

    private void updateBasicFields(UserRequestDTO user, User updatedUser) {
        Optional.ofNullable(user.getName()).ifPresent(updatedUser::setName);
        Optional.ofNullable(user.getSurnames()).ifPresent(updatedUser::setSurnames);
        Optional.ofNullable(user.getUsername()).ifPresent(u -> updatedUser.setUsername(u.toLowerCase()));
        Optional.ofNullable(user.getPhoneNumber()).ifPresent(updatedUser::setPhoneNumber);
        Optional.ofNullable(user.getEmail()).ifPresent(e -> updatedUser.setEmail(e.toLowerCase()));
        Optional.ofNullable(user.getPassword()).ifPresent(updatedUser::setPassword);
        Optional.ofNullable(user.getTasks()).ifPresent(updatedUser::setTasks);
        Optional.ofNullable(user.getRol()).ifPresent(updatedUser::setRol);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) throw new IllegalArgumentException("User not found");
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User createUser(UserRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicatedUserException(dto.getUsername());
        } else {
            User user = UserMapper.toEntity(dto);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            User savedUser = userRepository.save(user);
            return savedUser;
        }
    }
}
