package com.example.catalog.web.controllers;

import com.example.catalog.domain.dto.NotificationResponseDTO;
import com.example.catalog.services.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDTO>> getNotifications(Pageable pageable) {
        Page<NotificationResponseDTO> notifications = service.findAll(pageable);
        return ResponseEntity.ok(notifications);
    }
}
