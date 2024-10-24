package com.todo.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.todo.backend.dto.ToDoDTO;
import com.todo.backend.model.SearchParams;
import com.todo.backend.service.impl.ToDoServiceImpl;

import jakarta.validation.Valid;

@Validated
@RestController
@CrossOrigin(origins = "*")
public class ToDoController {

    @Autowired
    private ToDoServiceImpl toDoService;

    // #region ################################ GET
    @GetMapping("/todos")
    public ResponseEntity<?> getToDos(SearchParams searchParams) {
        return toDoService.search(searchParams);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return toDoService.getById(id);
    }
    // #endregion

    // #region ################################ POST
    @PostMapping("/todos")
    public ResponseEntity<?> createToDo(@Valid @RequestBody ToDoDTO toDo) {
        return toDoService.create(toDo);
    }

    @PostMapping("/todos/{id}")
    public ResponseEntity<?> updateToDo(@PathVariable int id, @Valid @RequestBody ToDoDTO toDo) {
        return toDoService.update(id, toDo);
    }
    // #endregion

    // #region ################################ DONE/UNDONE
    @PostMapping("/todos/{id}/done")
    public ResponseEntity<?> markDone(@PathVariable int id) {
        return toDoService.toggleDone(id, true);
    }

    @PutMapping("/todos/{id}/undone")
    public ResponseEntity<?> markUndone(@PathVariable int id) {
        return toDoService.toggleDone(id, false);
    }
    // #endregion

    // #region ################################ DELETE
    @RequestMapping(method = { RequestMethod.DELETE }, value = { "/todos/{id}/delete" })
    public ResponseEntity<?> delete(@PathVariable int id) {
        return toDoService.delete(id);
    }
    // #endregion
}
