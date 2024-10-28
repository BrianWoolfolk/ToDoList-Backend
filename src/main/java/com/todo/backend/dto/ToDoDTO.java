package com.todo.backend.dto;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
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
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Optional<Optional<Instant>> dueDate = Optional.empty();

    @Nullable
    private List<String> tags;

    @Nullable
    @Size(min = 1, max = 50, message = "Assigned user must be between 1 and 50 characters")
    @JsonProperty("assigned_user")
    private Optional<Optional<String>> assignedUser = Optional.empty();
}
