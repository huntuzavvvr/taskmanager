package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.CategoryDto;
import com.example.taskmanager.model.Category;

public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        if (category == null) return null;

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public static Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }
}
