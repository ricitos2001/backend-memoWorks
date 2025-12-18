package com.example.catalog.repositories;

import com.example.catalog.domain.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(boolean status);

    Task getTaskById(Long id);

    Task getTaskByTitle(String title);

    boolean existsByTitle(String title);

    Page<Task> findByAssigmentForEmail(String assigmentFor, Pageable pageable);

    Optional<Object> findTaskByTitle(String title);
}