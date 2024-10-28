package com.todo.backend.service.impl;

import com.todo.backend.model.GETResponse;
import com.todo.backend.model.SortParams;
import com.todo.backend.model.Metrics.LastMetrics;
import com.todo.backend.model.ToDo;
import com.todo.backend.repository.ToDoRepository;
import com.todo.backend.service.ToDoService;
import com.todo.backend.model.FilterParams;
import com.todo.backend.dto.ToDoDTO;
import com.todo.backend.exception.ClientErrorException;
import com.todo.backend.exception.DatabaseErrorException;
import com.todo.backend.exception.ValidationErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ToDoServiceImpl implements ToDoService {

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private MetricsServiceImpl metricsService;

    @Cacheable(value = "searchCache", key = "#searchParams")
    public ResponseEntity<GETResponse> search(FilterParams filterParams, SortParams sortParams) {
        Page<ToDo> toDos = getPage(filterParams, sortParams);

        List<ToDo> filteredList = toDos.getContent().stream()
                .filter(todo -> filterParams.getDone() == null || todo.isDone() == filterParams.getDone())
                .filter(todo -> filterParams.getText() == null || todo.getText().contains(filterParams.getText()))
                .filter(todo -> filterParams.getPriority() == null || todo.getPriority() == filterParams.getPriority())
                .collect(Collectors.toList());

        LastMetrics metrics = metricsService.calculateMetrics(filteredList);
        GETResponse response = new GETResponse(filteredList, toDos.getNumber(), toDos.getTotalPages(), metrics);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Page<ToDo> getPage(FilterParams filterParams, SortParams sortParams) {
        if (filterParams == null) {
            throw new ClientErrorException("Invalid search parameters");
        }

        Sort sort = buildSortCriteria(sortParams);
        Pageable pageable = PageRequest.of(filterParams.getPage(), FilterParams.PAGE_SIZE, sort);

        Page<ToDo> toDos;
        try {
            toDos = toDoRepository.findAll(pageable);
        } catch (Exception e) {
            throw new DatabaseErrorException("Error fetching ToDo items");
        }

        return toDos;
    }

    private Sort buildSortCriteria(SortParams sortParams) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        if (sortParams == null) {
            return sort;
        }
        if (sortParams.getPriority() != null) {
            sort = Sort.by(sortParams.getPriority() ? Sort.Direction.ASC : Sort.Direction.DESC, "priority");
        }
        if (sortParams.getDueDate() != null) {
            sort = sort
                    .and(Sort.by(sortParams.getDueDate() ? Sort.Direction.ASC : Sort.Direction.DESC, "dueDate"));
        }
        if (sortParams.getText() != null) {
            sort = sort.and(Sort.by(sortParams.getText() ? Sort.Direction.ASC : Sort.Direction.DESC, "text"));
        }
        if (sortParams.getCreationDate() != null) {
            sort = sort.and(Sort.by(sortParams.getCreationDate() ? Sort.Direction.ASC : Sort.Direction.DESC,
                    "creationDate"));
        }
        if (sortParams.getDoneDate() != null) {
            sort = sort.and(
                    Sort.by(sortParams.getDoneDate() ? Sort.Direction.ASC : Sort.Direction.DESC, "doneDate"));
        }
        if (sortParams.getDone() != null) {
            sort = sort.and(Sort.by(sortParams.getDone() ? Sort.Direction.ASC : Sort.Direction.DESC, "done"));
        }
        if (sortParams.getAssignedUser() != null) {
            sort = sort.and(Sort.by(sortParams.getAssignedUser() ? Sort.Direction.ASC : Sort.Direction.DESC,
                    "assignedUser"));
        }
        return sort;
    }

    @Cacheable(value = "searchCache", key = "#id")
    public ResponseEntity<ToDo> getById(int id) {
        ToDo toDo = toDoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found"));

        return new ResponseEntity<>(toDo, HttpStatus.OK);
    }

    @CacheEvict(value = "searchCache", allEntries = true)
    public ResponseEntity<?> create(ToDoDTO toDoDTO) {
        // Check validation constraints
        if (toDoDTO.getText() == null || toDoDTO.getText().length() < 1 || toDoDTO.getText().length() > 120) {
            throw new ValidationErrorException("Text must be between 1 and 120 characters");
        }

        // Check if the priority is valid
        if (toDoDTO.getPriority() == null) {
            throw new ValidationErrorException("Priority must be specified");
        }

        // Check if the due date is a valid date or null
        if (!toDoDTO.getDueDate().isPresent()) {
            throw new ValidationErrorException("Due date must be null or a valid date");
        }

        // Handle due date
        Instant dueDate = null;
        if (toDoDTO.getDueDate().isPresent()) {
            Optional<Instant> dueDateOptional = toDoDTO.getDueDate().get();
            if (dueDateOptional.isPresent()) {
                dueDate = dueDateOptional.get();
                if (dueDate.isBefore(Instant.now())) {
                    throw new ValidationErrorException("Due date must be in the future");
                }
            }
        }

        // Create a new ToDo object from the ToDoDTO object
        ToDo toDo = new ToDo();
        toDo.setText(toDoDTO.getText());
        toDo.setPriority(toDoDTO.getPriority());
        toDo.setDueDate(dueDate);

        // Save the ToDo object to the database
        toDoRepository.save(toDo);

        // Return a ResponseEntity with the ToDo object and a status of CREATED
        return new ResponseEntity<>(toDo, HttpStatus.CREATED);
    }

    @CacheEvict(value = "searchCache", allEntries = true)
    public ResponseEntity<?> update(int id, ToDoDTO toDoDTO) {
        // Fetch the ToDo object with the given id
        ToDo toDo = toDoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found"));

        // Update the ToDo object with the values from the ToDoDTO object
        // Check if the text is present to update
        if (toDoDTO.getText() != null) {
            // Check validation constraints
            if (toDoDTO.getText().length() < 1 || toDoDTO.getText().length() > 120) {
                throw new ValidationErrorException("Text must be between 1 and 120 characters");
            }

            toDo.setText(toDoDTO.getText());
        }

        // Check if the priority is present to update
        if (toDoDTO.getPriority() != null) {
            toDo.setPriority(toDoDTO.getPriority());
        }

        // Handle due date
        if (toDoDTO.getDueDate().isPresent()) {
            Optional<Instant> dueDateOptional = toDoDTO.getDueDate().get();
            if (dueDateOptional.isPresent()) {
                Instant dueDate = dueDateOptional.get();
                if (dueDate.isBefore(Instant.now())) {
                    throw new ValidationErrorException("Due date must be in the future");
                }
                toDo.setDueDate(dueDate);
            } else {
                toDo.setDueDate(null);
            }
        }

        // Save the updated ToDo object to the database
        toDoRepository.save(toDo);

        // Return a ResponseEntity with the ToDo object and a status of OK
        return new ResponseEntity<>(toDo, HttpStatus.OK);
    }

    @CacheEvict(value = "searchCache", allEntries = true)
    public ResponseEntity<?> delete(int id) {
        // Fetch the ToDo object with the given id
        ToDo toDo = toDoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found"));

        // Delete the ToDo object from the database
        toDoRepository.delete(toDo);

        // Return a ResponseEntity with a status of OK
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CacheEvict(value = "searchCache", allEntries = true)
    public ResponseEntity<?> toggleDone(int id, boolean done) {
        // Fetch the ToDo object with the given id
        ToDo toDo = toDoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found"));

        // Mark the ToDo object as done
        toDo.setDone(done);

        // Save the updated ToDo object to the database
        toDoRepository.save(toDo);

        // Return a ResponseEntity with the ToDo object and a status of OK
        return new ResponseEntity<>(toDo, HttpStatus.OK);
    }

    // Aditional methods

    public ResponseEntity<?> searchToDos(FilterParams searchParams, SortParams sortParams) {
        Sort sort = buildSortCriteria(sortParams);

        // Fetch the filtered ToDo objects from the database
        List<ToDo> toDos = toDoRepository.searchToDos(
                searchParams.getText(),
                searchParams.getDone(),
                searchParams.getPriority(),
                searchParams.getDueDateFrom(),
                searchParams.getDueDateTo(),
                searchParams.getCreationDateFrom(),
                searchParams.getCreationDateTo(),
                searchParams.getDoneDateFrom(),
                searchParams.getDoneDateTo(),
                searchParams.getTags(),
                searchParams.getAssignedUser(),
                sort);

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
