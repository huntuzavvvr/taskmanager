package com.example.taskmanager.service;

import com.example.taskmanager.dto.CategoryDto;
import com.example.taskmanager.exception.CategoryNotFoundException;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void createShouldReturnCategory() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("category");

        Category category = new Category();
        category.setId(1L);
        category.setName(categoryDto.getName());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        CategoryDto result = categoryService.create(categoryDto);
        assertEquals("category", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateShouldThrowIfCategoryNotFound() {
        CategoryDto categoryDto = new CategoryDto();
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(CategoryNotFoundException.class, () -> categoryService.update(1L, categoryDto));
    }

    @Test
    void deleteShouldDeleteCategory() {
        categoryService.delete(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

}
