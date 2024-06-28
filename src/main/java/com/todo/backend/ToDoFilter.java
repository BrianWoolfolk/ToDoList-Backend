package com.todo.backend;

import com.todo.backend.ToDo.Priority;

/**
 * Properties for filtering To Do items.
 */
public class ToDoFilter {
    // #region ################################ PROPERTIES
    private int pag = 1;

    private Boolean filter_done = null;
    private String filter_text = null;
    private Priority filter_priority = null;

    // WIP SORTING DONE & PRIORITY
    // #endregion

    // #region ################################ GETTERS
    public int getPag() {
        return pag;
    }

    public Boolean getFilter_done() {
        return filter_done;
    }

    public String getFilter_text() {
        return filter_text;
    }

    public Priority getFilter_priority() {
        return filter_priority;
    }
    // #endregion

    // #region ################################ SETTERS
    public void setPag(int pag) {
        if (pag <= 0)
            throw new Error("Pagination begins at 1");

        this.pag = pag;
    }

    public void setFilter_done(Boolean filter_done) {
        this.filter_done = filter_done;
    }

    public void setFilter_text(String filter_text) {
        this.filter_text = filter_text;
    }

    public void setFilter_priority(Priority filter_priority) {
        this.filter_priority = filter_priority;
    }
    // #endregion
}
