package com.example.taskmanager.service;

import com.example.taskmanager.dto.CategoryDto;
import com.example.taskmanager.exception.CategoryNotFoundException;
import com.example.taskmanager.mapper.CategoryMapper;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Cacheable(value = "categoriesAll")
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryMapper.toDto(category);
    }

    @CacheEvict(value = {"categories", "categoriesAll"}, allEntries = true)
    public CategoryDto create(CategoryDto dto) {
        Category category = CategoryMapper.toEntity(dto);
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @CacheEvict(value = {"categories", "categoriesAll"}, allEntries = true)
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.setName(dto.getName());
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @CacheEvict(value = {"categories", "categoriesAll"}, allEntries = true)
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
