package com.todo.backend.model;

import java.util.List;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortParams {
    @Nullable
    private Boolean sortByDone;
    @Nullable
    private Boolean sortByText;
    @Nullable
    private Boolean sortByPriority;
    @Nullable
    private Boolean sortByDueDate;
    @Nullable
    private Boolean sortByCreationDate;
    @Nullable
    private Boolean sortByDoneDate;
    // @Nullable
    // private Boolean tags;
    @Nullable
    private Boolean sortByAssignedUser;
    @Nullable
    private List<String> sortOrder;
}
