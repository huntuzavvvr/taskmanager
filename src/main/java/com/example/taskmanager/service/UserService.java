package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.exception.UserNotFoundException;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "usersAll")
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "users", key = "#id")
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toDto(user);
    }

    @CacheEvict(value = {"users", "usersAll"}, allEntries = true)
    public UserDto create(UserDto dto) {
        User user = UserMapper.toEntity(dto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @CacheEvict(value = {"users", "usersAll"}, allEntries = true)
    public UserDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setName(dto.getName());
        return UserMapper.toDto(userRepository.save(user));
    }

    @CacheEvict(value = {"users", "usersAll"}, allEntries = true)
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
