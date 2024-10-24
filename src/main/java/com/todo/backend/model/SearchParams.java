package com.todo.backend.model;

import com.todo.backend.model.ToDo.Priority;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchParams {
    @Nullable
    private Boolean done;
    @Nullable
    private String text;
    @Nullable
    private Priority priority;
    @Nullable
    private Boolean sortPriority;
    @Nullable
    private Boolean sortDueDate;
    @Nullable
    private Boolean sortText;
    @Nullable
    private Boolean sortCreationDate;
    @Nullable
    private Boolean sortDoneDate;
    private int page = 0; // Default page number
    public static final int PAGE_SIZE = 10; // Default page size
}
