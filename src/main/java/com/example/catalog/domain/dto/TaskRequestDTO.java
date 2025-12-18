package com.example.catalog.domain.dto;

import com.example.catalog.domain.entities.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {
    @Size(max=25, message = "el titulo no puede tener mas de 25 caracteres")
    private String title;
    @Size(max=500, message = "la descripción no puede tener mas de 500 caracteres")
    private String description;
    @NotNull(message = "la fecha es obligatoria")
    private LocalDate date;
    @NotNull(message = "la hora es obligatoria")
    private LocalTime time;
    @NotNull(message = "es obligatorio asignar la tarea a un usuario")
    private User assigmentFor;
    @NotNull(message = "el status es obligatorio")
    private Boolean status;
    @NotNull(message = "debes introducir una etiqueta como minimo")
    private List<String> labels;
    @Column(nullable = true)
    private String image;
}
