package com.example.taskmanager.dto;

import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.TaskType;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskDto implements Serializable {
    private Long id;
    private String description;
    private TaskStatus status;
    private TaskType type;
    private Long userId;
    private Long categoryId;
}
