package com.todo.backend.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todo.backend.model.ToDo.Priority;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ToDoDTO {
    @Nullable
    @Size(min = 1, max = 120, message = "Text must be between 1 and 120 characters")
    private String text;

    @Nullable
    private Priority priority;

    @Nullable
    @JsonProperty("due_date")
    private Instant dueDate;
}
