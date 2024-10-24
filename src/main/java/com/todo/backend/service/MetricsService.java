package com.todo.backend.service;

import java.util.List;

import com.todo.backend.model.ToDo;
import com.todo.backend.model.Metrics.LastMetrics;

public interface MetricsService {
    LastMetrics calculateMetrics(List<ToDo> todos);

    LastMetrics getLastMetrics();
}
