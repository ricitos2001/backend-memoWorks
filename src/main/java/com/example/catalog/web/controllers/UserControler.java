package com.example.catalog.web.controllers;

import com.example.catalog.domain.dto.CreateUserDTO;
import com.example.catalog.domain.dto.UpdateUserDTO;
import com.example.catalog.domain.entities.User;
import com.example.catalog.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserControler {

    private final UserService userService;

    public UserControler(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> list() {
        List<User> users = userService.list();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> bookById(@PathVariable(name = "id") Long id) {
        User user = userService.showUser(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody @Valid CreateUserDTO dto) {
        User saved = userService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/users/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<User> toggle(@PathVariable(name = "id") Long id, UpdateUserDTO dto) {
        User toggled = userService.toggle(id, dto);
        return ResponseEntity.ok(toggled);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
