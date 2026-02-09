package com.example.taskmanager.service;

import com.example.taskmanager.dto.CategoryDto;
import com.example.taskmanager.exception.CategoryNotFoundException;
import com.example.taskmanager.mapper.CategoryMapper;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;


    @Cacheable(value = "categoriesPage", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort.toString()")
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(CategoryMapper::toDto);
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryDto findById(Long id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryMapper.toDto(category);
    }

    @Transactional
    @CacheEvict(value = "categoriesPage", allEntries = true)
    @CachePut(value = "categories", key = "#result.id")
    public CategoryDto create(CategoryDto dto) {
        log.info("Creating category: {}", dto);
        var category = CategoryMapper.toEntity(dto);
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categoriesPage", allEntries = true)
    @CachePut(value = "categories", key = "#result.id")
    public CategoryDto update(Long id, CategoryDto dto) {
        log.info("Updating category: {}", dto);
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.setName(dto.getName());
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categories", key = "#id"),
            @CacheEvict(value = "categoriesPage", allEntries = true)
    })
    public void delete(Long id) {
        log.info("Deleting category with id: {}", id);
        categoryRepository.deleteById(id);
    }
}
