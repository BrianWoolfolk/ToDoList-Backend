package com.todo.backend.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todo.backend.model.ToDo.Priority;
import lombok.Data;
import lombok.NonNull;

/**
 * Data Transfer Object for Create and Update ToDo items.
 */
@Data
public class ToDoDTO {
    @NonNull
    private String text;

    @NonNull
    private Priority priority;

    @JsonProperty("due_date")
    private Instant dueDate;
}