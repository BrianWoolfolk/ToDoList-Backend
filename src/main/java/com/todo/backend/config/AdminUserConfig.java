package com.todo.backend.config;

import com.todo.backend.dto.UserDTO;
import com.todo.backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class AdminUserConfig {

    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    public AdminUserConfig(UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userService.findByUsername(adminUsername).isEmpty()) {
            UserDTO adminUser = new UserDTO();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_ADMIN");
            adminUser.setRoles(roles);
            userService.create(adminUser);
        }
    }
}