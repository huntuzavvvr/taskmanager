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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @CacheEvict(value = {"tasksAll", "tasksByType", "tasksByCategory"}, allEntries = true)
    @CachePut(value = "tasks", key = "#result.id")
    public TaskDto create(TaskDto dto) {
        log.info("Creating task: {}", dto);
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

    @Cacheable(value = "tasksByType", key = "#type.name")
    public List<TaskDto> findByType(TaskType type) {
        return taskRepository.findByType(type)
                .stream()
                .map(TaskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "tasksByCategory", key = "#categoryName")
    public List<TaskDto> findByCategory(String categoryName) {
        return taskRepository.findByCategory_Name(categoryName)
                .stream()
                .map(TaskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(value = "tasksAll", allEntries = true),
            @CacheEvict(value = "tasksByCategory", key = "#result.categoryId"),
            @CacheEvict(value = "tasksByType", key = "#result.type.name")

    })
    @CachePut(value = "tasks", key = "#id")
    public TaskDto update(Long id, TaskDto dto) {
        log.info("Updating task: {}", dto);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getType() != null) task.setType(dto.getType());
        if (dto.getCompleted() != null) task.setCompleted(dto.getCompleted());

        if (dto.getUserId() != null) {
            task.setUser(UserMapper.toEntity(userService.findById(dto.getUserId())));
        }

        if (dto.getCategoryId() != null) {
            task.setCategory(CategoryMapper.toEntity(categoryService.findById(dto.getCategoryId())));
        }

        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Caching(evict = {
            @CacheEvict(value = "tasks", key = "#id"),
            @CacheEvict(value = {"tasksAll", "tasksByType", "tasksByCategory"}, allEntries = true)
    })
    public void delete(Long id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
    }
}
