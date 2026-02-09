package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.event.TaskEvent;
import com.example.taskmanager.event.TaskEventType;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.mapper.CategoryMapper;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.model.TaskType;
import com.example.taskmanager.producer.TaskEventProducer;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TaskEventProducer taskEventProducer;

    @Transactional
    @CacheEvict(value = {"tasksPage", "tasksByType", "tasksByCategory"}, allEntries = true)
    @CachePut(value = "tasks", key = "#result.id")
    public TaskDto create(TaskDto dto) {
        log.info("Creating task: {}", dto);
        Task task = TaskMapper.toEntity(dto);

        if (dto.getUserId() != null) {
            var user = UserMapper.toEntity(userService.findById(dto.getUserId()));
            task.setUser(user);
        }

        if (dto.getCategoryId() != null) {
            var category = CategoryMapper.toEntity(categoryService.findById(dto.getCategoryId()));
            task.setCategory(category);
        }
        TaskDto result = TaskMapper.toDto(taskRepository.save(task));
        taskEventProducer.sendMessage(new TaskEvent(result.getId(), TaskEventType.CREATED));
        return result;
    }

//    @Cacheable(value = "tasksAll")
//    public List<TaskDto> findAll() {
//        log.info("Finding all tasks");
//        return taskRepository.findAll()
//                .stream()
//                .map(TaskMapper::toDto)
//                .collect(Collectors.toList());
//    }

    @Cacheable(value = "tasksPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '_' + #pageable.sort.toString()")
    public Page<TaskDto> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable).map(TaskMapper::toDto);
    }

    public Page<TaskDto> findAll(Specification<Task> specification, Pageable pageable) {
        return taskRepository.findAll(specification, pageable).map(TaskMapper::toDto);
    }

    @Cacheable(value = "tasks", key = "#id")
    public TaskDto findById(Long id) {
        var task = taskRepository.findById(id)
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

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "tasksPage", allEntries = true),
            @CacheEvict(value = "tasksByCategory", key = "#result.categoryId"),
            @CacheEvict(value = "tasksByType", key = "#result.type.name")

    })
    @CachePut(value = "tasks", key = "#id")
    public TaskDto update(Long id, TaskDto dto) {
        log.info("Updating task: {}", dto);
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getType() != null) task.setType(dto.getType());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());

        if (dto.getUserId() != null) {
            task.setUser(UserMapper.toEntity(userService.findById(dto.getUserId())));
        }

        if (dto.getCategoryId() != null) {
            task.setCategory(CategoryMapper.toEntity(categoryService.findById(dto.getCategoryId())));
        }

        taskEventProducer.sendMessage(new TaskEvent(task.getId(), TaskEventType.UPDATED));
        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "tasks", key = "#id"),
            @CacheEvict(value = {"tasksPage", "tasksByType", "tasksByCategory"}, allEntries = true)
    })
    public void delete(Long id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
        taskEventProducer.sendMessage(new TaskEvent(id, TaskEventType.DELETED));
    }
}
