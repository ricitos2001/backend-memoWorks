package com.example.catalog.services;

import com.example.catalog.domain.dto.NotificationRequestDTO;
import com.example.catalog.domain.dto.NotificationResponseDTO;
import com.example.catalog.domain.entities.Notification;
import com.example.catalog.mappers.NotificationMapper;
import com.example.catalog.repositories.NotificationRepository;
import com.example.catalog.web.exceptions.DuplicatedNotificationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Page<NotificationResponseDTO> findAll(Pageable pageable) {
        Page<NotificationResponseDTO> notifications = repository.findAll(pageable).map(NotificationMapper::toDTO);
        return notifications;
    }

    public Page<NotificationResponseDTO> findByUserEmail(String email, Pageable pageable) {
        Page<NotificationResponseDTO> notifications = repository.findByUserEmail(email, pageable).map(NotificationMapper::toDTO);
        return notifications;
    }

    public NotificationResponseDTO create(NotificationRequestDTO dto) {
        if (repository.existsByTitle(dto.getTitle())) {
            throw new DuplicatedNotificationException(dto.getTitle());
        } else {
            Notification notification = NotificationMapper.toEntity(dto);
            if (notification.getTitle() != null) notification.setTitle(notification.getTitle().toLowerCase());
            if (notification.getMessage() != null) notification.setMessage(notification.getMessage().toLowerCase());
            if (notification.getCreatedAt() != null) notification.setCreatedAt(notification.getCreatedAt());
            if (notification.getUserEmail() != null) notification.setUserEmail(notification.getUserEmail());
            Notification savedNotification = repository.save(notification);
            return NotificationMapper.toDTO(savedNotification);
        }
    }
}
