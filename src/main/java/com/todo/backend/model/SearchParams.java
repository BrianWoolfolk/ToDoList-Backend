package com.todo.backend.model;

import com.todo.backend.model.ToDo.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchParams {
    private Boolean done;
    private String text;
    private Priority priority;
    private Boolean sortPriority;
    private Boolean sortDueDate;
    private Boolean sortText;
    private Boolean sortCreationDate;
    private Boolean sortDoneDate;
    private int page = 0; // Default page number
    public static final int PAGE_SIZE = 10; // Default page size
}
