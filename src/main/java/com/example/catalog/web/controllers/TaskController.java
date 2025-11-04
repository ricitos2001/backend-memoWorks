package com.example.catalog.web.controllers;

import com.example.catalog.domain.dto.CreateTaskDTO;
import com.example.catalog.domain.dto.UpdateTaskDTO;
import com.example.catalog.domain.entities.Task;
import com.example.catalog.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> list(@RequestParam(name = "status", required = false) Boolean status) {
        List<Task> tasks = taskService.list(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> bookById(@PathVariable(name = "id") Long id) {
        Task task = taskService.showTask(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody @Valid CreateTaskDTO dto) {
        Task saved = taskService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/tasks/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Task> updateTask(@PathVariable(name = "id") Long id, UpdateTaskDTO dto) {
        Task taskUpdated = taskService.updateTask(id, dto);
        return ResponseEntity.ok(taskUpdated);
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Task> toggle(@PathVariable(name = "id") Long id) {
        Task toggled = taskService.toggle(id);
        return ResponseEntity.ok(toggled);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}