package com.todo.backend;

import java.util.Date;

public class ToDo {
    // #region ################################ PROPERTIES
    /**
     * Priority of ToDo items. Mandatory.
     */
    enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

    /**
     * Auto-incremental ID. Cannot modify it, but delete the whole object.
     */
    final private int id;

    /**
     * ToDo List item's message. The main body.
     */
    private String text = null;

    /**
     * Due date if any. It serves not purpose other than 'importance' colors.
     */
    private Date due_date = null;

    /**
     * One of "LOW", "MEDIUM" or "HIGH". Required for filtering items.
     */
    private Priority priority = Priority.LOW;

    /**
     * Whether or not this item is "done". Could be replaced by 'done_date'.
     */
    private boolean done = false;

    /**
     * Date when this item was marked as done. It re-assings only when undone ->
     * done.
     */
    private Date done_date = null;

    /**
     * Date when the item was originally created. For performance-checking purposes.
     */
    final private Date creation_date;

    /**
     * Keeps track of ID's auto increment so it always is unique.
     */
    static protected int AUTO_INCREMENT = 0;
    // #endregion

    // #region ################################ CONSTRUCTOR
    public ToDo(String text, Priority priority, Date due_date) {
        this.id = AUTO_INCREMENT;
        this.text = text;
        this.priority = priority;
        this.due_date = due_date;
        this.creation_date = new Date();
        AUTO_INCREMENT++;
    }
    // #endregion

    // #region ################################ GETTERS
    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Priority getPriority() {
        return priority;
    }

    public Date getDue_date() {
        return due_date;
    }

    public Date getDone_date() {
        return done_date;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public boolean getDone() {
        return done;
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

    public void setDone(boolean done) {
        if (done ^ this.done)
            this.done_date = done ? new Date() : null;

        this.done = done;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }
    // #endregion

}
