package com.example.catalog.services;

import com.example.catalog.domain.dto.CreateGroupDTO;
import com.example.catalog.domain.dto.CreateTaskDTO;
import com.example.catalog.domain.dto.UpdateGroupDTO;
import com.example.catalog.domain.entities.Group;
import com.example.catalog.domain.entities.Task;
import com.example.catalog.repositories.GroupRepository;
import com.example.catalog.repositories.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;
    public GroupService(GroupRepository groupRepository) { this.groupRepository = groupRepository; }

    public List<Group> list() {
        return groupRepository.findAll();
    }

    public Group showGroup(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Group not found"));
    }

    public Group create(CreateGroupDTO dto) {
        Group newGroup = Group.builder().adminUser(dto.adminUser()).users(dto.users()).build();
        return groupRepository.save(newGroup);
    }

    public Group toggle(Long id, @RequestBody UpdateGroupDTO dto) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Group not found"));
        group.setAdminUser(dto.adminUser());
        group.setUsers(dto.users());
        return group;
    }

    public void delete(Long id) {
        if (!groupRepository.existsById(id)) throw new IllegalArgumentException("Group not found");
        groupRepository.deleteById(id);
    }
}
