package com.todo.backend.model;

import com.todo.backend.model.ToDo.Priority;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterParams {
    @Nullable
    private Boolean done;
    @Nullable
    private String text;
    @Nullable
    private Priority priority;
    @Nullable
    private Instant dueDateFrom;
    @Nullable
    private Instant dueDateTo;
    @Nullable
    private Instant creationDateFrom;
    @Nullable
    private Instant creationDateTo;
    @Nullable
    private Instant doneDateFrom;
    @Nullable
    private Instant doneDateTo;
    @Nullable
    private List<String> tags;
    @Nullable
    private String assignedUser;
}
