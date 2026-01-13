package com.example.catalog.web.controllers;

import com.example.catalog.domain.dto.GroupRequestDTO;
import com.example.catalog.domain.dto.GroupResponseDTO;
import com.example.catalog.domain.dto.TaskResponseDTO;
import com.example.catalog.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    @Operation(summary = "Get paginated list of groups", description = "Retrieve a paginated list of groups.", parameters = {@Parameter(name = "pageable", description = "Pagination information")})
    public ResponseEntity<Page<GroupResponseDTO>> list(Pageable pageable) {
        Page<GroupResponseDTO> groups = groupService.list(pageable);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/myGroups/{email}")
    @Operation(summary = "Get paginated list of groups by user email", description = "Retrieve a paginated list of groups associated with a specific user email.", parameters = {@Parameter(name = "email", description = "User email"), @Parameter(name = "pageable", description = "Pagination information")})
    public ResponseEntity<Page<GroupResponseDTO>> listByUserEmail(@PathVariable(name = "email") String email, Pageable pageable) {
        Page<GroupResponseDTO> groups = groupService.listByUserEmail(email, pageable);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Get group by ID", description = "Retrieve a group by its unique ID.", parameters = {@Parameter(name = "id", description = "Group ID")})
    public ResponseEntity<GroupResponseDTO> getById(@PathVariable(name = "id") Long id) {
        GroupResponseDTO group = groupService.showById(id);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get group by name", description = "Retrieve a group by its unique name.", parameters = {@Parameter(name = "name", description = "Group name")})
    public ResponseEntity<GroupResponseDTO> getByName(@PathVariable(name = "name") String name) {
        GroupResponseDTO group = groupService.showByName(name);
        return ResponseEntity.ok(group);
    }

    @PostMapping
    @Operation(summary = "Create a new group", description = "Create a new group with the provided details.", parameters = {@Parameter(name = "dto", description = "Group details")})
    public ResponseEntity<GroupResponseDTO> create(@RequestBody @Valid GroupRequestDTO dto) {
        GroupResponseDTO saved = groupService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing group", description = "Update an existing group with the provided details.", parameters = {@Parameter(name = "id", description = "Group ID"), @Parameter(name = "dto", description = "Updated group details")})
    public ResponseEntity<GroupResponseDTO> update(@PathVariable(name = "id") Long id, @RequestBody @Valid GroupRequestDTO dto) {
        GroupResponseDTO toggled = groupService.update(id, dto);
        return ResponseEntity.ok(toggled);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a group", description = "Delete a group by its unique ID.", parameters = {@Parameter(name = "id", description = "Group ID")})
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
