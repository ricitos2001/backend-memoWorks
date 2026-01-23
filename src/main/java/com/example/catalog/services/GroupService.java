package com.example.catalog.services;

import com.example.catalog.domain.dto.*;
import com.example.catalog.domain.entities.Group;
import com.example.catalog.domain.entities.User;
import com.example.catalog.mappers.GroupMapper;
import com.example.catalog.repositories.GroupRepository;
import com.example.catalog.repositories.UserRepository;
import com.example.catalog.web.exceptions.DuplicatedGroupException;
import com.example.catalog.web.exceptions.GroupNotFoundException;
import com.example.catalog.web.exceptions.ResourceNotFoundException;
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
public class GroupService {
    public static final String GRUPO_NO_ENCONTRADO_CON = "Usuario no encontrado con ";
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final EmailService emailService;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, FileService fileService, EmailService emailService) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.emailService = emailService;
    }

    public Page<GroupResponseDTO> list(Pageable pageable) {
        Page<GroupResponseDTO> groups = groupRepository.findAll(pageable).map(GroupMapper::toDTO);
        return groups;
    }

    public Page<GroupResponseDTO> listByUserEmail(String email, Pageable pageable) {
        return groupRepository.findByAdminOrMemberEmail(email, pageable).map(GroupMapper::toDTO);
    }

    public GroupResponseDTO showById(Long id) {
        Group group = groupRepository.getGroupById(id);
        if (group == null) {
            throw new GroupNotFoundException(id);
        } else {
            return GroupMapper.toDTO(group);
        }
    }

    public GroupResponseDTO showByName(String name) {
        Group group = groupRepository.getGroupByName(name);
        if (group == null) {
            throw new GroupNotFoundException(name);
        } else {
            return GroupMapper.toDTO(group);
        }
    }

    public GroupResponseDTO create(GroupRequestDTO dto) {
        if (groupRepository.existsByName(dto.getName())) {
            throw new DuplicatedGroupException(dto.getName());
        } else {
            User admin = userRepository.findById(dto.getAdminUser().getId()).orElseThrow(() -> new UserNotFoundException(dto.getAdminUser().getId()));
            Group group = GroupMapper.toEntity(dto);
            group.setAdminUser(admin);
            Group savedGroup = groupRepository.save(group);

            // notificar al admin
            try {
                String subject = "Nuevo grupo creado: " + savedGroup.getName();
                Map<String, Object> model = new HashMap<>();
                model.put("group", savedGroup);
                model.put("user", admin);
                emailService.sendTemplateEmail(admin.getEmail(), subject, "group-created.html", model);
            } catch (Exception e) {
                try {
                    emailService.sendSimpleEmail(admin.getEmail(), "Nuevo grupo creado: " + savedGroup.getName(), "Se ha creado el grupo: " + savedGroup.getName());
                } catch (Exception ex) {
                    // ignore
                }
            }

            return GroupMapper.toDTO(savedGroup);
        }
    }

    public GroupResponseDTO update(Long id, @RequestBody GroupRequestDTO dto) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException(id));
        User admin = userRepository.findById(dto.getAdminUser().getId()).orElseThrow(() -> new UserNotFoundException(dto.getAdminUser().getId()));
        group.setAdminUser(admin);
        updateBasicFields(dto, group);
        Group updatedGroup = groupRepository.save(group);
        // notificar al admin sobre actualización
        try {
            String subject = "Grupo actualizado: " + updatedGroup.getName();
            Map<String, Object> model = new HashMap<>();
            model.put("group", updatedGroup);
            model.put("user", admin);
            emailService.sendTemplateEmail(admin.getEmail(), subject, "group-updated.html", model);
        } catch (Exception e) {
            try { emailService.sendSimpleEmail(admin.getEmail(), "Grupo actualizado: " + updatedGroup.getName(), "El grupo ha sido actualizado: " + updatedGroup.getName()); } catch (Exception ex) { }
        }
        return GroupMapper.toDTO(updatedGroup);
    }

    private void updateBasicFields(GroupRequestDTO group, Group updatedGroup) {
        Optional.ofNullable(group.getName()).ifPresent(updatedGroup::setName);
        Optional.ofNullable(group.getDescription()).ifPresent(updatedGroup::setDescription);
        Optional.ofNullable(group.getAdminUser()).ifPresent(updatedGroup::setAdminUser);
        Optional.ofNullable(group.getUsers()).ifPresent(updatedGroup::setUsers);
        Optional.ofNullable(group.getImage()).ifPresent(updatedGroup::setImage);
    }


    public void delete(Long id) {
        if (!groupRepository.existsById(id)) throw new IllegalArgumentException("Group not found");
        Group grp = groupRepository.findById(id).orElse(null);
        groupRepository.deleteById(id);
        if (grp != null) {
            try {
                String subject = "Grupo eliminado: " + grp.getName();
                Map<String, Object> model = new HashMap<>();
                model.put("group", grp);
                emailService.sendTemplateEmail(grp.getAdminUser().getEmail(), subject, "group-deleted.html", model);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public Resource obtenerAvatarGenerico(Long id) {
        Group grupo = obtenerGrupoPorId(id);
        if (grupo.getImage() == null || grupo.getImage().isEmpty()) {
            throw new ResourceNotFoundException("El usuario no tiene un avatar asignado.");
        }
        return fileService.cargarFichero(grupo.getImage());
    }

    public void guardarAvatar(Long grupoId, MultipartFile avatar) throws IOException {
        validarTamanoArchivo(avatar);
        validarTipoDeArchivo(avatar);
        Group grupo = groupRepository.findById(grupoId).orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado con id: " + grupoId));
        String rutaArchivo = fileService.guardarFichero(grupoId, avatar);
        grupo.setImage(rutaArchivo);
        groupRepository.save(grupo);
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

    public Group obtenerGrupoPorId(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(GRUPO_NO_ENCONTRADO_CON + "id " + id));
    }
}
