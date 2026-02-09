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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "usersPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '_' + #pageable.sort.toString()")
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toDto);
    }

    @Cacheable(value = "users", key = "#id")
    public UserDto findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toDto(user);
    }


    @Transactional
    @CacheEvict(value = "usersPage", allEntries = true)
    @CachePut(value = "users", key = "#result.id")
    public UserDto create(UserDto dto) {
        log.info("Creating user: {}", dto);
        User user = UserMapper.toEntity(dto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @CacheEvict(value = "usersPage", allEntries = true)
    @CachePut(value = "users", key = "#id")
    public UserDto update(Long id, UserDto dto) {
        log.info("Updating user: {}", dto);
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setName(dto.getName());
        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @CacheEvict(value = {"users", "usersPage"}, allEntries = true)
    public void delete(Long id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }
}
