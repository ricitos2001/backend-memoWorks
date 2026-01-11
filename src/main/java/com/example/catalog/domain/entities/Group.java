package com.example.catalog.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String description;
    //@JoinColumn(nullable = false, unique = true)
    @OneToOne
    private User adminUser;
    @OneToMany
    //@Column(nullable = false)
    @JsonBackReference
    private List<User> users;
}
