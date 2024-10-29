package com.todo.backend.dto;

import java.util.Set;

import com.todo.backend.model.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @NotEmpty
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    private String username;

    @NotEmpty
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
    private String password;

    @NotEmpty
    private Set<String> roles;

    public User toUser() {
        return new User(null, username, password, roles);
    }
}
