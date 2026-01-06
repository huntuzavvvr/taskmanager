package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.exception.UserNotFoundException;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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


    @CacheEvict(value = "usersAll", allEntries = true)
    @CachePut(value = "users", key = "#result.id")
    public UserDto create(UserDto dto) {
        log.info("Creating user: {}", dto);
        User user = UserMapper.toEntity(dto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @CacheEvict(value = "usersAll", allEntries = true)
    @CachePut(value = "users", key = "#id")
    public UserDto update(Long id, UserDto dto) {
        log.info("Updating user: {}", dto);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setName(dto.getName());
        return UserMapper.toDto(userRepository.save(user));
    }

    @CacheEvict(value = {"users", "usersAll"}, allEntries = true)
    public void delete(Long id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }
}
