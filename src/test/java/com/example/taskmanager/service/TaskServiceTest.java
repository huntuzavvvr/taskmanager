package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.event.TaskEventType;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.mapper.CategoryMapper;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.producer.TaskEventProducer;
import com.example.taskmanager.repository.CategoryRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TaskEventProducer taskEventProducer;

    @Test
    public void createShouldReturnTaskAndSendEvent(){
        TaskDto taskDto = new TaskDto();
        taskDto.setCategoryId(1L);
        taskDto.setUserId(1L);
        taskDto.setDescription("description");

        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Task task = TaskMapper.toEntity(taskDto);
        task.setId(2L);


        when(userService.findById(1L)).thenReturn(UserMapper.toDto(user));
        when(categoryService.findById(1L)).thenReturn(CategoryMapper.toDto(category));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto result = taskService.create(taskDto);

        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskEventProducer).sendMessage(argThat(e -> e.type() ==  TaskEventType.CREATED));
        assertEquals(2L, result.getId());
    }

    @Test
    public void updateShouldThrowIfTaskNotFound(){
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.update(1L, new TaskDto()));
    }

    @Test
    public void deleteShouldDeleteAndSendEvent(){
        taskService.delete(1L);
        verify(taskRepository, times(1)).deleteById(1L);
        verify(taskEventProducer).sendMessage(argThat(e -> e.type() ==  TaskEventType.DELETED));
    }
}
