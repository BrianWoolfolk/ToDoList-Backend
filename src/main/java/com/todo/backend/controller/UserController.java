package com.todo.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo.backend.dto.UserDTO;
import com.todo.backend.service.impl.UserServiceImpl;

import jakarta.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO.getUsername(), userDTO.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return userService.logout();
    }

    @Secured("ROLE_EDITOR")
    @GetMapping("/users")
    public ResponseEntity<?> user(@RequestParam(defaultValue = "") String username) {
        return userService.search(username);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO user) {
        return userService.create(user);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/users/update")
    public ResponseEntity<?> updateUser(@RequestParam String username, @RequestBody UserDTO user) {
        return userService.update(username, user);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(method = { RequestMethod.DELETE }, value = "/user/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String username) {
        return userService.delete(username);
    }
}