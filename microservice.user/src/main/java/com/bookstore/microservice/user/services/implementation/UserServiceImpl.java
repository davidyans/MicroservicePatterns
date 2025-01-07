package com.bookstore.microservice.user.services.implementation;

import com.bookstore.microservice.user.domain.User;
import com.bookstore.microservice.user.dto.UserDTO;
import com.bookstore.microservice.user.exceptions.DuplicateResourceException;
import com.bookstore.microservice.user.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.user.mappers.UserMapper;
import com.bookstore.microservice.user.repository.UserRepository;
import com.bookstore.microservice.user.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return UserMapper.toUserDTO(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent() ||
                userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Email or Username already exists.");
        }
        User user = UserMapper.toUserEntity(userDTO);
        user.setCreatedAt(LocalDateTime.now());
        return UserMapper.toUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setRole(userDTO.getRole());
        user.setUpdatedAt(LocalDateTime.now());
        return UserMapper.toUserDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
