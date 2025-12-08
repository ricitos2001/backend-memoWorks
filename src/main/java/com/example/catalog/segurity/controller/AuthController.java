package com.example.catalog.segurity.controller;

import com.example.catalog.domain.dto.UserRequestDTO;
import com.example.catalog.domain.dto.UserResponseDTO;
import com.example.catalog.domain.entities.User;
import com.example.catalog.segurity.dto.AuthResponse;
import com.example.catalog.segurity.dto.UserLoginDTO;
import com.example.catalog.segurity.jwt.JwtUtil;
import com.example.catalog.segurity.user.CustomUserDetails;
import com.example.catalog.services.TokenBlacklistService;
import com.example.catalog.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;


    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/authenticate")
    public AuthResponse authenticate(@RequestBody UserLoginDTO request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtUtil.generateToken((CustomUserDetails) authentication.getPrincipal());

        //Crear la cookie
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true); // Solo accesible desde el servidor
        jwtCookie.setSecure(true); // Solo en conexiones HTTPS
        jwtCookie.setPath("/"); // Disponible para toda la aplicación
        jwtCookie.setMaxAge(60 * 60 * 10); // Validez de 10 horas

        // Agregar la cookie a la respuesta
        response.addCookie(jwtCookie);
        return new AuthResponse(token);
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Manejar el token desde el encabezado Authorization
        String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        // Manejar el token desde la cookie
        if (jwt == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                }
            }
        }

        // Si se encuentra el token, añadirlo a la blacklist
        if (jwt != null) {
            tokenBlacklistService.addTokenToBlacklist(jwt);

            // Eliminar la cookie
            Cookie jwtCookie = new Cookie("jwt", null);
            jwtCookie.setPath("/");
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(0); // Caducar inmediatamente
            response.addCookie(jwtCookie);

            return ResponseEntity.ok("Logout exitoso. Token añadido a la blacklist y cookie eliminada.");
        }

        return ResponseEntity.badRequest().body("No se encontró un token válido para cerrar sesión.");
    }

    // Registro de usuario
    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid UserRequestDTO dto) {
        // Registrar usuario usando el servicio
        User newUser = userService.createUser(dto);

        // Generar token JWT para el nuevo usuario
        String token = jwtUtil.generateToken(new CustomUserDetails(newUser));

        // Devolver el token en la respuesta
        return new AuthResponse(token);
    }
}
