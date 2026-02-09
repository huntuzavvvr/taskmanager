package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.model.*;
import com.example.taskmanager.producer.TaskEventProducer;
import com.example.taskmanager.repository.CategoryRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Mock
    private TaskEventProducer taskEventProducer;

    @BeforeEach
    void cleanUp(){
        taskRepository.deleteAll();
    }

    @Test
    void findPagination(){

        Task taskA = new Task();
        taskA.setType(TaskType.IMPROVEMENT);
        taskA.setStatus(TaskStatus.COMPLETED);
        taskA.setDescription("a");
        taskRepository.save(taskA);
        Task taskB = new Task();
        taskB.setType(TaskType.IMPROVEMENT);
        taskB.setStatus(TaskStatus.COMPLETED);
        taskB.setDescription("c");
        taskRepository.save(taskB);

        Page<TaskDto> page = taskService.findAll(PageRequest.of(0, 2, Sort.by("id").ascending()));

        assertEquals(2, page.getTotalElements());
        assertEquals("a",  page.getContent().get(0).getDescription());
        assertEquals("c", page.getContent().get(1).getDescription());
    }

    @Test
    void create(){
        TaskDto taskDto = new TaskDto();
        taskDto.setDescription("test");
        taskDto.setStatus(TaskStatus.RUNNING);
        taskDto.setType(TaskType.IMPROVEMENT);

        TaskDto created = taskService.create(taskDto);
        System.out.println(created);
        assertNotNull(created.getId());
    }

    @Test
    void update() {
        User user = new User();
        user.setName("test");
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("test cat");
        category = categoryRepository.save(category);

        Task task = new Task();
        task.setDescription("old");
        task.setStatus(TaskStatus.RUNNING);
        task.setType(TaskType.IMPROVEMENT);
        task.setUser(user);
        task.setCategory(category);
        task = taskRepository.save(task);

        TaskDto taskDto = new TaskDto();
        taskDto.setDescription("new");
        taskDto.setStatus(TaskStatus.COMPLETED);

        TaskDto updated = taskService.update(task.getId(), taskDto);

        assertEquals("new", updated.getDescription());
        assertEquals(TaskStatus.COMPLETED, updated.getStatus());
    }

    @Test
    void delete(){
        Task task = new Task();
        task.setDescription("task");
        task.setStatus(TaskStatus.COMPLETED);
        task.setType(TaskType.IMPROVEMENT);
        task = taskRepository.save(task);

        taskService.delete(task.getId());

        Optional<Task> result = taskRepository.findById(task.getId());
        assertTrue(result.isEmpty());
    }
}
