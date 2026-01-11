package com.example.catalog.repositories;

import com.example.catalog.domain.entities.Group;
import com.example.catalog.domain.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group getGroupById(Long id);

    Group getGroupByName(String name);

    boolean existsByName(String name);

    Page<Group> findByAdminUserEmail(String adminUserEmail, Pageable pageable);
}
