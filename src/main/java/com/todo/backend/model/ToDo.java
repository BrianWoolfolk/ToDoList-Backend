package com.todo.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

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

    public void setDone(boolean done) {
        if (done != this.done) {
            this.done = done;
            this.doneDate = done ? Instant.now() : null;
        }
    }
}
