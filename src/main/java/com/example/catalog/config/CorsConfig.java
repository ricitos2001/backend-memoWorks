package com.example.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permitir credenciales
        config.setAllowCredentials(true);

        // Orígenes permitidos - añade tu frontend de Render
        config.setAllowedOrigins(Arrays.asList(
                "https://frontend-memoworks.onrender.com",
                "http://localhost:4200"  // Para desarrollo local
        ));

        // Cabeceras permitidas
        config.setAllowedHeaders(Arrays.asList("*"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Cabeceras expuestas
        config.setExposedHeaders(Arrays.asList("Authorization"));

        // Tiempo de caché para preflight (1 hora)
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}