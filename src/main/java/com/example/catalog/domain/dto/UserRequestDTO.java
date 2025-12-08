package com.example.catalog.domain.dto;

import com.example.catalog.domain.entities.Task;
import com.example.catalog.domain.enums.Rol;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @Size(max=25, message = "el nombre no puede tener mas de 25 caracteres")
    private String name;
    @Size(max=25, message = "los apellidos no puede tener mas de 25 caracteres")
    private String surnames;
    @Size(max=20, message = "el nombre de usuario no puede tener mas de 20 caracteres")
    private String username;
    @Size(max=20, message = "el numero de telefono no puede tener mas de 20 caracteres")
    private String phoneNumber;
    @Size(max=50, message = "el correo electronico no puede tener mas de 50 caracteres")
    private String email;
    @Size(min=8, max=20, message = "la contraseña debe tener entre 8 y 20 caracteres")
    private String password;
    @OneToMany
    private List<Task> tasks;
    private Rol rol;
}
