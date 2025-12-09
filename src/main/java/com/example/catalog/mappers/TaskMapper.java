package com.example.catalog.mappers;

import com.example.catalog.domain.dto.TaskRequestDTO;
import com.example.catalog.domain.dto.TaskResponseDTO;
import com.example.catalog.domain.entities.Task;

public class TaskMapper {
    public static Task toEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDate(dto.getDate());
        task.setAssigmentFor(dto.getAssigmentFor());
        task.setStatus(dto.getStatus());
        task.setLabels(dto.getLabels());
        return task;
    }

    public static TaskResponseDTO toDTO(Task task) {
       return new TaskResponseDTO(
               task.getId(),
               task.getTitle(),
               task.getDescription(),
               task.getDate(),
               task.getAssigmentFor(),
               task.getStatus(),
               task.getLabels()
       );
    }
}
