package com.todo.backend.model;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortParams {
    @Nullable
    private Boolean done;
    @Nullable
    private Boolean text;
    @Nullable
    private Boolean priority;
    @Nullable
    private Boolean dueDate;
    @Nullable
    private Boolean creationDate;
    @Nullable
    private Boolean doneDate;
    // @Nullable
    // private Boolean tags;
    @Nullable
    private Boolean assignedUser;
}
