package com.bookstore.microservice.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    private String name;
    private String email;
    private String username;
    private String role;

    public UserDTO() {
    }
}
