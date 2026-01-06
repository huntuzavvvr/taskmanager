package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.model.Task;

public class TaskMapper {

    public static TaskDto toDto(Task task) {
        if (task == null) return null;

        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        dto.setType(task.getType());
        if (task.getUser() != null) {dto.setUserId(task.getUser().getId());}
        else {dto.setUserId(null);}
        if (task.getCategory() != null) {dto.setCategoryId(task.getCategory().getId());}
        else {dto.setCategoryId(null);}
        return dto;
    }

    public static Task toEntity(TaskDto dto) {
        if (dto == null) return null;

        Task task = new Task();
        task.setId(dto.getId());
        task.setDescription(dto.getDescription());
        task.setCompleted(dto.getCompleted());
        task.setType(dto.getType());
        return task;
    }
}
