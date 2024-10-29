package com.todo.backend.service.impl;

import com.todo.backend.model.SortParams;
import com.todo.backend.model.Metrics.LastMetrics;
import com.todo.backend.model.ToDo;
import com.todo.backend.repository.ToDoRepository;
import com.todo.backend.service.ToDoService;
import com.todo.backend.model.FilterParams;
import com.todo.backend.model.GETResponse;
import com.todo.backend.dto.ToDoDTO;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ToDoServiceImpl implements ToDoService {

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private MetricsServiceImpl metricsService;

    // Search methods

    @Cacheable(value = "searchCache", key = "{#searchParams, #sortParams, #page, #size}")
    public ResponseEntity<?> search(FilterParams searchParams, SortParams sortParams, int page, int size) {
        // Fetch the ToDo objects from the database
        Page<ToDo> toDosPage = getToDosPage(searchParams, sortParams, page, size);

        // Get the Metrics object from the MetricsService
        LastMetrics metrics = getMetrics(searchParams);

        // Create GETResponse object with the ToDo objects, page, maxpage and metrics
        GETResponse toDosResponse = new GETResponse(toDosPage.getContent(), toDosPage.getNumber(),
                toDosPage.getTotalPages(), metrics);

        // Return a ResponseEntity with the GETResponse object and a status of OK
        return new ResponseEntity<>(toDosResponse, HttpStatus.OK);
    }

    @Cacheable(value = "sortCriteria", key = "#sortParams")
    private Sort buildSortCriteria(SortParams sortParams) {
        if (sortParams == null) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        List<Sort.Order> orders = new ArrayList<>();
        List<String> fields = sortParams.getSortOrder() == null ? new ArrayList<>() : sortParams.getSortOrder();
        fields.addAll(List.of("priority", "dueDate", "text", "creationDate", "doneDate", "done",
                "assignedUser"));
        Set<String> addedFields = new HashSet<>();

        for (String field : sortParams.getSortOrder()) {
            switch (field) {
                case "priority":
                    if (sortParams.getPriority() != null && addedFields.add("priority")) {
                        orders.add(new Sort.Order(sortParams.getPriority() ? Sort.Direction.ASC : Sort.Direction.DESC,
                                "priority"));
                    }
                    break;
                case "dueDate":
                    if (sortParams.getDueDate() != null && addedFields.add("dueDate")) {
                        orders.add(new Sort.Order(sortParams.getDueDate() ? Sort.Direction.ASC : Sort.Direction.DESC,
                                "dueDate"));
                    }
                    break;
                case "text":
                    if (sortParams.getText() != null && addedFields.add("text")) {
                        orders.add(new Sort.Order(sortParams.getText() ? Sort.Direction.ASC : Sort.Direction.DESC,
                                "text"));
                    }
                    break;
                case "creationDate":
                    if (sortParams.getCreationDate() != null && addedFields.add("creationDate")) {
                        orders.add(
                                new Sort.Order(sortParams.getCreationDate() ? Sort.Direction.ASC : Sort.Direction.DESC,
                                        "creationDate"));
                    }
                    break;
                case "doneDate":
                    if (sortParams.getDoneDate() != null && addedFields.add("doneDate")) {
                        orders.add(new Sort.Order(sortParams.getDoneDate() ? Sort.Direction.ASC : Sort.Direction.DESC,
                                "doneDate"));
                    }
                    break;
                case "done":
                    if (sortParams.getDone() != null && addedFields.add("done")) {
                        orders.add(new Sort.Order(sortParams.getDone() ? Sort.Direction.ASC : Sort.Direction.DESC,
                                "done"));
                    }
                    break;
                case "assignedUser":
                    if (sortParams.getAssignedUser() != null && addedFields.add("assignedUser")) {
                        orders.add(
                                new Sort.Order(sortParams.getAssignedUser() ? Sort.Direction.ASC : Sort.Direction.DESC,
                                        "assignedUser"));
                    }
                    break;
                default:
                    throw new ValidationErrorException("Invalid sort field: " + field);
            }
        }

        if (orders.isEmpty()) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "id"));
        }

        return Sort.by(orders);
    }

    @Cacheable(value = "searchCache", key = "#id")
    public ResponseEntity<ToDo> getById(int id) {
        ToDo toDo = toDoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found"));

        return new ResponseEntity<>(toDo, HttpStatus.OK);
    }

    // CRUD methods

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

        // Check if the assigned user is a valid user or null
        if (!toDoDTO.getAssignedUser().isPresent()) {
            throw new ValidationErrorException("Assigned user must be null or a valid user");
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

        // Handle assigned user
        String assignedUser = null;
        if (toDoDTO.getAssignedUser().isPresent()) {
            Optional<String> assignedUserOptional = toDoDTO.getAssignedUser().get();
            if (assignedUserOptional.isPresent()) {
                assignedUser = assignedUserOptional.get();
                if (assignedUser.length() < 1 || assignedUser.length() > 50) {
                    throw new ValidationErrorException("Assigned user must be between 1 and 50 characters");
                }
            }
        }

        // Create a new ToDo object from the ToDoDTO object
        ToDo toDo = new ToDo();
        toDo.setText(toDoDTO.getText());
        toDo.setPriority(toDoDTO.getPriority());
        toDo.setDueDate(dueDate);
        toDo.setTags(toDoDTO.getTags() == null ? new ArrayList<>() : toDoDTO.getTags());
        toDo.setAssignedUser(assignedUser);

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

        // Check if the tags are present to update
        if (toDoDTO.getTags() != null) {
            toDo.setTags(toDoDTO.getTags());
        }

        // Handle assigned user
        if (toDoDTO.getAssignedUser().isPresent()) {
            Optional<String> assignedUserOptional = toDoDTO.getAssignedUser().get();
            if (assignedUserOptional.isPresent()) {
                String assignedUser = assignedUserOptional.get();
                if (assignedUser.length() < 1 || assignedUser.length() > 50) {
                    throw new ValidationErrorException("Assigned user must be between 1 and 50 characters");
                }
                toDo.setAssignedUser(assignedUser);
            } else {
                toDo.setAssignedUser(null);
            }
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

    @CacheEvict(value = "searchCache", allEntries = true)
    public ResponseEntity<?> markAll(
            FilterParams searchParams,
            SortParams sortParams,
            int page,
            int size,
            boolean done) {
        // Fetch the ToDo objects from the database
        Page<ToDo> toDosPage = getToDosPage(searchParams, sortParams, page, size);

        // Get the ToDo objects from the Page object
        List<ToDo> toDos = toDosPage.getContent();

        // Mark all the ToDo objects as done
        for (ToDo toDo : toDos) {
            toDo.setDone(done);
        }

        // Save the updated ToDo objects to the database
        toDoRepository.saveAll(toDos);

        // Return a ResponseEntity with a status of OK
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public Page<ToDo> getToDosPage(FilterParams searchParams, SortParams sortParams, int page, int size) {
        if (page < 0 || size < 1) {
            throw new ValidationErrorException(
                    "Page must be greater than or equal to 0 and size must be greater than 0");
        }

        if (searchParams == null) {
            searchParams = new FilterParams();
        }

        // Build the sort criteria from the SortParams object
        Sort sort = buildSortCriteria(sortParams);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch the filtered ToDo objects from the database with pagination
        Page<ToDo> toDosPage = toDoRepository.searchToDos(
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
                pageable);

        return toDosPage;
    }

    public LastMetrics getMetrics(FilterParams searchParams) {
        if (searchParams == null) {
            searchParams = new FilterParams();
        }

        // Fetch all the ToDo objects from the database
        List<ToDo> toDos = toDoRepository.searchAllToDos(
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
                searchParams.getAssignedUser());
        metricsService.calculateMetrics(toDos);

        // Fetch the last metrics object
        LastMetrics lastMetrics = metricsService.getLastMetrics();

        // Return the last metrics object
        return lastMetrics;
    }
}
