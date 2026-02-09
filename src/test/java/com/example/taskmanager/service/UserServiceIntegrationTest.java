package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void create(){
        UserDto userDto = new UserDto();
        userDto.setName("name");
        UserDto result = userService.create(userDto);
        assertEquals("name", result.getName());

    }

    @Test
    void update(){
        UserDto userDto = new UserDto();
        userDto.setName("new name");
        User user = new User();
        user.setName("name");
        user = userRepository.save(user);
        UserDto result = userService.update(user.getId(), userDto);
        assertEquals("new name", result.getName());
    }

    @Test
    void delete(){
        User user = new User();
        user.setName("name");
        user = userRepository.save(user);
        userService.delete(user.getId());
        Optional<User> result = userRepository.findById(user.getId());
        assertTrue(result.isEmpty());
    }
}
