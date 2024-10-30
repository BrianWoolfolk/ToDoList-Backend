package com.todo.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo.backend.dto.ToDoDTO;
import com.todo.backend.model.FilterParams;
import com.todo.backend.model.SortParams;
import com.todo.backend.service.impl.ToDoServiceImpl;

import jakarta.validation.Valid;

@Validated
@RestController
@CrossOrigin(origins = "*")
public class ToDoController {

    @Autowired
    private ToDoServiceImpl toDoService;

    @Secured("ROLE_USER")
    @GetMapping("/todos")
    public ResponseEntity<?> getToDos(
            @ModelAttribute FilterParams searchParams,
            @ModelAttribute SortParams sortParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return toDoService.search(searchParams, sortParams, page, size);
    }

    @Secured("ROLE_USER")
    @GetMapping("/todos/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return toDoService.getById(id);
    }

    @Secured("ROLE_EDITOR")
    @PostMapping("/todos")
    public ResponseEntity<?> createToDo(@Valid @RequestBody ToDoDTO toDo) {
        return toDoService.create(toDo);
    }

    @Secured("ROLE_EDITOR")
    @PostMapping("/todos/{id}")
    public ResponseEntity<?> updateToDo(@PathVariable int id, @RequestBody ToDoDTO toDo) {
        return toDoService.update(id, toDo);
    }

    @Secured("ROLE_EDITOR")
    @PostMapping("/todos/{id}/done")
    public ResponseEntity<?> markDone(@PathVariable int id) {
        return toDoService.toggleDone(id, true);
    }

    @Secured("ROLE_EDITOR")
    @PutMapping("/todos/{id}/undone")
    public ResponseEntity<?> markUndone(@PathVariable int id) {
        return toDoService.toggleDone(id, false);
    }

    @Secured("ROLE_EDITOR")
    @PutMapping("/todos/markAll")
    public ResponseEntity<?> markAll(
            @ModelAttribute FilterParams searchParams,
            @ModelAttribute SortParams sortParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean done) {
        return toDoService.markAll(searchParams, sortParams, page, size, done);
    }

    @Secured("ROLE_EDITOR")
    @RequestMapping(method = { RequestMethod.DELETE }, value = { "/todos/{id}/delete" })
    public ResponseEntity<?> delete(@PathVariable int id) {
        return toDoService.delete(id);
    }
}
