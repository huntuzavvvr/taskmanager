package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.TaskType;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> create(@RequestBody TaskDto task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(task));
    }

//    @GetMapping
//    public ResponseEntity<List<TaskDto>> getAll() {
//        return ResponseEntity.status(HttpStatus.OK).body(taskService.findAll());
//    }

    @GetMapping
    public ResponseEntity<Page<TaskDto>> findAll(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Specification<Task> specification = Specification.anyOf();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        if (startDate == null && status == null) {
            return ResponseEntity.status(HttpStatus.OK).body(taskService.findAll(pageable));

        }

        if (startDate != null) specification = specification.and(TaskSpecification.createdAfter(startDate));
        if (status != null) specification = specification.and(TaskSpecification.byStatus(status));

        return ResponseEntity.status(HttpStatus.OK).body(taskService.findAll(specification, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.findById(id));
    }

    @GetMapping("/type")
    public ResponseEntity<List<TaskDto>> findByType(@RequestParam TaskType type) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.findByType(type));
    }

    @GetMapping("/category/{name}")
    public ResponseEntity<List<TaskDto>> findByCategory(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.findByCategory(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> update(@PathVariable Long id, @RequestBody TaskDto task) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.update(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
