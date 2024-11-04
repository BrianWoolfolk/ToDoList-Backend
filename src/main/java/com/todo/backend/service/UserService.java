package com.todo.backend.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.todo.backend.dto.UserDTO;
import com.todo.backend.model.User;
import com.todo.backend.repository.UserRepository;
import com.todo.backend.security.JwtTokenUtil;
import com.todo.backend.service.impl.UserServiceImpl;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserServiceImpl userService;

    public ResponseEntity<?> search(String username) {
        List<User> users = userRepository.findByUsernameContaining(username);

        return ResponseEntity.ok(users);
    }

    public ResponseEntity<?> create(UserDTO user) {
        // Check if username is empty
        if (user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        // Check if password is empty
        if (user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Check if roles is empty
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        // Check if user already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user.toUser());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public ResponseEntity<?> update(String username, UserDTO user) {
        // Check if username is empty
        if (user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        // Check if password is empty
        if (user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Check if roles is empty
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        existingUser.setRoles(user.getRoles());
        userRepository.save(existingUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<?> delete(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.delete(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<?> getAll() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<?> login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            // Generate JWT token
            String token = jwtTokenUtil.generateToken(userService.loadUserByUsername(username));

            return new ResponseEntity<>(token, HttpStatus.OK);
        }

        throw new UsernameNotFoundException("Invalid password");
    }
}
