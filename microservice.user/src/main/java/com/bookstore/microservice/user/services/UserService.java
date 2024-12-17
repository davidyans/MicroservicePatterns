package com.bookstore.microservice.user.services;

import com.bookstore.microservice.user.dto.UserDTO;
import com.bookstore.microservice.user.domain.User;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
}

