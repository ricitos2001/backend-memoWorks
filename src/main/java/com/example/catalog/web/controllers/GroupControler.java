package com.example.catalog.web.controllers;

import com.example.catalog.domain.dto.GroupRequestDTO;
import com.example.catalog.domain.dto.GroupResponseDTO;
import com.example.catalog.domain.dto.TaskResponseDTO;
import com.example.catalog.services.GroupService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupControler {

    private final GroupService groupService;

    public GroupControler(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<Page<GroupResponseDTO>> list(Pageable pageable) {
        Page<GroupResponseDTO> groups = groupService.list(pageable);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/myGroups/{email}")
    public ResponseEntity<Page<GroupResponseDTO>> listByUserEmail(@PathVariable(name = "email") String email, Pageable pageable) {
        Page<GroupResponseDTO> groups = groupService.listByUserEmail(email, pageable);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<GroupResponseDTO> getById(@PathVariable(name = "id") Long id) {
        GroupResponseDTO group = groupService.showById(id);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<GroupResponseDTO> getByName(@PathVariable(name = "name") String name) {
        GroupResponseDTO group = groupService.showByName(name);
        return ResponseEntity.ok(group);
    }

    @PostMapping
    public ResponseEntity<GroupResponseDTO> create(@RequestBody @Valid GroupRequestDTO dto) {
        GroupResponseDTO saved = groupService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDTO> update(@PathVariable(name = "id") Long id, @RequestBody @Valid GroupRequestDTO dto) {
        GroupResponseDTO toggled = groupService.update(id, dto);
        return ResponseEntity.ok(toggled);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
