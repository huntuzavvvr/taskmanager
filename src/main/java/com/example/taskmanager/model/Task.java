package com.example.taskmanager.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name="task_seq", sequenceName = "task_seq", allocationSize = 1)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(updatable = false,  nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @ManyToOne
    private User user;

    @ManyToOne
    private Category category;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
