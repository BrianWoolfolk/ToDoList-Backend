package com.todo.backend.service.impl;

import com.todo.backend.dto.UserDTO;
import com.todo.backend.model.User;
import com.todo.backend.repository.UserRepository;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        throw new UsernameNotFoundException("Invalid password");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }

    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> search(String username) {
        return ResponseEntity.ok(userRepository.findByUsernameContaining(username));
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
}
