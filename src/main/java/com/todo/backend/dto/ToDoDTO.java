package com.todo.backend.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todo.backend.model.ToDo.Priority;
import lombok.Data;

/**
 * Data Transfer Object for Create and Update ToDo items.
 */
@Data
public class ToDoDTO {
    private String text;

    private Priority priority;

    @JsonProperty("due_date")
    private Instant dueDate;
}
