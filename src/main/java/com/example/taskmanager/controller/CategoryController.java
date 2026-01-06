package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CategoryDto;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @RequestBody CategoryDto category) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.update(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
