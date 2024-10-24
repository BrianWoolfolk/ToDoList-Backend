package com.todo.backend.service.impl;

import com.todo.backend.model.GETResponse;
import com.todo.backend.model.Metrics.LastMetrics;
import com.todo.backend.model.ToDo;
import com.todo.backend.model.ToDo.Priority;
import com.todo.backend.repository.ToDoRepository;
import com.todo.backend.service.ToDoService;
import com.todo.backend.model.SearchParams;
import com.todo.backend.dto.ToDoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToDoServiceImpl implements ToDoService {

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private MetricsServiceImpl metricsService;

    public ResponseEntity<GETResponse> getToDos(SearchParams searchParams) {
        // Create sorting criteria
        Sort sort = Sort.by(Sort.Direction.ASC, "id"); // Default sorting by id
        if (searchParams.getSortPriority() != null) {
            sort = Sort.by(searchParams.getSortPriority() ? Sort.Direction.ASC : Sort.Direction.DESC, "priority");
        }
        if (searchParams.getSortDueDate() != null) {
            sort = sort
                    .and(Sort.by(searchParams.getSortDueDate() ? Sort.Direction.ASC : Sort.Direction.DESC, "dueDate"));
        }
        if (searchParams.getSortText() != null) {
            sort = sort.and(Sort.by(searchParams.getSortText() ? Sort.Direction.ASC : Sort.Direction.DESC, "text"));
        }
        if (searchParams.getSortCreationDate() != null) {
            sort = sort.and(Sort.by(searchParams.getSortCreationDate() ? Sort.Direction.ASC : Sort.Direction.DESC,
                    "creationDate"));
        }
        if (searchParams.getSortDoneDate() != null) {
            sort = sort.and(
                    Sort.by(searchParams.getSortDoneDate() ? Sort.Direction.ASC : Sort.Direction.DESC, "doneDate"));
        }

        // Create a Pageable object
        Pageable pageable = PageRequest.of(searchParams.getPage(), SearchParams.PAGE_SIZE, sort);

        // Fetch the filtered and sorted page of ToDo objects
        Page<ToDo> toDos = toDoRepository.findAll(pageable);

        // Filter the results based on searchParams
        List<ToDo> filteredList = toDos.getContent().stream()
                .filter(todo -> searchParams.getDone() == null || todo.isDone() == searchParams.getDone())
                .filter(todo -> searchParams.getText() == null || todo.getText().contains(searchParams.getText()))
                .filter(todo -> searchParams.getPriority() == null || todo.getPriority() == searchParams.getPriority())
                .collect(Collectors.toList());

        // Create a Metrics object
        LastMetrics metrics = metricsService.calculateMetrics(filteredList);

        // Create a GETResponse object
        GETResponse response = new GETResponse(filteredList, toDos.getNumber(), toDos.getTotalPages(),
                metrics);

        // Return a ResponseEntity with the response object and a status of OK
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getById(int id) {
        // Fetch the ToDo object with the given id
        ToDo toDo = toDoRepository.findById(id).orElse(null);

        // If the ToDo object is not found, return a ResponseEntity with a status of
        // NOT_FOUND
        if (toDo == null) {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }

        // Return a ResponseEntity with the ToDo object and a status of OK
        return new ResponseEntity<>(toDo, HttpStatus.OK);
    }

    public ResponseEntity<?> create(ToDoDTO toDoDTO) {
        // Create a new ToDo object from the ToDoDTO object
        ToDo toDo = new ToDo();
        toDo.setText(toDoDTO.getText());
        toDo.setPriority(toDoDTO.getPriority());
        toDo.setDueDate(toDoDTO.getDueDate());

        // Save the ToDo object to the database
        toDoRepository.save(toDo);

        // Return a ResponseEntity with the ToDo object and a status of CREATED
        return new ResponseEntity<>(toDo, HttpStatus.CREATED);
    }

    public ResponseEntity<?> update(int id, ToDoDTO toDoDTO) {
        // Fetch the ToDo object with the given id
        ToDo toDo = toDoRepository.findById(id).orElse(null);

        // If the ToDo object is not found, return a ResponseEntity with a status of
        // NOT_FOUND
        if (toDo == null) {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }

        // Update the ToDo object with the values from the ToDoDTO object
        toDo.setText(toDoDTO.getText());
        toDo.setPriority(toDoDTO.getPriority());
        toDo.setDueDate(toDoDTO.getDueDate());

        // Save the updated ToDo object to the database
        toDoRepository.save(toDo);

        // Return a ResponseEntity with the ToDo object and a status of OK
        return new ResponseEntity<>(toDo, HttpStatus.OK);
    }

    public ResponseEntity<?> delete(int id) {
        // Fetch the ToDo object with the given id
        ToDo toDo = toDoRepository.findById(id).orElse(null);

        // If the ToDo object is not found, return a ResponseEntity with a status of
        // NOT_FOUND
        if (toDo == null) {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }

        // Delete the ToDo object from the database
        toDoRepository.delete(toDo);

        // Return a ResponseEntity with a status of OK
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> toggleDone(int id, boolean done) {
        // Fetch the ToDo object with the given id
        ToDo toDo = toDoRepository.findById(id).orElse(null);

        // If the ToDo object is not found, return a ResponseEntity with a status of
        // NOT_FOUND
        if (toDo == null) {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }

        // Mark the ToDo object as done
        toDo.setDone(done);

        // Save the updated ToDo object to the database
        toDoRepository.save(toDo);

        // Return a ResponseEntity with the ToDo object and a status of OK
        return new ResponseEntity<>(toDo, HttpStatus.OK);
    }

    public ResponseEntity<?> searchToDos(String text, Boolean done, Priority priority) {
        // Fetch the filtered ToDo objects from the database
        List<ToDo> toDos = toDoRepository.searchToDos(text, done, priority);

        // Return a ResponseEntity with the filtered ToDo objects and a status of OK
        return new ResponseEntity<>(toDos, HttpStatus.OK);
    }

    public ResponseEntity<?> getMetrics() {
        // Fetch the last metrics object
        LastMetrics lastMetrics = metricsService.getLastMetrics();

        // Return a ResponseEntity with the last metrics object and a status of OK
        return new ResponseEntity<>(lastMetrics, HttpStatus.OK);
    }
}
