package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskType;
import com.example.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> create(@RequestBody TaskDto task) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.create(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.findAll());
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
    public List<TaskDto> findByCategory(@PathVariable String name) {
        return taskService.findByCategory(name);
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
