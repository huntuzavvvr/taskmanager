package com.example.taskmanager.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name="task_seq", sequenceName = "task_seq", allocationSize = 1)
    private Long id;

    private String description;
    private boolean completed;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @ManyToOne
    private User user;

    @ManyToOne
    private Category category;
}
