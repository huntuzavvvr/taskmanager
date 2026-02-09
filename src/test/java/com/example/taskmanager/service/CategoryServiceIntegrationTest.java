package com.example.taskmanager.service;

import com.example.taskmanager.dto.CategoryDto;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void cleanUp() {
        categoryRepository.deleteAll();
    }

    @Test
    void create(){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("category");

        CategoryDto result = categoryService.create(categoryDto);
        assertEquals("category", result.getName());

    }

    @Test
    void update(){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("new category");

        Category category = new Category();
        category.setName("category");

        category = categoryRepository.save(category);

        CategoryDto result = categoryService.update(category.getId(), categoryDto);

        assertEquals("new category", result.getName());
    }

    @Test
    void delete(){
        Category category = new Category();
        category.setName("category");
        category = categoryRepository.save(category);
        categoryService.delete(category.getId());
        Optional<Category> result = categoryRepository.findById(category.getId());

        assertTrue(result.isEmpty());
    }

}
