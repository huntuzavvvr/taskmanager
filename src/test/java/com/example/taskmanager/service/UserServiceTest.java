package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.exception.UserNotFoundException;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.model.User;
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
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void createShouldReturnUser(){
        UserDto userDto = new UserDto();
        userDto.setName("test");

        User user = UserMapper.toEntity(userDto);
        user.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertEquals("test", result.getName());
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    void updateShouldReturnUser(){
        UserDto userDto = new UserDto();
        userDto.setName("new name");
        User user = new User();
        user.setId(1L);
        user.setName("old name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto result = userService.update(1L, userDto);
        assertEquals("new name", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteShouldDelete(){
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
