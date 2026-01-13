package com.example.catalog.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SaludoController {

    @GetMapping("/saludo")
    @Operation(summary = "Muestra un saludo personalizado", description = "Devuelve una vista Thymeleaf con un saludo que incluye el nombre y la edad.", parameters = {@Parameter(name = "nombre", description = "El nombre de la persona a saludar"), @Parameter(name = "edad", description = "La edad de la persona a saludar")})
    public String mostrarSaludo(Model model) {
        model.addAttribute("nombre", "Juan");
        model.addAttribute("edad", 25);
        return "saludo"; // Nombre de la plantilla Thymeleaf
    }
}
