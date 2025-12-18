package com.example.catalog.services;

import com.example.catalog.domain.dto.TaskRequestDTO;
import com.example.catalog.domain.dto.TaskResponseDTO;
import com.example.catalog.domain.entities.Task;
import com.example.catalog.domain.entities.User;
import com.example.catalog.mappers.TaskMapper;
import com.example.catalog.repositories.TaskRepository;
import com.example.catalog.repositories.UserRepository;
import com.example.catalog.web.exceptions.DuplicatedTaskException;
import com.example.catalog.web.exceptions.TaskNotFoundException;
import com.example.catalog.web.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) { this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Page<TaskResponseDTO> list(Pageable pageable) {
        Page<TaskResponseDTO> tasks = taskRepository.findAll(pageable).map(TaskMapper::toDTO);
        return tasks;
    }

    public Page<TaskResponseDTO> listByUserEmail(String email, Pageable pageable) {
        return taskRepository.findByAssigmentForEmail(email, pageable).map(TaskMapper::toDTO);
    }

    public TaskResponseDTO showById(Long id) {
        Task task = taskRepository.getTaskById(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        } else {
            return TaskMapper.toDTO(task);
        }
    }

    public TaskResponseDTO showByTitle(String title) {
        Task task = taskRepository.getTaskByTitle(title);
        if (task == null) {
            throw new TaskNotFoundException(title);
        } else {
            return TaskMapper.toDTO(task);
        }
    }

    public TaskResponseDTO create(TaskRequestDTO dto) {
        if (taskRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicatedTaskException(dto.getTitle());
        } else {
            User user = userRepository.findById(dto.getAssigmentFor().getId()).orElseThrow(() -> new UserNotFoundException(dto.getAssigmentFor().getId()));
            Task task = TaskMapper.toEntity(dto);
            task.setAssigmentFor(user);
            Task savedTask = taskRepository.save(task);
            return TaskMapper.toDTO(savedTask);
        }
    }

    public TaskResponseDTO update(Long id, @RequestBody TaskRequestDTO dto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        User user = userRepository.findById(dto.getAssigmentFor().getId()).orElseThrow(() -> new UserNotFoundException(dto.getAssigmentFor().getId()));
        task.setAssigmentFor(user);
        updateBasicFields(dto, task);
        Task updatedTask = taskRepository.save(task);
        return TaskMapper.toDTO(updatedTask);
    }

    private void updateBasicFields(TaskRequestDTO task, Task updatedTask) {
        Optional.ofNullable(task.getTitle()).ifPresent(updatedTask::setTitle);
        Optional.ofNullable(task.getDescription()).ifPresent(updatedTask::setDescription);
        Optional.ofNullable(task.getDate()).ifPresent(updatedTask::setDate);
        Optional.ofNullable(task.getTime()).ifPresent(updatedTask::setTime);
        Optional.ofNullable(task.getAssigmentFor()).ifPresent(updatedTask::setAssigmentFor);
        Optional.ofNullable(task.getStatus()).ifPresent(updatedTask::setStatus);
        Optional.ofNullable(task.getLabels()).ifPresent(updatedTask::setLabels);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) throw new IllegalArgumentException("Task not found");
        taskRepository.deleteById(id);
    }
}