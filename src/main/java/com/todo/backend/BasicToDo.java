package com.todo.backend;

import java.util.Date;

import com.todo.backend.ToDo.Priority;

/**
 * Basic Properties for Create and Update ToDo items.
 */
public class BasicToDo {
    // #region ################################ PROPERTIES
    private String text;
    private Priority priority;
    private Date due_date;
    // #endregion

    // #region ################################ GETTERS
    public String getText() {
        return text;
    }

    public Priority getPriority() {
        return priority;
    }

    public Date getDue_date() {
        return due_date;
    }
    // #endregion

    // #region ################################ SETTERS
    public void setText(String text) {
        if (text == null)
            throw new Error("Text field cannot be null");
        this.text = text;
    }

    public void setPriority(Priority priority) {
        if (priority == null)
            throw new Error("Priority field cannot be null");
        this.priority = priority;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }
    // #endregion
}
