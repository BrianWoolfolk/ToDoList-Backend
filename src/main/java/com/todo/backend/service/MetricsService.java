package com.todo.backend.service;

import java.util.List;

import com.todo.backend.model.Metrics;
import com.todo.backend.model.ToDo;

public interface MetricsService {
    Metrics calculateMetrics(List<ToDo> todos);
}
