package com.bookstore.microservice.user.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private String username;
    private String role;
    private Integer status;
}
