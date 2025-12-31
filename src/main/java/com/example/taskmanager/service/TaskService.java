package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.mapper.CategoryMapper;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.model.TaskType;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @CacheEvict(value = {"tasks", "tasksAll"}, allEntries = true)
    public TaskDto create(TaskDto dto) {
        Task task = TaskMapper.toEntity(dto);

        if (dto.getUserId() != null) {
            User user = UserMapper.toEntity(userService.findById(dto.getUserId()));
            task.setUser(user);
        }

        if (dto.getCategoryId() != null) {
            Category category = CategoryMapper.toEntity(categoryService.findById(dto.getCategoryId()));
            task.setCategory(category);
        }

        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Cacheable(value = "tasksAll")
    public List<TaskDto> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(TaskMapper::toDto)
                .collect(Collectors.toList());
    }


    @Cacheable(value = "tasks", key = "#id")
    public TaskDto findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskMapper.toDto(task);
    }

    @Cacheable(value = "tasks", key = "#type.name")
    public List<TaskDto> findByType(TaskType type) {
        return taskRepository.findByType(type)
                .stream()
                .map(TaskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "tasks", key = "#categoryName")
    public List<TaskDto> findByCategory(String categoryName) {
        return taskRepository.findByCategory_Name(categoryName)
                .stream()
                .map(TaskMapper::toDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"tasks", "tasksAll"}, allEntries = true)
    public TaskDto update(Long id, TaskDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setDescription(dto.getDescription());
        task.setCompleted(dto.isCompleted());
        task.setType(dto.getType());

        if (dto.getUserId() != null) {
            task.setUser(UserMapper.toEntity(userService.findById(dto.getUserId())));
        }

        if (dto.getCategoryId() != null) {
            task.setCategory(CategoryMapper.toEntity(categoryService.findById(dto.getCategoryId())));
        }

        return TaskMapper.toDto(taskRepository.save(task));
    }

    @CacheEvict(value = {"tasks", "tasksAll"}, allEntries = true)
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
