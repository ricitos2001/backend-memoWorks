package com.example.catalog.domain.dto;

import com.example.catalog.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private User assigmentFor;
    private Boolean status;
    private List<String> labels;
}
