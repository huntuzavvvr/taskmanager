package com.example.taskmanager.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryDto implements Serializable {
    private Long id;
    private String name;
}
