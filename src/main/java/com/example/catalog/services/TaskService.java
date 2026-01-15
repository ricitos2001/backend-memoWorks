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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional
public class TaskService {
    public static final String TAREA_NO_ENCONTRADA_CON = "Usuario no encontrado con ";
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, FileService fileService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
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
            return TaskMapper.toDTO(savedTask);
        }
    }

    public TaskResponseDTO update(Long id, @RequestBody TaskRequestDTO dto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        User user = userRepository.findById(dto.getAssigmentFor().getId()).orElseThrow(() -> new UserNotFoundException(dto.getAssigmentFor().getId()));
        task.setAssigmentFor(user);
        updateBasicFields(dto, task);
        Task updatedTask = taskRepository.save(task);
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
        if (!taskRepository.existsById(id)) throw new IllegalArgumentException("Task not found");
        taskRepository.deleteById(id);
    }

    public Resource obtenerAvatarGenerico(Long id) {
        Task tarea = obtenerTareaPorId(id);
        if (tarea.getImage() == null || tarea.getImage().isEmpty()) {
            throw new ResourceNotFoundException("El usuario no tiene un avatar asignado.");
        }
        return fileService.cargarFichero(tarea.getImage());
    }

    public void guardarAvatar(Long usuarioId, MultipartFile avatar) throws IOException {
        validarTamanoArchivo(avatar);
        validarTipoDeArchivo(avatar);
        User usuario = userRepository.findById(usuarioId).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));
        String rutaArchivo = fileService.guardarFichero(usuarioId, avatar);
        usuario.setAvatar(rutaArchivo);
        userRepository.save(usuario);
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