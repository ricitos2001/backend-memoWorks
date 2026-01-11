package com.example.catalog.services;

import com.example.catalog.domain.dto.*;
import com.example.catalog.domain.entities.Group;
import com.example.catalog.domain.entities.User;
import com.example.catalog.mappers.GroupMapper;
import com.example.catalog.repositories.GroupRepository;
import com.example.catalog.repositories.UserRepository;
import com.example.catalog.web.exceptions.DuplicatedGroupException;
import com.example.catalog.web.exceptions.GroupNotFoundException;
import com.example.catalog.web.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository) { this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public Page<GroupResponseDTO> list(Pageable pageable) {
        Page<GroupResponseDTO> groups = groupRepository.findAll(pageable).map(GroupMapper::toDTO);
        return groups;
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
            return GroupMapper.toDTO(savedGroup);
        }
    }

    public GroupResponseDTO update(Long id, @RequestBody GroupRequestDTO dto) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException(id));
        User admin = userRepository.findById(dto.getAdminUser().getId()).orElseThrow(() -> new UserNotFoundException(dto.getAdminUser().getId()));
        group.setAdminUser(admin);
        updateBasicFields(dto, group);
        Group updatedGroup = groupRepository.save(group);
        return GroupMapper.toDTO(updatedGroup);
    }

    private void updateBasicFields(GroupRequestDTO group, Group updatedGroup) {
        Optional.ofNullable(group.getName()).ifPresent(updatedGroup::setName);
        Optional.ofNullable(group.getDescription()).ifPresent(updatedGroup::setDescription);
        Optional.ofNullable(group.getAdminUser()).ifPresent(updatedGroup::setAdminUser);
        Optional.ofNullable(group.getUsers()).ifPresent(updatedGroup::setUsers);
    }


    public void delete(Long id) {
        if (!groupRepository.existsById(id)) throw new IllegalArgumentException("Group not found");
        groupRepository.deleteById(id);
    }
}
