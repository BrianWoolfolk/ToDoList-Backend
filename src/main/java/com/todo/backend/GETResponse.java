package com.todo.backend;

import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public class GETResponse {
    // #region ################################ PROPERTIES
    final private List<ToDo> data;
    final private int page;
    final private int maxpage;
    // #endregion

    // #region ################################ CONSTRUCTOR
    public GETResponse(List<ToDo> data, int page, int maxpage) {
        this.data = data;
        this.page = page;
        this.maxpage = maxpage;
    }
    // #endregion

    // #region ################################ GETTERS
    public List<ToDo> getData() {
        return data;
    }

    public int getMaxpage() {
        return maxpage;
    }

    public int getPage() {
        return page;
    }
    // #endregion
}
