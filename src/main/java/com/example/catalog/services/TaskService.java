package com.example.catalog.services;

import com.example.catalog.domain.dto.TaskRequestDTO;
import com.example.catalog.domain.dto.TaskResponseDTO;
import com.example.catalog.domain.entities.Task;
import com.example.catalog.domain.entities.User;
import com.example.catalog.mappers.TaskMapper;
import com.example.catalog.repositories.TaskRepository;
import com.example.catalog.repositories.UserRepository;
import com.example.catalog.web.exceptions.DuplicatedTaskException;
import com.example.catalog.web.exceptions.ResourceNotFoundException;
import com.example.catalog.web.exceptions.TaskNotFoundException;
import com.example.catalog.web.exceptions.UserNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.catalog.services.email.EmailService;

@Service
@Transactional
public class TaskService {
    public static final String TAREA_NO_ENCONTRADA_CON = "Usuario no encontrado con ";
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final EmailService emailService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, FileService fileService, EmailService emailService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.emailService = emailService;
    }

    public Page<TaskResponseDTO> list(Pageable pageable) {
        Page<TaskResponseDTO> tasks = taskRepository.findAll(pageable).map(TaskMapper::toDTO);
        return tasks;
    }

    public Page<TaskResponseDTO> listByUserEmail(String email, Pageable pageable) {
        return taskRepository.findByAssigmentForEmail(email, pageable).map(TaskMapper::toDTO);
    }

    public TaskResponseDTO showById(Long id) {
        Task task = taskRepository.getTaskById(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        } else {
            return TaskMapper.toDTO(task);
        }
    }

    public TaskResponseDTO showByTitle(String title) {
        Task task = taskRepository.getTaskByTitle(title);
        if (task == null) {
            throw new TaskNotFoundException(title);
        } else {
            return TaskMapper.toDTO(task);
        }
    }

    public TaskResponseDTO create(TaskRequestDTO dto) {
        if (taskRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicatedTaskException(dto.getTitle());
        } else {
            User user = userRepository.findById(dto.getAssigmentFor().getId()).orElseThrow(() -> new UserNotFoundException(dto.getAssigmentFor().getId()));
            Task task = TaskMapper.toEntity(dto);
            task.setAssigmentFor(user);
            Task savedTask = taskRepository.save(task);

            try {
                String subject = "Nueva tarea asignada: " + savedTask.getTitle();
                Map<String, Object> model = new HashMap<>();
                model.put("task", savedTask);
                model.put("user", user);
                emailService.sendTemplateEmail(user.getEmail(), subject, "task-created.html", model);
            } catch (Exception e) {
                String subject = "Nueva tarea asignada: " + savedTask.getTitle();
                String text = "Te han asignado la tarea: " + savedTask.getTitle();
                emailService.sendSimpleEmail(user.getEmail(), subject, text);
            }

            return TaskMapper.toDTO(savedTask);
        }
    }

    public TaskResponseDTO update(Long id, @RequestBody TaskRequestDTO dto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        User newUser = userRepository.findById(dto.getAssigmentFor().getId()).orElseThrow(() -> new UserNotFoundException(dto.getAssigmentFor().getId()));
        User oldUser = task.getAssigmentFor();
        task.setAssigmentFor(newUser);
        updateBasicFields(dto, task);
        Task updatedTask = taskRepository.save(task);

        // notificar cambios: si el asignado cambió, notificar a nuevo y (opcional) al antiguo
        try {
            String subject = "Tarea actualizada: " + updatedTask.getTitle();
            Map<String, Object> model = new HashMap<>();
            model.put("task", updatedTask);
            model.put("user", newUser);
            emailService.sendTemplateEmail(newUser.getEmail(), subject, "task-updated.html", model);
        } catch (Exception e) {
            try {
                String subject = "Tarea actualizada: " + updatedTask.getTitle();
                String text = "La tarea ha sido actualizada: " + updatedTask.getTitle();
                emailService.sendSimpleEmail(newUser.getEmail(), subject, text);
            } catch (Exception ex) {
                // ignore
            }
        }

        // if assignment changed, notify old user about unassignment
        if (oldUser != null && !oldUser.getId().equals(newUser.getId())) {
            try {
                String subject = "Has sido desasignado de la tarea: " + updatedTask.getTitle();
                emailService.sendSimpleEmail(oldUser.getEmail(), subject, "Ya no estás asignado a la tarea: " + updatedTask.getTitle());
            } catch (Exception ex) {
                // ignore
            }
        }

        return TaskMapper.toDTO(updatedTask);
    }

    private void updateBasicFields(TaskRequestDTO task, Task updatedTask) {
        Optional.ofNullable(task.getTitle()).ifPresent(updatedTask::setTitle);
        Optional.ofNullable(task.getDescription()).ifPresent(updatedTask::setDescription);
        Optional.ofNullable(task.getDate()).ifPresent(updatedTask::setDate);
        Optional.ofNullable(task.getTime()).ifPresent(updatedTask::setTime);
        Optional.ofNullable(task.getAssigmentFor()).ifPresent(updatedTask::setAssigmentFor);
        Optional.ofNullable(task.getStatus()).ifPresent(updatedTask::setStatus);
        Optional.ofNullable(task.getLabels()).ifPresent(updatedTask::setLabels);
        Optional.ofNullable(task.getImage()).ifPresent(updatedTask::setImage);
    }

    public void delete(Long id) {
        Task tarea = taskRepository.findById(id).orElse(null);
        if (tarea == null) throw new IllegalArgumentException("Task not found");
        taskRepository.deleteById(id);

        // notificar al asignado sobre eliminación
        try {
            if (tarea.getAssigmentFor() != null) {
                String subject = "Tarea eliminada: " + tarea.getTitle();
                Map<String, Object> model = new HashMap<>();
                model.put("task", tarea);
                emailService.sendTemplateEmail(tarea.getAssigmentFor().getEmail(), subject, "task-deleted.html", model);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public Resource obtenerAvatarGenerico(Long id) {
        Task tarea = obtenerTareaPorId(id);
        if (tarea.getImage() == null || tarea.getImage().isEmpty()) {
            throw new ResourceNotFoundException("El usuario no tiene un avatar asignado.");
        }
        return fileService.cargarFichero(tarea.getImage());
    }

    public void guardarAvatar(Long tareaId, MultipartFile avatar) throws IOException {
        validarTamanoArchivo(avatar);
        validarTipoDeArchivo(avatar);
        Task tarea = taskRepository.findById(tareaId).orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + tareaId));
        String rutaArchivo = fileService.guardarFichero(tareaId, avatar);
        tarea.setImage(rutaArchivo);
        taskRepository.save(tarea);
    }

    private void validarTamanoArchivo(MultipartFile avatar) {
        long maxSizeInBytes = 1024 * 1024 * 5L; // 5MB
        if (avatar.getSize() > maxSizeInBytes) {
            throw new IllegalArgumentException("Tamaño de archivo excede el límite de 5MB");
        }
    }

    private void validarTipoDeArchivo(MultipartFile avatar) {
        String contentType = avatar.getContentType();
        if (!Arrays.asList("image/png", "image/jpeg", "image/gif", "image/webp").contains(contentType)) {
            throw new IllegalArgumentException("Tipo de archivo debe ser: (jpeg, png, gif, webp)");
        }
    }

    public Task obtenerTareaPorId(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(TAREA_NO_ENCONTRADA_CON + "id " + id));
    }
}
