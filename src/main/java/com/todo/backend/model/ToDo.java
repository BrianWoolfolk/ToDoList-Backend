package com.todo.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ToDo {
    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    private String text;

    @NonNull
    private Priority priority;

    @JsonProperty("due_date")
    private Instant dueDate;

    @JsonProperty("creation_date")
    private final Instant creationDate = Instant.now();

    private boolean done = false;

    @JsonProperty("done_date")
    private Instant doneDate;

    @NonNull
    private List<String> tags;

    @Nullable
    @Size(min = 1, max = 50, message = "Assigned user must be between 1 and 50 characters")
    @JsonProperty("assigned_user")
    private String assignedUser;

    public void setDone(boolean done) {
        if (done != this.done) {
            this.done = done;
            this.doneDate = done ? Instant.now() : null;
        }
    }
}
