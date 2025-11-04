package com.example.catalog.web.controllers;

import com.example.catalog.domain.dto.CreateGroupDTO;
import com.example.catalog.domain.dto.CreateTaskDTO;
import com.example.catalog.domain.dto.UpdateGroupDTO;
import com.example.catalog.domain.entities.Group;
import com.example.catalog.domain.entities.Task;
import com.example.catalog.services.GroupService;
import com.example.catalog.services.TaskService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<Group>> list() {
        List<Group> groups = groupService.list();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> bookById(@PathVariable(name = "id") Long id) {
        Group group = groupService.showGroup(id);
        return ResponseEntity.ok(group);
    }

    @PostMapping
    public ResponseEntity<Group> create(@RequestBody @Valid CreateGroupDTO dto) {
        Group saved = groupService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/groups/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Group> toggle(@PathVariable(name = "id") Long id, UpdateGroupDTO dto) {
        Group toggled = groupService.toggle(id, dto);
        return ResponseEntity.ok(toggled);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
