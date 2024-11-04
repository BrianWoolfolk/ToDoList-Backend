package com.todo.backend.config;

import com.todo.backend.dto.UserDTO;
import com.todo.backend.repository.UserRepository;
import com.todo.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class AdminUserConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @PostConstruct
    public void init() {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            UserDTO adminUser = new UserDTO();
            Set<String> roles = new HashSet<>();
            String encodedPassword = passwordEncoder.encode(adminPassword);

            adminUser.setUsername(adminUsername);
            adminUser.setPassword(encodedPassword);
            roles.add("ADMIN");
            roles.add("EDITOR");
            roles.add("USER");
            adminUser.setRoles(roles);
            userService.create(adminUser);
        }
    }
}