package com.todo.backend;

import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;

import com.todo.backend.Metrics.LastMetrics;

@ResponseBody
public class GETResponse {
    // #region ################################ PROPERTIES
    final private List<ToDo> data;
    final private int page;
    final private int maxpage;
    final private LastMetrics metrics;
    // #endregion

    // #region ################################ CONSTRUCTOR
    public GETResponse(List<ToDo> data, int page, int maxpage, LastMetrics mLastMetrics) {
        this.data = data;
        this.page = page;
        this.maxpage = maxpage;
        this.metrics = mLastMetrics;
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

    public LastMetrics getLastMetrics() {
        return metrics;
    }
    // #endregion
}
