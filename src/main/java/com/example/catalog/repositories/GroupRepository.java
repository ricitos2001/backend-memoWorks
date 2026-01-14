package com.example.catalog.repositories;

import com.example.catalog.domain.entities.Group;
import com.example.catalog.domain.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group getGroupById(Long id);

    Group getGroupByName(String name);

    boolean existsByName(String name);

    Page<Group> findByAdminUserEmail(String adminUserEmail, Pageable pageable);

    // Busca grupos donde el usuario sea admin o esté en la colección users (miembro).
    // Se usa DISTINCT para evitar duplicados si coincide en ambas condiciones.
    @Query("SELECT DISTINCT g FROM Group g LEFT JOIN g.users u WHERE g.adminUser.email = :email OR u.email = :email")
    Page<Group> findByAdminOrMemberEmail(@Param("email") String email, Pageable pageable);
}
