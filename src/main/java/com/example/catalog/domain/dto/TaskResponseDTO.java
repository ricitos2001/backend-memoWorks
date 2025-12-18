package com.example.catalog.domain.dto;

import com.example.catalog.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private User assigmentFor;
    private Boolean status;
    private List<String> labels;
    private String image;
}
